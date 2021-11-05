package gov.sec.idap.maxds.domain;

public class Preferences {

	
	
	private int processId;;
	private String companyName;
	private String code;
	private String preferenceName;
	private String userid;
	private String termName;
	private String resultLink;
	private String validationStatus;
	private String researchLink;
	private String cik;
	private String cName;
	private String quaterly;
	
	public String getQuaterly() {
		return quaterly;
	}
	public void setQuaterly(String quaterly) {
		this.quaterly = quaterly;
	}
	public String getcName() {
		return cName;
	}
	public void setcName(String cName) {
		this.cName = cName;
	}
	public String getCik() {
		return cik;
	}
	public void setCik(String cik) {
		this.cik = cik;
	}
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public String getResultLink() {
		return resultLink;
	}
	public void setResultLink(String resultLink) {
		this.resultLink = resultLink;
	}
	public String getValidationStatus() {
		return validationStatus;
	}
	public void setValidationStatus(String validationStatus) {
		this.validationStatus = validationStatus;
	}
	public String getResearchLink() {
		return researchLink;
	}
	public void setResearchLink(String researchLink) {
		this.researchLink = researchLink;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPreferenceName() {
		return preferenceName;
	}
	public void setPreferenceName(String preferenceName) {
		this.preferenceName = preferenceName;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	@Override
	public String toString() {
		return "Preferences [companyName=" + companyName + ", code=" + code + ", preferenceName=" + preferenceName
				+ ", userid=" + userid + ", termName=" + termName + "]";
	}
	
	

}
