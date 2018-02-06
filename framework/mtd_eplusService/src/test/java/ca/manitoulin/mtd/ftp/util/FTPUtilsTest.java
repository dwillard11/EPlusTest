package ca.manitoulin.mtd.ftp.util;

import java.util.ArrayList;

import org.junit.Test;

import ca.manitoulin.mtd.util.FTPUtils;

public class FTPUtilsTest {
	@Test
	public void testCopyFileToLocal() throws Exception {
		ArrayList<String> src = new ArrayList<String>();
		src.add("/homepage/bridge/B15169AAKUN.AFP");
		
		ArrayList<String> dist = new ArrayList<String>();
		dist.add("C:\\B15169AAKUN.AFP");
		
		FTPUtils.downloadFiles("10.3.44.54", 2121, src, dist);
	}
}
