
package gov.sec.idap.maxds.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Doc {

	@SerializedName("termId")
	@Expose
	private String termId;
	@SerializedName("FY")
	@Expose
	private Integer fY;
	@SerializedName("accession_s")
	@Expose
	private String accessionS;
	@SerializedName("entityId")
	@Expose
	private String entityId;
	@SerializedName("FQ")
	@Expose
	private String fQ;
	@SerializedName("termName")
	@Expose
	private String termName;
	@SerializedName("value")
	@Expose
	private Double value;

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public Integer getFY() {
		return fY;
	}

	public void setFY(Integer fY) {
		this.fY = fY;
	}

	public String getAccessionS() {
		return accessionS;
	}

	public void setAccessionS(String accessionS) {
		this.accessionS = accessionS;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getFQ() {
		return fQ;
	}

	public void setFQ(String fQ) {
		this.fQ = fQ;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

}