/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



@JsonIgnoreProperties(ignoreUnknown = true)
public class SECFilings {
    
    public String id = null;
    public String entityId ;
    public int filingPeriod;
    public String fiscalYearEnd;
    public String formType;
    @JsonProperty("fy_l")public int FY;
    @JsonProperty("fp")public String FQ;
    
}
