package gov.sec.idap.maxds.api.vo;

import java.util.List;

import gov.sec.idap.maxds.domain.AccuracyTestItem;

public class AccuracyTestDataVO {
	public Boolean status;
    public String errorDescription;
    public Long cik;
    public String htmlTitle;
    public int fiscalYear;
    public String filingUrl;
    public String formType;
    public int filingDate;
    
    public List<AccuracyTestItem> items;

}
