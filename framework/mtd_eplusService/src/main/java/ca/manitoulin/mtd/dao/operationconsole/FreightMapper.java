package ca.manitoulin.mtd.dao.operationconsole;

import java.util.List;

import ca.manitoulin.mtd.dto.operationconsole.Freight;

public interface FreightMapper {
	
	List<Freight> selectFreightByTripId(Integer id);
	
	void insertFreight(Freight freight);
	
	void updateFreight(Freight freight);
	
	void deleteFreight(Integer id);
	
	void deleteFreightByTripId(Integer id);
	
	Freight selectFreightById(Integer id);

}
