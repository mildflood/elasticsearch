package gov.sec.idap.maxds.elasticsearch.document;

import gov.sec.idap.maxds.domain.PeriodType;
import gov.sec.idap.maxds.domain.ProcessingStatusCode;
import gov.sec.idap.maxds.domain.TermRule;
import gov.sec.idap.maxds.domain.TermRuleHeader;
import gov.sec.idap.maxds.elasticsearch.helper.Indices;

import java.io.IOException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Document(indexName = Indices.TERMRULE_INDEX)
@Setting(settingPath = "es-settings.json")
public class TermRuleDoc {
	@Id
	@Field(type = FieldType.Keyword)
	private String id;

	@Field(type = FieldType.Keyword)
	private String termId;

	@Field(type = FieldType.Keyword)
	private String processingStatus = ProcessingStatusCode.NotProcessed.toString();

	@Field(type = FieldType.Keyword)
	private String periodType = PeriodType.na.toString();

	@Field(type = FieldType.Text)
	private String name;

	@Field(type = FieldType.Text)
	private String financialStatement = "Uncategorized";

	@Field(type = FieldType.Boolean)
	private Boolean hasValidations;

	@Field(type = FieldType.Text)
	private String description;

	@Field(type = FieldType.Date)
	private Date lastModified;

	@Field(type = FieldType.Text)
	private String type;

	@Field(type = FieldType.Long)
	private Long order;

	@Field(type = FieldType.Text)
	private String ruleText;

	private Boolean includeInAccuracyTests;
	private String priorityGroup;

	/**
	 * @return the includeInAccuracyTests
	 */
	public Boolean getIncludeInAccuracyTests() {
		return this.includeInAccuracyTests;
	}

	/**
	 * @param includeInAccuracyTests the includeInAccuracyTests to set
	 */
	public void setIncludeInAccuracyTests(Boolean includeInAccuracyTests) {
		this.includeInAccuracyTests = includeInAccuracyTests;
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

	public String getProcessingStatus() {
		return processingStatus;
	}

	public void setProcessingStatus(String processingStatus) {
		this.processingStatus = processingStatus;
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFinancialStatement() {
		return financialStatement;
	}

	public void setFinancialStatement(String financialStatement) {
		this.financialStatement = financialStatement;
	}

	public Boolean getHasValidations() {
		return hasValidations;
	}

	public void setHasValidations(Boolean hasValidations) {
		this.hasValidations = hasValidations;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	public String getRuleText() {
		return ruleText;
	}

	public void setRuleText(String ruleText) {
		//getting includeInAccuracyTests and priorityGroup value from ruleText json string
		try {
			JSONArray array = new JSONArray(ruleText);
		      for(int i=0; i < array.length(); i++) {
		         JSONObject object = array.getJSONObject(i);
		         this.setIncludeInAccuracyTests(Boolean.valueOf(object.getString("includeInAccuracyTests")));
		         this.setPriorityGroup(object.getString("priorityGroup"));
		      }
		} catch (JSONException err) {
			//log.debug("Error", err.toString());
		}
		this.ruleText = ruleText;
	}

	public String getPriorityGroup() {
		return priorityGroup;
	}

	public void setPriorityGroup(String priorityGroup) {
		this.priorityGroup = priorityGroup;
	}

	public TermRule BuildRuleFromRuleText() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("RuleText in " + this.getName() + ": " + this.getRuleText());
			TermRule ret = mapper.readValue(this.getRuleText(), TermRule.class);
			System.out.println("Done with mapper work in TermRuleDoc for " + this.getName());
			ret.setProcessingStatus(ProcessingStatusCode.valueOf(processingStatus));
			return ret;

		} catch (IOException exp) {

		}

		return null;
	}

	public TermRuleHeader buildTermRuleHeader() {

		TermRuleHeader ret = new TermRuleHeader();
		ret.setTermId(termId);
		ret.setName(name);
		ret.setFinancialStatement(financialStatement);
		ret.setPriorityGroup(priorityGroup);
		ret.setHasValidations(hasValidations);

		return ret;
	}

}
