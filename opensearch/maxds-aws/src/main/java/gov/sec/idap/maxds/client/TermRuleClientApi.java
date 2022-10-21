/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.client;

import gov.sec.idap.maxds.domain.TermRule;
import java.util.List;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 *
 * @author Sriram.Srinivasan
 */
public interface TermRuleClientApi {
    
	@GET("/rest/rule/list.json")
	public List<TermRule> getTermRulesList();
        
	@Headers({ "Content-Type: application/json" })
	@POST("/rest/rule/add")
	public Data addData(@Body Data t);    
        
	@Headers({ "Content-Type: application/json" })
	@POST("/rest/rule/save")
	public TermRule saveTermRule(@Body TermRule t);           
}
