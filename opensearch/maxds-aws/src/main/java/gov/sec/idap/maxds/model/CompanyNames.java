package gov.sec.idap.maxds.model;

import java.util.Comparator;

public class CompanyNames implements Comparator<TermRuleHeader>{

	@Override
	public int compare(TermRuleHeader o1, TermRuleHeader o2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private String companyName;
	private String cik;
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCik() {
		return cik;
	}
	public void setCik(String cik) {
		this.cik = cik;
	}
	@Override
	public String toString() {
		return "CompanyNames [companyName=" + companyName + ", cik=" + cik + "]";
	}
	
	
	

}
