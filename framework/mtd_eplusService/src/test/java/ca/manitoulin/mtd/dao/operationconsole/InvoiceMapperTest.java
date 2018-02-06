package ca.manitoulin.mtd.dao.operationconsole;

import static org.apache.log4j.Logger.getLogger;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.manitoulin.mtd.dto.operationconsole.Invoice;
import ca.manitoulin.mtd.dto.operationconsole.InvoiceDetail;
import ca.manitoulin.mtd.util.ApplicationSession;
import ca.manitoulin.mtd.util.ResultContextSimulator;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-db.xml" })
public class InvoiceMapperTest extends ResultContextSimulator{
	static private Logger log = getLogger(InvoiceMapperTest.class);

	@Autowired
	private InvoiceMapper invoiceMapper;
	
	@Before
	public void genSession(){
		super.gengerateUserSession();
	}
	
	@Test
	@Rollback(true)
	public void testAll() {
		final int tripId = 1;
		
		Invoice inv = new Invoice();
		inv.setBillingCurrency("RMB");
		inv.setInvoiceDate(new Date());
		inv.setInvoiceFrom("From");
		inv.setInvoiceTo("to");
		inv.setMiles(new BigDecimal(0.76f));
		inv.setRate(new BigDecimal(0.9f));
		inv.setTripId(tripId);
		
		invoiceMapper.insertInvoice(inv);
		
		List<Invoice> lst = invoiceMapper.selectInvoices(tripId, null, null, null, null, ApplicationSession.get().getReferLanguage());
		
		Assert.assertEquals( 1,lst.size());
		
		Invoice inv2 = invoiceMapper.selectInvoiceById(inv.getId());
		
		inv2.setBillingCurrency("CAD");
		invoiceMapper.updateInvoice(inv2);
		invoiceMapper.insertInvoice(inv2);
		
		
		invoiceMapper.deleteInvoice(inv.getId());
		lst = invoiceMapper.selectInvoices(tripId, null, null, null, null, ApplicationSession.get().getReferLanguage());
		Assert.assertEquals("CAD", lst.get(0).getBillingCurrency());
		
		InvoiceDetail de = new InvoiceDetail();
		de.setAmount(new BigDecimal(99));
		de.setChargeCode("AC");
		de.setInvoiceId(inv2.getId());
		de.setItem("Item");
		de.setSequence(1);
		de.setTaxCode("XX");
		invoiceMapper.insertInvoiceDetail(de);
		
		List<InvoiceDetail> dList = invoiceMapper.selectInvoiceDetails(inv2.getId(), ApplicationSession.get().getReferLanguage());
		Assert.assertEquals(1, dList.size());
		
		invoiceMapper.deleteAllInvoiceDetails(inv2.getId());
		invoiceMapper.deleteInvoice(inv2.getId());
		
	}

}
