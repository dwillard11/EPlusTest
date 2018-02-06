package ca.manitoulin.mtd.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UnitConversionHelper {
	
	private static final String KG = "KG";
	private static final String LBS = "LBS";
	private static final float LBS_EQUALS_1_KG = 2.21f;
	
	public static BigDecimal toLBS(BigDecimal input, String inputUnit){
		
		if(inputUnit == null) return BigDecimal.ZERO;
		
		switch(StringUtils.upperCase(inputUnit)){
		case KG: return kgToLBS(input);
		case LBS: return input;
		default: return BigDecimal.ZERO;
		}
	}
	
	public static BigDecimal inchToFeet(BigDecimal input, String unit){
		if(input == null) return BigDecimal.ZERO;
		switch(StringUtils.upperCase(unit)){
		case "INCH": 
			return input.multiply(new BigDecimal(0.0833f)).setScale(2, RoundingMode.HALF_EVEN);
		case "CM": 
			return input.multiply(new BigDecimal(0.0328f)).setScale(2, RoundingMode.HALF_EVEN);
		default: return BigDecimal.ZERO;
		}
		
	}
	
	public static BigDecimal inchToCM(BigDecimal inches){
		if(inches == null) return null;
		return inches.multiply(new BigDecimal(2.54f)).setScale(0, RoundingMode.HALF_EVEN);
		
	}
	
	public static BigDecimal cmToInch(BigDecimal cm){
		if(cm == null) return null;
		return cm.multiply(new BigDecimal(0.3937f)).setScale(0, RoundingMode.HALF_EVEN);
		
	}
	
	public static BigDecimal kgToLBS(BigDecimal kg){
		if(kg == null) return null;
		return kg.multiply(new BigDecimal(LBS_EQUALS_1_KG)).setScale(2, RoundingMode.HALF_EVEN);
		
	}
	
	public static BigDecimal lbsToKG(BigDecimal lbs){
		if(lbs == null) return null;
		return lbs.divide(new BigDecimal(LBS_EQUALS_1_KG),2, RoundingMode.HALF_EVEN);
		
	}
}
