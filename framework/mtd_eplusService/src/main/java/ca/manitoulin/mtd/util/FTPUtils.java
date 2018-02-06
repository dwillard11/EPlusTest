package ca.manitoulin.mtd.util;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import ca.manitoulin.mtd.exception.BusinessException;

public class FTPUtils {
	private static final Logger log = Logger.getLogger(FTPUtils.class);
	
	/**
	 * Download from FTP server
	 * @param host
	 * @param port
	 * @param remoteFiles 
	 * @param localFiles should be absolutely file path, e.g. C:\\test.txt 
	 */
	public static void downloadFiles(String host, Integer port, List<String> remoteFiles, List<String> localFiles){
		
		//Do some validation
		if(remoteFiles == null || remoteFiles.isEmpty()) {
			log.error("Remote file is not specified, return");
			return;
		}
		
		if(localFiles == null || localFiles.isEmpty()){
			log.error("Distinate file is not specified, return");
			return;
		}
		
		if(remoteFiles.size() != localFiles.size()){
			log.error("Number of remote and local files is not matched, return");
			return;
		}
		
		// begin download
		FTPClient ftpClient = null;
		try {
			ftpClient = abtainConnection(host, port);
		} catch (Exception e) {
			log.error("Failed to establish FTP connection: " + host + ":" + port);
			return;
		}
			
		try{
			for(int i = 0; i < remoteFiles.size(); i++){
				try{
					//download
					String src = remoteFiles.get(i);
					String dist = localFiles.get(i);
					File downloadFile = new File(dist);
					
					OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
					ftpClient.retrieveFile(src, outputStream);
					outputStream.close();
					log.info("File successfully downloaded from " + src + " to " + dist);
				} catch (Exception e) {
					//if any exception catched, continue to next one.
					log.error("Failed to download file:" + remoteFiles.get(i));
				}
			}
		}catch(Exception e){
			log.error("Error occured during download from FTP ", e);
		}finally{
			closeConnection(ftpClient);
		}
		
	}
	
	static private FTPClient abtainConnection(String host, Integer port) throws SocketException, IOException, BusinessException{
		if (isBlank(host)) {
			throw new BusinessException("FTP HOST should not be blank!");
		}
		if (null == port) {
			throw new BusinessException("FTP PORT should not be null!");
		}
		
		long begin = System.currentTimeMillis();
		log.info("+ Try to establish FTP connection");
		
		FTPClient ftpClient = new FTPClient();

		ftpClient.connect(host, port);
		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(BINARY_FILE_TYPE);

		log.info(".");
		log.info("+++++++++++++++++++++ FTP Connection OK +++++++++++++++++++++++++");
		log.info("+   HOST 	= " + host);
		log.info("+   PORT 	= " + port);
		log.info("+   Time effort 	= " + (System.currentTimeMillis() - begin) / 1000 );		
		log.info("+++++++++++++++++++++ FTP Connection OK  +++++++++++++++++++++++++");
		log.info(".");
		
		return ftpClient;
		
	}
	
	static  private void closeConnection(FTPClient connection){
		if(connection == null) return;
		
		try {
			if (connection.isConnected()) {
				connection.logout();
				connection.disconnect();
			}
		} catch (IOException ex) {
			log.error(ex);
		}
	}


}
