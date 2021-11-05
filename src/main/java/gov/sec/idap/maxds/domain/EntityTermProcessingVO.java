package gov.sec.idap.maxds.domain;

import java.util.List;

public class EntityTermProcessingVO 
{
	public List<String> entities;
	public List<String> terms;
	public String userid;
	public List<String> getEntities() {
		return entities;
	}
	public void setEntities(List<String> entities) {
		this.entities = entities;
	}
	public List<String> getTerms() {
		return terms;
	}
	public void setTerms(List<String> terms) {
		this.terms = terms;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	@Override
	public String toString() {
		return "EntityTermProcessingVO [entities=" + entities + ", terms=" + terms + ", userid=" + userid + "]";
	}

	
	
	
	

}
