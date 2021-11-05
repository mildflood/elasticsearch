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
public class AccuracyTestItem {
    
    public String termId;
    public double value = 0;
    public String resolvedExpression;
    public String csTermId;
   
    public double csTermValue = 0;
    
    public Boolean isCheckedCS = false;
    public Boolean isCheckedMaxDS = false;
    public String notes;
    public String financialStatement = "Uncategorized";
    
    
}
