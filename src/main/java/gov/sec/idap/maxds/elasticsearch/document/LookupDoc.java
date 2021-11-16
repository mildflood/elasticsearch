/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.elasticsearch.document;

import gov.sec.idap.maxds.elasticsearch.helper.Indices;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
@Document(indexName = Indices.LOOKUP_REFERENCES_INDEX)
@Setting(settingPath = "static/es-settings.json")
public class LookupDoc {

    public enum LookupType
    {
            termRulePriorityGroup,
            termRuleCategory,
            taxonomyElement,
            termMapGroup,
            none
    }    
    
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    
    @JsonProperty("name_s")
    @Field(type = FieldType.Text)
    private String name_s;
    
    @JsonProperty("label_s")
    @Field(type = FieldType.Text)
    private String label_s;
    
    @JsonProperty("description_s")
    @Field(type = FieldType.Text)
    private String description_s;
    
    @JsonProperty("type_s")
    @Field(type = FieldType.Text)
    private LookupType type_s = LookupType.none;
    
    @JsonProperty("isTextBlock_b")
    @Field(type = FieldType.Boolean)
    private Boolean isTextBlock_b;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name_s;
	}

	public void setName(String name_s) {
		this.name_s = name_s;
	}

	public String getLabel() {
		return label_s;
	}

	public void setLabel(String label_s) {
		this.label_s = label_s;
	}

	public String getDescription() {
		return description_s;
	}

	public void setDescription(String description_s) {
		this.description_s = description_s;
	}

	public LookupType getType() {
		return type_s;
	}

	public void setType(LookupType type_s) {
		this.type_s = type_s;
	}

	public Boolean getIsTextBlock() {
		return isTextBlock_b;
	}

	public void setIsTextBlock(Boolean isTextBlock_b) {
		this.isTextBlock_b = isTextBlock_b;
	}
     
}
