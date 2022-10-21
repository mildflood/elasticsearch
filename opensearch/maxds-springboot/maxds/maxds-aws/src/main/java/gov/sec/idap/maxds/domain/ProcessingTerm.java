package gov.sec.idap.maxds.domain;

public class ProcessingTerm {
	
	private String termId;
	private String division;
	private String sector;
	private String sic;
	private String filerCategory;
	private String entityId;
	private Boolean isNewAction;
	private String userid;
	
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}
	public String getDivision() {
		return division;
	}
	public Boolean getIsNewAction() {
		return isNewAction;
	}
	public void setIsNewAction(Boolean isNewAction) {
		this.isNewAction = isNewAction;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getSic() {
		return sic;
	}
	public void setSic(String sic) {
		this.sic = sic;
	}
	public String getFilerCategory() {
		return filerCategory;
	}
	public void setFilerCategory(String filerCategory) {
		this.filerCategory = filerCategory;
	}
	
}
