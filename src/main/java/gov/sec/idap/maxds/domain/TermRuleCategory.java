/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;


public class TermRuleCategory {
    
    public TermRuleCategory()
    {
        
    }
    
    public TermRuleCategory(String name){
        this.financialStatement = name;
    }
    public String financialStatement;
}
