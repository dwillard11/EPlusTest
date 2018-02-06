package ca.manitoulin.mtd.util;

import java.io.File;
import java.io.IOException;

public interface IDocumentManager {

	String getFilePathToSave(Integer tripId, String originalFilename);
	
	File getDocument(Integer tripId, String fileName) throws IOException;
	
	String getEmailAttachmentFolderPath();

}