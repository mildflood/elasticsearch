package gov.sec.idap.maxds.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.sec.idap.maxds.domain.TermMapInformation;
import gov.sec.idap.maxds.elasticsearch.helper.Indices;

@Component
@Document(indexName = Indices.TERM_MAP_INFO_INDEX)
@Setting(settingPath = "es-settings.json")
public class TermMapInformationDoc {
            @Id
            @Field(type = FieldType.Keyword)
            private String id;
            
            @JsonProperty("mapTermName_s")
            @Field(type = FieldType.Text)
            private String mapTermName_s;
            
            @JsonProperty("termId_s")
            @Field(type = FieldType.Keyword)
            private String termId_s;
            
            @JsonProperty("mapName_s")
            @Field(type = FieldType.Keyword)
            private String mapName_s;
            
            @JsonProperty("mapTermId_s")
            @Field(type = FieldType.Keyword)
            private String mapTermId_s;
            
            @JsonProperty("mapTermDescription_s")
            @Field(type = FieldType.Text)
            private String mapTermDescription_s;
            
            @JsonProperty("version_s")
            @Field(type = FieldType.Long)
            private String version_s;

            public TermMapInformationDoc() {
            }

            public TermMapInformationDoc(TermMapInformation mapInfo) {
                this.id = mapInfo.id;
                this.termId_s = mapInfo.termId;
                this.mapName_s = mapInfo.mapName;
                this.mapTermId_s = mapInfo.mapTermId;
                this.mapTermName_s = mapInfo.mapTermName;
                this.mapTermDescription_s = mapInfo.mapTermDescription;
                this.version_s = mapInfo.mapTermMappingInfo;

            }

            public TermMapInformation getTermMapInformation() {
                TermMapInformation ret = new TermMapInformation();
                ret.id = this.id;
                ret.termId = this.termId_s;
                ret.mapName = this.mapName_s;
                ret.mapTermId = this.mapTermId_s;
                ret.mapTermName = this.mapTermName_s;
                ret.mapTermDescription = this.mapTermDescription_s;
                ret.mapTermMappingInfo = this.version_s;

                return ret;
            }
			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getMapTermName() {
				return mapTermName_s;
			}

			public void setMapTermName(String mapTermName_s) {
				this.mapTermName_s = mapTermName_s;
			}

			public String getTermId() {
				return termId_s;
			}

			public void setTermId(String termId_s) {
				this.termId_s = termId_s;
			}

			public String getMapName() {
				return mapName_s;
			}

			public void setMapName(String mapName_s) {
				this.mapName_s = mapName_s;
			}

			public String getMapTermId() {
				return mapTermId_s;
			}

			public void setMapTermId(String mapTermId_s) {
				this.mapTermId_s = mapTermId_s;
			}

			public String getMapTermDescription() {
				return mapTermDescription_s;
			}

			public void setMapTermDescription(String mapTermDescription_s) {
				this.mapTermDescription_s = mapTermDescription_s;
			}

			public String getVersion() {
				return version_s;
			}

			public void setVersion(String version_s) {
				this.version_s = version_s;
			}
}
