package gov.sec.idap.maxds.model;

import java.util.Comparator;

public class TermNames implements Comparator<TermRuleHeader>{

	private String termId;
	private String name;
	private String financialStatement;
	private String priorityGroup;
	private String hasValidations;
	
	@Override
	public int compare(TermRuleHeader o1, TermRuleHeader o2) {
		// TODO Auto-generated method stub
		return 0;
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
	public String getFinancialStatement() {
		return financialStatement;
	}
	public void setFinancialStatement(String financialStatement) {
		this.financialStatement = financialStatement;
	}
	public String getPriorityGroup() {
		return priorityGroup;
	}
	public void setPriorityGroup(String priorityGroup) {
		this.priorityGroup = priorityGroup;
	}
	public String getHasValidations() {
		return hasValidations;
	}
	public void setHasValidations(String hasValidations) {
		this.hasValidations = hasValidations;
	}

}
