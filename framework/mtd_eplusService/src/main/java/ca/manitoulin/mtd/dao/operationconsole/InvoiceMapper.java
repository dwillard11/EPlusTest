package ca.manitoulin.mtd.dao.operationconsole;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.manitoulin.mtd.dto.operationconsole.Invoice;
import ca.manitoulin.mtd.dto.operationconsole.InvoiceDetail;

public interface InvoiceMapper {
	
    //List<Invoice> selectInvoiceByTrip(Integer tripId);
    
    Invoice selectInvoiceById(Integer id);
    
    void insertInvoice(Invoice invoice);
    
    void updateInvoice(Invoice invoice);
    
    void deleteInvoice(Integer id);
    
    List<InvoiceDetail> selectInvoiceDetails(@Param("id")Integer invoiceId, @Param("language") String lang);
    
    void insertInvoiceDetail(InvoiceDetail detail);
    
    void deleteAllInvoiceDetails(Integer invoiceId);
    
    List<Invoice> selectInvoices(
    		@Param("tripId")Integer tripId,
    		@Param("departmentIdList") List<Integer> departmentIdList,
    		@Param("departmentId")Integer depId, 
    		@Param("dateFrom") Date dateFrom, 
    		@Param("dateTo") Date dateTo,
    		@Param("language") String language);
}
