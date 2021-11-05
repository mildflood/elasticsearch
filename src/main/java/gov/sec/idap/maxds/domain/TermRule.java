/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;



public class TermRule implements Comparator<TermRule> 
{

    @Override
    public int compare(TermRule o1, TermRule o2) {
        
        return Integer.compare(o1.order, o2.order);
    }
    
      private String id;
      private String termId;
      private String name;
      private String description;
      private String type;
      private PeriodType periodType = PeriodType.na;
      private Boolean includeInAccuracyTests;
      private Date lastModified = new Date();
      private int order = 0;
      private ProcessingStatusCode processingStatus = ProcessingStatusCode.NotProcessed;
      private String priorityGroup;
      
      private String financialStatement = "Uncategorized";

      private List<TermExpression> expressions = null;
      private List<TermValidationExpression> validationExpressions = null;
      private List<DerviedZeroExpression> derivedZeroExpressions = null;
      
      private List<TermRuleOverride> overrides = null;
      
      private String userid;
      
      private List<String> termIds = null;
      
      public List<String> getTermIds() {
  	return termIds;
  	}

  	public void setTermIds(List<String> termIds) {
  	this.termIds = termIds;
  	}
     
    public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

   
    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
     public ProcessingStatusCode getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatusCode processingStatus) {
        this.processingStatus = processingStatus;
    }

    public List<TermExpression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<TermExpression> expressions) {
        this.expressions = expressions;
    }

   
      public List<TermValidationExpression> getValidationExpressions() {
        return validationExpressions;
    }

    public void setValidationExpressions(List<TermValidationExpression> validationExpressions) {
        this.validationExpressions = validationExpressions;
    }

    /**
     * @return the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return the overrides
     */
    public List<TermRuleOverride> getOverrides() {
        return overrides;
    }
    
    public List<TermRuleOverride> getSortedOverrides() {
        
        //entity level overrides before industry overrrides
        if (overrides != null ) {
            
            List<TermRuleOverride> entOverrides = new ArrayList<>();
            List<TermRuleOverride> indOverrides = new ArrayList<>();
  
            for (TermRuleOverride override : overrides) {
                Boolean add = true;
                if( override.getEntityOverrides() != null){
                    
                    for(EntityOverride eo : override.getEntityOverrides()){
                        
                        if( eo.getEntityId() != null && !eo.getEntityId().isEmpty()){
                            
                            entOverrides.add(override);
                            add = false;
                            break;
                        }
                    }
                }
                if( add){
                    indOverrides.add(override);
                }
            }
            
            entOverrides.addAll(indOverrides);
            return entOverrides;
        }
            
        return overrides;
    }

    /**
     * @param overrides the overrides to set
     */
    public void setOverrides(List<TermRuleOverride> overrides) {
        this.overrides = overrides;
    }

    /**
     * @return the derivedZeroExpressions
     */
    public List<DerviedZeroExpression> getDerivedZeroExpressions() {
        return derivedZeroExpressions;
    }

    /**
     * @param derivedZeroExpressions the derivedZeroExpressions to set
     */
    public void setDerivedZeroExpressions(List<DerviedZeroExpression> derivedZeroExpressions) {
        this.derivedZeroExpressions = derivedZeroExpressions;
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

    /**
     * @return the includeInAccuracyTests
     */
    public Boolean getIncludeInAccuracyTests() {
        return includeInAccuracyTests;
    }

    /**
     * @param includeInAccuracyTests the includeInAccuracyTests to set
     */
    public void setIncludeInAccuracyTests(Boolean includeInAccuracyTests) {
        this.includeInAccuracyTests = includeInAccuracyTests;
    }

      
}




