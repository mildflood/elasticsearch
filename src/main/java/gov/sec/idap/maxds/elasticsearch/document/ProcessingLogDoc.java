package gov.sec.idap.maxds.elasticsearch.document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Component;

import gov.sec.idap.maxds.api.vo.ProcessingStatusVO;
import gov.sec.idap.maxds.elasticsearch.helper.Indices;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
@Document(indexName = Indices.PROCESSING_LOG_INDEX)
@Setting(settingPath = "static/es-settings.json")
public class ProcessingLogDoc {
	
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
	
	@JsonProperty("processingGroupId")
    @Field(type = FieldType.Text)
    private String processingGroupId;
    
    @JsonProperty("entityId")
    @Field(type = FieldType.Text)
    private String entityId;
    
    @JsonProperty("termId")
    @Field(type = FieldType.Text)
    private String termId;
    
    @JsonProperty("logStatus")
    @Field(type = FieldType.Text)
    private String logStatus;
    
    @JsonProperty("description")
    @Field(type = FieldType.Text)
    private String description;
    
    @JsonProperty("userName")
    @Field(type = FieldType.Text)
    private String userName;
    
    @JsonProperty("currentTime")
    @Field(type = FieldType.Text)
    private String currentTime;
    
    @JsonProperty("processingTimeInSec")
    @Field(type = FieldType.Long)
    private long processingTimeInSec;
    
    @JsonProperty("noOfEntitiesBeingProcessed")
    @Field(type = FieldType.Integer)
    private int noOfEntitiesBeingProcessed;
    
    @JsonProperty("lastModified")
    @Field(type = FieldType.Date)
    private Date lastModified = new Date();
    
    @JsonProperty("processingSequence")
    @Field(type = FieldType.Integer)
    private int processingSequence = 0;
    
    public static ProcessingLogDoc BuildProcessingLog(String groupId,
            String entityId, String termId, String logStatus,
            String description, long processingTimeInSec, int noOfEntities, String userName) {
        ProcessingLogDoc ret = new ProcessingLogDoc();
        ret.id = groupId; //so that we are updating the same record...
        ret.processingGroupId = groupId;
        ret.entityId = entityId;
        ret.termId = termId;
        ret.description = description;
        ret.termId = termId;
        ret.logStatus = logStatus;

        ret.currentTime = GetUTCdatetimeAsString();
        ret.processingTimeInSec = processingTimeInSec;
        ret.noOfEntitiesBeingProcessed = noOfEntities;
        ret.userName = userName;

        return ret;
    }

    public static String GetUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }
    
    public ProcessingStatusVO createVO() {
    	ProcessingStatusVO vo = new ProcessingStatusVO();
    	vo.id = this.id;
    	vo.currentTime = this.currentTime;
    	vo.description = this.description;
    	vo.cik = Long.parseLong(this.entityId);
    	vo.lastModified = this.lastModified;
    	vo.logStatus = this.logStatus;
    	vo.noOfEntitiesBeingProcessed = this.noOfEntitiesBeingProcessed;
    	vo.processingGroupId = this.processingGroupId;
    	vo.processingSequence = this.processingSequence;
    	vo.processingTimeInSec = this.processingTimeInSec;
    	vo.termId = this.termId;
    	vo.userName = this.userName;
    	
    	return vo;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcessingGroupId() {
		return processingGroupId;
	}

	public void setProcessingGroupId(String processingGroupId) {
		this.processingGroupId = processingGroupId;
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

	public String getLogStatus() {
		return logStatus;
	}

	public void setLogStatus(String logStatus) {
		this.logStatus = logStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public long getProcessingTimeInSec() {
		return processingTimeInSec;
	}

	public void setProcessingTimeInSec(long processingTimeInSec) {
		this.processingTimeInSec = processingTimeInSec;
	}

	public int getNoOfEntitiesBeingProcessed() {
		return noOfEntitiesBeingProcessed;
	}

	public void setNoOfEntitiesBeingProcessed(int noOfEntitiesBeingProcessed) {
		this.noOfEntitiesBeingProcessed = noOfEntitiesBeingProcessed;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public int getProcessingSequence() {
		return processingSequence;
	}

	public void setProcessingSequence(int processingSequence) {
		this.processingSequence = processingSequence;
	}
}
