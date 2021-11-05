/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.List;

import gov.sec.idap.maxds.api.vo.AccuracyTestDataVO;

/**
 *
 * @author srika
 */
public class AccuracyTestData {
    
    public Boolean status;
    public String errorDescription;
    public String cik;
    public String htmlTitle;
    public int fiscalYear;
    public String filingUrl;
    public String formType;
    public int filingDate;
    
    public List<AccuracyTestItem> items;
    
    public AccuracyTestDataVO createVO() {
    	AccuracyTestDataVO vo = new AccuracyTestDataVO();
    	vo.cik = Long.parseLong(this.cik);
    	vo.status = this.status;
    	vo.errorDescription = this.errorDescription;
    	vo.htmlTitle = this.htmlTitle;
    	vo.fiscalYear = this.fiscalYear;
    	vo.filingDate = this.filingDate;
    	vo.formType = this.formType;
    	vo.filingUrl = this.filingUrl;
    	vo.items = this.items;
    	return vo;
    }
}
