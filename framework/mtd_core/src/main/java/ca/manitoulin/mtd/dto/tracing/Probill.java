package ca.manitoulin.mtd.dto.tracing;

import static ca.manitoulin.mtd.code.DocumentType.valueByCode;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.leftPad;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.manitoulin.mtd.code.DocumentType;
import ca.manitoulin.mtd.dto.AbstractDTO;
import ca.manitoulin.mtd.dto.support.Attachment;
import ca.manitoulin.mtd.util.DateUtil;

public class Probill extends AbstractDTO {

	// List fields
	private String probillNumber;
	private String pickupDate;
	private String shipper;
	private String shipperCity;
	private String shipperProvince;
	private String shipperZip;
	private String consignee;
	private String consigneeCity;
	private String consigneeProvince;
	private String consigneeZip;
	private String deliveryDate;
	private String deliveryTime;
	private String bolNumber;
	private String poNumber;
	private String weight;
	private String adjWeight;
	private String receivedBy;
	private String terms;

	// Details fields
	private String pieces;
	private String deliveryAppointment; // Not a data mapping field
	private String deliveryApptDate;
	private String deliveryApptTime;
	private String deliverySpecialTips; // Not a data mapping field
	private String deliveryCode;
	private String deliveryCodeDesp; // Not a data mapping field
	private String shipperNumber;
	private String cargoControlNumber;
	private String transferTo;
	private String toCarrierETA;
	private String etaDate;
	private String probillETA;
	private String calculatedETA; // Not a data mapping field
	private String status;
	private String guaranteedDeliveryCode;
	private String consigneeCountry;
	private String terminalId;
	private String rescheduledDate;
	private String rescheduledReasonCode;
	private String rescheduledReason; // Not a data mapping field

	// Discount info
	private BigDecimal fromCarrierDiscountPercentage;
	private BigDecimal toCarrierDiscountPercentage;
	private BigDecimal fromCarrierRevenue;
	private BigDecimal toCarrierRevenue;
	private BigDecimal discountAmount;
	private BigDecimal discountPercentage;
	private String transferedFromCarrier;
	private String transferedToCarrier;

	// pieces section
	private List<PackingPiece> piecesList;

	// image
	private List<Attachment> images;

	// Manifest section
	private List<ManifestInfo> manifestList;
	// partner section
	private Boolean showPartnerSection;
	private String interlineFrom;
	private String interlineTo;
	private String revenue;
	private String discount;
	private List<PartnerChargeInfo> partnerList;

	public List<List<Attachment>> getImageGroup() {
		if (null == this.images || 0 == this.images.size()) {
			return null;
		} else {
			List<List<Attachment>> imageGroup = new ArrayList<List<Attachment>>();
			// Set<DocumentType> typeSet = new HashSet<DocumentType>();
			List<DocumentType> typeList = new ArrayList<DocumentType>();
			// get the unique type set
			for (Attachment attachment : this.images) {
				DocumentType type = valueByCode(attachment.getFileType());
				if (!typeList.contains(type)) {
					typeList.add(type);
				}
				continue;
			}
			// Sort the group
			Collections.sort(typeList, new Comparator<DocumentType>() {
				@Override
				public int compare(DocumentType type1, DocumentType type2) {
					return type1.toString().compareTo(type2.toString());
				}
			});
			// group the images by type set
			for (DocumentType type : typeList) {
				List<Attachment> sameTypeList = new ArrayList<Attachment>();
				for (Attachment attachment : this.images) {
					if (type.equals(valueByCode(attachment.getFileType()))) {
						sameTypeList.add(attachment);
					}
				}
				imageGroup.add(sameTypeList);
			}
			return imageGroup;
		}
	}

	public String getDeliveryOnLabel() {
		if (isNotBlank(getFormattedDeliveryDate()) && isNotBlank(getFormattedDeliveryTime())) {
			return "Delivered on:";
		} else {
			return "ETA:";
		}
	}

	public String getDeliveryApptLabel() {
		if (isNotBlank(this.getRescheduledReason())) {
			return "Rescheduled Date/Reason:";
		} else {
			return "Delivery Appt:";
		}
	}

	public String getDeliveryApptValue() {
		if (isNotBlank(this.getRescheduledReason())) {
			return this.getRescheduledReason();
		} else {
			return this.getDeliveryAppointment();
		}
	}

	public String getDeliveryOn() {
		if (isNotBlank(getFormattedDeliveryDate()) && isNotBlank(getFormattedDeliveryTime())) {
			return getFormattedDeliveryDate() + " at " + getFormattedDeliveryTime();
		} else {
			return this.getCalculatedETA();
		}

	}

	public String getGuaranteedDelivery() {
		String code = StringUtils.trimToEmpty(this.guaranteedDeliveryCode);
		switch (code) {
		case "A":
			return "Guaranteed delivery by noon";
		case "P":
			return "Guaranteed delivery by 4 pm";
		default:
			return EMPTY;
		}
	}

	/**
	 * Prefix "0" to probill number
	 * 
	 * @return
	 */
	public String getFormattedProbillNumber() {
		return leftPad(probillNumber, 10, "0");
	}

	public String getFormattedPickupDate() {
		return DateUtil.displayDBDate(pickupDate);

	}

	public String getFormattedDeliveryApptDate() {
		return DateUtil.displayDBDate(deliveryApptDate);

	}

	public String getFormattedDeliveryApptTime() {
		return DateUtil.displayDBTime(deliveryApptTime);
	}

	public String getFormattedDeliveryDate() {
		return DateUtil.displayDBDate(deliveryDate);

	}

	public String getFormattedDeliveryTime() {
		return DateUtil.displayDBTime(deliveryTime);
	}

	public String getProbillNumber() {
		return probillNumber;
	}

	public void setProbillNumber(String probillNumber) {
		this.probillNumber = probillNumber;
	}

	public String getShipper() {
		return shipper;
	}

	public void setShipper(String shipper) {
		this.shipper = shipper;
	}

	public String getBolNumber() {
		return bolNumber;
	}

	public void setBolNumber(String bolNumber) {
		this.bolNumber = bolNumber;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getAdjWeight() {
		return adjWeight;
	}

	public void setAdjWeight(String adjWeight) {
		this.adjWeight = adjWeight;
	}

	public String getReceivedBy() {
		return receivedBy;
	}

	public void setReceivedBy(String receivedBy) {
		this.receivedBy = receivedBy;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public List<PackingPiece> getPiecesList() {
		return piecesList;
	}

	public void setPiecesList(List<PackingPiece> piecesList) {
		this.piecesList = piecesList;
	}

	public String getShipperCity() {
		return shipperCity;
	}

	public void setShipperCity(String shipperCity) {
		this.shipperCity = shipperCity;
	}

	public String getShipperProvince() {
		return shipperProvince;
	}

	public void setShipperProvince(String shipperProvince) {
		this.shipperProvince = shipperProvince;
	}

	public String getShipperZip() {
		return shipperZip;
	}

	public void setShipperZip(String shipperZip) {
		this.shipperZip = shipperZip;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getConsigneeCity() {
		return consigneeCity;
	}

	public void setConsigneeCity(String consigneeCity) {
		this.consigneeCity = consigneeCity;
	}

	public String getConsigneeProvince() {
		return consigneeProvince;
	}

	public void setConsigneeProvince(String consigneeProvince) {
		this.consigneeProvince = consigneeProvince;
	}

	public String getConsigneeZip() {
		return consigneeZip;
	}

	public void setConsigneeZip(String consigneeZip) {
		this.consigneeZip = consigneeZip;
	}

	public String getPieces() {
		return pieces;
	}

	public void setPieces(String pieces) {
		this.pieces = pieces;
	}

	public String getDeliveryCodeLabel() {
		if (isNotBlank(this.getDeliverySpecialTips())) {
			return EMPTY;
		} else {
			return "Delivery Code:";
		}
	}

	public String getDeliveryCodeValue() {
		if (isNotBlank(this.getDeliverySpecialTips())) {
			return this.getDeliverySpecialTips();
		} else {
			return this.getDeliveryCode();
		}
	}

	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

	public String getShipperNumber() {
		return shipperNumber;
	}

	public void setShipperNumber(String shipperNumber) {
		this.shipperNumber = shipperNumber;
	}

	public String getCargoControlNumber() {
		return cargoControlNumber;
	}

	public void setCargoControlNumber(String cargoControlNumber) {
		this.cargoControlNumber = cargoControlNumber;
	}

	public String getDeliveryCodeDesp() {
		return deliveryCodeDesp;
	}

	public void setDeliveryCodeDesp(String deliveryCodeDesp) {
		this.deliveryCodeDesp = deliveryCodeDesp;
	}

	public String getPickupDate() {
		return pickupDate;
	}

	public void setPickupDate(String pickupDate) {
		this.pickupDate = pickupDate;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public List<Attachment> getImages() {
		return images;
	}

	public void setImages(List<Attachment> images) {
		this.images = images;
	}

	public List<ManifestInfo> getManifestList() {
		return manifestList;
	}

	public void setManifestList(List<ManifestInfo> manifestList) {
		this.manifestList = manifestList;
	}

	public String getInterlineLabel() {
		if (isNotBlank(this.getInterlineFrom())) {
			return "Interline From:";
		} else if (isNotBlank(this.getInterlineTo())) {
			return "Interline To:";
		}
		return EMPTY;
	}

	public String getInterlineValue() {
		if (isNotBlank(this.getInterlineFrom())) {
			return this.getInterlineFrom();
		} else if (isNotBlank(this.getInterlineTo())) {
			return this.getInterlineTo();
		}
		return EMPTY;
	}

	public String getInterlineFrom() {
		return interlineFrom;
	}

	public void setInterlineFrom(String interlineFrom) {
		this.interlineFrom = interlineFrom;
	}

	public String getRevenue() {
		return revenue;
	}

	public void setRevenue(String revenue) {
		this.revenue = revenue;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public List<PartnerChargeInfo> getPartnerList() {
		return partnerList;
	}

	public void setPartnerList(List<PartnerChargeInfo> partnerList) {
		this.partnerList = partnerList;
	}

	public String getTransferTo() {
		return transferTo;
	}

	public void setTransferTo(String transferTo) {
		this.transferTo = transferTo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGuaranteedDeliveryCode() {
		return guaranteedDeliveryCode;
	}

	public void setGuaranteedDeliveryCode(String guaranteedDeliveryCode) {
		this.guaranteedDeliveryCode = guaranteedDeliveryCode;
	}

	public String getConsigneeCountry() {
		return consigneeCountry;
	}

	public void setConsigneeCountry(String consigneeCountry) {
		this.consigneeCountry = consigneeCountry;
	}

	public BigDecimal getFromCarrierDiscountPercentage() {
		return fromCarrierDiscountPercentage;
	}

	public void setFromCarrierDiscountPercentage(BigDecimal fromCarrierDiscountPercentage) {
		this.fromCarrierDiscountPercentage = fromCarrierDiscountPercentage;
	}

	public BigDecimal getToCarrierDiscountPercentage() {
		return toCarrierDiscountPercentage;
	}

	public void setToCarrierDiscountPercentage(BigDecimal toCarrierDiscountPercentage) {
		this.toCarrierDiscountPercentage = toCarrierDiscountPercentage;
	}

	public BigDecimal getFromCarrierRevenue() {
		return fromCarrierRevenue;
	}

	public void setFromCarrierRevenue(BigDecimal fromCarrierRevenue) {
		this.fromCarrierRevenue = fromCarrierRevenue;
	}

	public BigDecimal getToCarrierRevenue() {
		return toCarrierRevenue;
	}

	public void setToCarrierRevenue(BigDecimal toCarrierRevenue) {
		this.toCarrierRevenue = toCarrierRevenue;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public Boolean getShowPartnerSection() {
		return showPartnerSection;
	}

	public void setShowPartnerSection(Boolean showPartnerSection) {
		this.showPartnerSection = showPartnerSection;
	}

	public String getCalculatedETA() {
		return calculatedETA;
	}

	public void setCalculatedETA(String calculatedETA) {
		this.calculatedETA = calculatedETA;
	}

	public String getRescheduledDate() {
		return rescheduledDate;
	}

	public void setRescheduledDate(String rescheduledDate) {
		this.rescheduledDate = rescheduledDate;
	}

	public String getProbillETA() {
		return probillETA;
	}

	public void setProbillETA(String probillETA) {
		this.probillETA = probillETA;
	}

	public String getToCarrierETA() {
		return toCarrierETA;
	}

	public void setToCarrierETA(String toCarrierETA) {
		this.toCarrierETA = toCarrierETA;
	}

	public String getEtaDate() {
		return etaDate;
	}

	public void setEtaDate(String etaDate) {
		this.etaDate = etaDate;
	}

	public String getRescheduledReasonCode() {
		return rescheduledReasonCode;
	}

	public void setRescheduledReasonCode(String rescheduledReasonCode) {
		this.rescheduledReasonCode = rescheduledReasonCode;
	}

	public String getRescheduledReason() {
		return rescheduledReason;
	}

	public void setRescheduledReason(String rescheduledReason) {
		this.rescheduledReason = rescheduledReason;
	}

	public String getDeliveryApptDate() {
		return deliveryApptDate;
	}

	public void setDeliveryApptDate(String deliveryApptDate) {
		this.deliveryApptDate = deliveryApptDate;
	}

	public String getDeliveryApptTime() {
		return deliveryApptTime;
	}

	public void setDeliveryApptTime(String deliveryApptTime) {
		this.deliveryApptTime = deliveryApptTime;
	}

	public String getDeliveryAppointment() {
		return deliveryAppointment;
	}

	public void setDeliveryAppointment(String deliveryAppointment) {
		this.deliveryAppointment = deliveryAppointment;
	}

	public String getDeliverySpecialTips() {
		return deliverySpecialTips;
	}

	public void setDeliverySpecialTips(String deliverySpecialTips) {
		this.deliverySpecialTips = deliverySpecialTips;
	}

	public String getTransferedFromCarrier() {
		return transferedFromCarrier;
	}

	public void setTransferedFromCarrier(String transferedFromCarrier) {
		this.transferedFromCarrier = transferedFromCarrier;
	}

	public String getTransferedToCarrier() {
		return transferedToCarrier;
	}

	public void setTransferedToCarrier(String transferedToCarrier) {
		this.transferedToCarrier = transferedToCarrier;
	}

	public String getInterlineTo() {
		return interlineTo;
	}

	public void setInterlineTo(String interlineTo) {
		this.interlineTo = interlineTo;
	}
}
