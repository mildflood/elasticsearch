/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import gov.sec.idap.maxds.api.vo.ProcessingStatusVO;

@SolrDocument(collection = "MaxDS_ProcessingLogs")
public class ProcessingLogDoc {

    @Id
    public String id;
    @Field("processingGroupId_s")
    public String processingGroupId;
    @Field("entityId_s")
    public String entityId;
    @Field("termId_s")
    public String termId;
    @Field("logStatus_s")
    public String logStatus;
    @Field("description_s")
    public String description;
    @Field("userName_s")
    public String userName;
    @Field("currentTime_s")
    public String currentTime;
    @Field("processingTimeInSec_l")
    public long processingTimeInSec = 0;
    @Field("noOfEntitiesBeingProcessed_i")
    public int noOfEntitiesBeingProcessed = 0;
    @Field("lastModified_dt")
    public Date lastModified = new Date();
    
    @Field("processingSequence_i")
    public int processingSequence = 0;

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
    
  
}
