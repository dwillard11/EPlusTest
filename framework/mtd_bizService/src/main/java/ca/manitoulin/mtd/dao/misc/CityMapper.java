package ca.manitoulin.mtd.dao.misc;

import org.apache.ibatis.annotations.Param;

public interface CityMapper {
	
	String selectCityNameWithLongCode(@Param("schema") String schema, @Param("code") String code);
	
	String selectCityNameWithShortCode(@Param("schema") String schema, @Param("code") String code);
	
	String selectStateWithLongCode(@Param("schema") String schema, @Param("code") String code);
	
	String selectStateWithShortCode(@Param("schema") String schema, @Param("code") String code);
	

}
