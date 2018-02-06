package ca.manitoulin.mtd.dao.tracing;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.manitoulin.mtd.dto.support.Attachment;

public interface TracingMapper {
	List<Attachment> selectDownloadedImages(@Param("probillNumber") String probillNumber);
	void insertImagesRecords(@Param("probillNumber") String probillNumber,@Param("imageId") String imageId);
}
