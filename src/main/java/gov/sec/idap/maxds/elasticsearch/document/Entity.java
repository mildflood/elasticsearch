package gov.sec.idap.maxds.elasticsearch.document;

import gov.sec.idap.maxds.elasticsearch.helper.Indices;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Component;

@Component
@Document(indexName = Indices.ENTITY_INDEX)
@Setting(settingPath = "static/es-settings.json")
public class Entity {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Text)
    private String geoState;
    @Field(type = FieldType.Text)
    private String cik;
    @Field(type = FieldType.Text)
    private String companyName;
    @Field(type = FieldType.Text)
    private String earliestFilingDate;
    @Field(type = FieldType.Text)
    private String filerCategory;
    @Field(type = FieldType.Text)
    private String entityId;
    @Field(type = FieldType.Text)
    private String industry;
    @Field(type = FieldType.Text)
    private String sic;    
    @Field(type = FieldType.Text)
    private String latestFilingDate;
    @Field(type = FieldType.Text)
    private String division;
    @Field(type = FieldType.Text)
    private String sector;
    @Field(type = FieldType.Text)
    private String tradingSymbol;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGeoState() {
		return geoState;
	}
	public void setGeoState(String geoState) {
		this.geoState = geoState;
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
	public String getEarliestFilingDate() {
		return earliestFilingDate;
	}
	public void setEarliestFilingDate(String earliestFilingDate) {
		this.earliestFilingDate = earliestFilingDate;
	}
	public String getFilerCategory() {
		return filerCategory;
	}
	public void setFilerCategory(String filerCategory) {
		this.filerCategory = filerCategory;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getSic() {
		return sic;
	}
	public void setSic(String sic) {
		this.sic = sic;
	}
	public String getLatestFilingDate() {
		return latestFilingDate;
	}
	public void setLatestFilingDate(String latestFilingDate) {
		this.latestFilingDate = latestFilingDate;
	}
	public String getDivision() {
		return division;
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
	public String getTradingSymbol() {
		return tradingSymbol;
	}
	public void setTradingSymbol(String tradingSymbol) {
		this.tradingSymbol = tradingSymbol;
	}
}

