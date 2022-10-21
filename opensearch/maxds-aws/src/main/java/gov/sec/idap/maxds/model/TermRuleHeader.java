/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.model;

import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class TermRuleHeader implements Comparator<TermRuleHeader> {

    /**
     * @return the hasValidations
     */
    public Boolean getHasValidations() {
        return hasValidations;
    }

    /**
     * @param hasValidations the hasValidations to set
     */
    public void setHasValidations(Boolean hasValidations) {
        this.hasValidations = hasValidations;
    }

    @Override
    public int compare(TermRuleHeader o1, TermRuleHeader o2) {
       
        return o1.getName().compareTo(o2.getName());
    }
    
     private String termId;
     private String name;
     private String financialStatement = "Uncategorized";
     private String priorityGroup="simplePriority";
     private Boolean hasValidations;
    

    /**
     * @return the termId
     */
    public String getTermId() {
        return termId;
    }

    /**
     * @param termId the termId to set
     */
    public void setTermId(String termId) {
        this.termId = termId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /**
     * @return the financialStatement
     */
    public String getFinancialStatement() {
        return financialStatement;
    }

    /**
     * @param financialStatement the financialStatement to set
     */
    public void setFinancialStatement(String financialStatement) {
        this.financialStatement = financialStatement;
    }

    /**
     * @return the priorityGroup
     */
    public String getPriorityGroup() {
        return priorityGroup;
    }

    /**
     * @param priorityGroup the priorityGroup to set
     */
    public void setPriorityGroup(String priorityGroup) {
        this.priorityGroup = priorityGroup;
    }

      
}
