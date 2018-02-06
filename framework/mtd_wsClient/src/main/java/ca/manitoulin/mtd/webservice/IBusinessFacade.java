package ca.manitoulin.mtd.webservice;

import java.io.IOException;

import javax.jws.WebService;

import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;

@WebService
public interface IBusinessFacade {

	String execute(String contextStr) throws IOException;

}