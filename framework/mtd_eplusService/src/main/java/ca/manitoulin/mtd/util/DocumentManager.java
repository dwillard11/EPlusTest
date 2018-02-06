package ca.manitoulin.mtd.util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.manitoulin.mtd.constant.ApplicationConstants;
import ca.manitoulin.mtd.dao.operationconsole.TripMapper;
import ca.manitoulin.mtd.dto.operationconsole.Trip;

@Service
public class DocumentManager implements IDocumentManager {

    private static final Logger log = Logger.getLogger(DocumentManager.class);
    
   @Autowired
   private TripMapper tripMapper;

   
    public String getFilePathToSave(Integer tripId, String originalFilename) {

        String folderName = getDocumentFolderWithTripCreationTime(tripId).getAbsolutePath();

        
        long now = System.currentTimeMillis() * 1000 + RandomUtils.nextInt(1000);
        String documentName = String.valueOf(now);
        

        return folderName + File.separator + documentName;
    }

	

    /**
     * Retrieve document
     *
     * @param tripId
     * @param fileName
     * @param request
     * @return
     * @throws IOException In case file not found
     */
    public File getDocument(Integer tripId, String fileName) throws IOException {

        String folderName = getDocumentFolderWithTripCreationTime(tripId).getAbsolutePath();


        String documentName = fileName;

        File file = new File(folderName + File.separator + documentName);

        if (!file.exists())
            throw new IOException("File NOT exists: " + file.getAbsolutePath());
        else
            return file;

    }

    private File getDocumentFolderWithTripCreationTime(Integer tripId){
    	
    	Trip trip = tripMapper.selectQuoteByID(tripId, ApplicationSession.get().getReferLanguage());
    	
    	Date creationDate = trip.getCreateTime();
    	log.debug("--> create time is " + creationDate + " of trip : " + tripId);
    	Calendar c = Calendar.getInstance();
    	c.setTime(creationDate);
    	String year = String.valueOf(c.get(Calendar.YEAR));
    	String month = String.valueOf(c.get(Calendar.MONTH) + 1);
    	String rootPath = getAttachmentRootPath();
    	
    	String folderName = (rootPath.endsWith(File.separator) ? rootPath : (rootPath + File.separator)) 
    			+ year + File.separator 
    			+ month + File.separator 
    			+ tripId;
    	
    	File dir = creatIfNotExist(folderName);

        return dir;
    	
    }

    /**
     * Return the directory where to save quote's document. If it not exists,
     * will create.
     * @deprecated - Use getDocumentFolderWithTripCreationTime() instead
     * @param tripId
     * @param request
     * @return
     */
    private File getDocumentFolder(Integer tripId) {
        String rootPath = getAttachmentRootPath();

        // path should look like C:\\eplus\\pdf\\quote\\1\\20
        String folderName = (rootPath.endsWith(File.separator) ? rootPath : (rootPath + File.separator)) + "quote"
                + File.separator + getSubFolderName(tripId) + File.separator + tripId;

        File dir = creatIfNotExist(folderName);

        return dir;
    }

    
    private String getAttachmentRootPath() {

        String rootpath = ApplicationConstants.getConfig("sys.attachment.abspath");
        creatIfNotExist(rootpath);
        
        return rootpath;
    }
    
    public String getEmailAttachmentFolderPath(){
    	String root = getAttachmentRootPath();
    	String emailFolder = root + File.separator +  ApplicationConstants.getConfig("sys.attachment.emailFolderName");
    	creatIfNotExist(emailFolder);
        
        return emailFolder;
    }


    private File creatIfNotExist(String folderPath){
    	File rootFolder = new File(folderPath);
        if (!rootFolder.exists()) {
             rootFolder.mkdirs();
             log.info("Target folder not exists, create one at:" + rootFolder.getAbsolutePath());
        }
        return rootFolder;
    }
    /**
     * To improve IO performance, limit 1000 sub-folder in the attachment file system
     *
     * @param id
     * @return
     */
    private String getSubFolderName(Integer id) {
        int i = id / 1000 + 1;
        return String.valueOf(i);
    }


}
