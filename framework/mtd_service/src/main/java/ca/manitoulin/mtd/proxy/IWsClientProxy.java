package ca.manitoulin.mtd.proxy;

import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;

public interface IWsClientProxy {

	ResultContext execute(RequestContext requestContext) throws Exception;
}
