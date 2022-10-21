/*
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

/**
 *
 * @author srika
 */
public class TermRuleStatisticsInput {
    public String termRuleId;
    public String division ="NULL";
    public String sector ="NULL";
    public String sic ="NULL";
    public String filerCategory ="NULL";
    public String entityId ="NULL";
    public int maxYear;
    public int minYear;
    public Boolean includeQuarterly;
    public int rankId;
    
            
}
