package gov.sec.idap.maxds.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Component;

import gov.sec.idap.maxds.elasticsearch.helper.Indices;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
@Document(indexName = Indices.ACCURACY_TEST_NOTES_INDEX)
@Setting(settingPath = "static/es-settings.json")
public class AccuracyTestNotesDoc {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    
    @JsonProperty("entityId_s")
    @Field(type = FieldType.Text)
    private String entityId;
    
    @JsonProperty("fy_i")
    @Field(type = FieldType.Long)
    private int FY = 0;
    
    @JsonProperty("fq_s")
    @Field(type = FieldType.Text)
    private String FQ = "FY";
    
    @JsonProperty("termId_s")
    @Field(type = FieldType.Text)
    private String termId;
    
    @JsonProperty("isCheckedCS_b")
    @Field(type = FieldType.Boolean)
    private Boolean isCheckedCS = false;
    
    @JsonProperty("isCheckedMaxDS_b")
    @Field(type = FieldType.Boolean)
    private Boolean isCheckedMaxDS = false;
    
    @JsonProperty("notes_s")
    @Field(type = FieldType.Text)
    private String notes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public int getFY() {
		return FY;
	}

	public void setFY(int fY) {
		FY = fY;
	}

	public String getFQ() {
		return FQ;
	}

	public void setFQ(String fQ) {
		FQ = fQ;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public Boolean getIsCheckedCS() {
		return isCheckedCS;
	}

	public void setIsCheckedCS(Boolean isCheckedCS) {
		this.isCheckedCS = isCheckedCS;
	}

	public Boolean getIsCheckedMaxDS() {
		return isCheckedMaxDS;
	}

	public void setIsCheckedMaxDS(Boolean isCheckedMaxDS) {
		this.isCheckedMaxDS = isCheckedMaxDS;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
