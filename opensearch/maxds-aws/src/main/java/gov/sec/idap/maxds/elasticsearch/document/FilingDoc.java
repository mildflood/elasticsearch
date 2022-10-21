package gov.sec.idap.maxds.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Component;

import gov.sec.idap.maxds.elasticsearch.helper.Indices;

@Component
@Document(indexName = Indices.FILING_INDEX)
@Setting(settingPath = "es-settings.json")
public class FilingDoc {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Text)
    private String accession;
    @Field(type = FieldType.Text)
    private String cik;
    @Field(type = FieldType.Text)
    private String companyName;
    @Field(type = FieldType.Text)
    private String entityId;
    @Field(type = FieldType.Text)
    private String filingDate;
    @Field(type = FieldType.Text)
    private String filingPeriod;
    @Field(type = FieldType.Text)
    private String fiscalYearEnd;
    @Field(type = FieldType.Text)
    private String formType;
    @Field(type = FieldType.Text)
    private String fp;
    @Field(type = FieldType.Text)
    private String fy;
    @Field(type = FieldType.Text)
    private String fy_l;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public String getCik() {
		return cik;
	}
	public void setCik(String cik) {
		this.cik = cik;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getFilingDate() {
		return filingDate;
	}
	public void setFilingDate(String filingDate) {
		this.filingDate = filingDate;
	}
	public String getFilingPeriod() {
		return filingPeriod;
	}
	public void setFilingPeriod(String filingPeriod) {
		this.filingPeriod = filingPeriod;
	}
	public String getFiscalYearEnd() {
		return fiscalYearEnd;
	}
	public void setFiscalYearEnd(String fiscalYearEnd) {
		this.fiscalYearEnd = fiscalYearEnd;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public String getFp() {
		return fp;
	}
	public void setFp(String fp) {
		this.fp = fp;
	}
	public String getFy() {
		return fy;
	}
	public void setFy(String fy) {
		this.fy = fy;
	}
	public String getFy_l() {
		return fy_l;
	}
	public void setFy_l(String fy_l) {
		this.fy_l = fy_l;
	}
}