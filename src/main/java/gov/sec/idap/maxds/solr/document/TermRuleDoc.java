/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.sec.idap.maxds.domain.PeriodType;
import gov.sec.idap.maxds.domain.ProcessingStatusCode;
import gov.sec.idap.maxds.domain.TermRule;
import gov.sec.idap.maxds.domain.TermRuleHeader;
import java.io.IOException;
import java.util.Date;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;


@SolrDocument(collection = "MaxDS_TermRules")
public class TermRuleDoc {

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
  
    @Id  
    private String id;
    @Field
    private String termId;
    @Field
    private String name;
    @Field
    private String description;
    @Field
    private String type;
    @Field
    private PeriodType periodType = PeriodType.na;
    @Field
    private Date lastModified = new Date();
    @Field
    private int order = 0;
    @Field
    private ProcessingStatusCode processingStatus = ProcessingStatusCode.NotProcessed;
    @Field
    private String priorityGroup;
    @Field
    private String financialStatement = "Uncategorized";
    @Field
    private Boolean hasValidations;
    @Field("includeInAccuracyTests_b")
    private Boolean includeInAccuracyTests;
    
    @Field
    private String ruleText;
    
    
    public TermRule BuildRuleFromRuleText() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            TermRule ret = mapper.readValue(this.getRuleText(), TermRule.class);
            ret.setProcessingStatus(processingStatus);
            return ret;

        } catch (IOException exp) {

        }

        return null;
    }
    
    public TermRuleHeader buildTermRuleHeader(){
        
        TermRuleHeader ret = new TermRuleHeader();
        ret.setTermId(termId);
        ret.setName(name);
        ret.setFinancialStatement(financialStatement);
        ret.setPriorityGroup(priorityGroup);
        ret.setHasValidations(hasValidations);
       
        
        return ret;
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

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
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

    public String getPriorityGroup() {
        return priorityGroup;
    }

    public void setPriorityGroup(String priorityGroup) {
        this.priorityGroup = priorityGroup;
    }

    public String getFinancialStatement() {
        return financialStatement;
    }

    public void setFinancialStatement(String financialStatement) {
        this.financialStatement = financialStatement;
    }
    
    @Override
    public String toString() {
        return "ID: " + termId + ", Rule Name : " + name;
    }

    /**
     * @return the ruleText
     */
    public String getRuleText() {
        return ruleText;
    }

    /**
     * @param ruleText the ruleText to set
     */
    public void setRuleText(String ruleText) {
        this.ruleText = ruleText;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

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
}
