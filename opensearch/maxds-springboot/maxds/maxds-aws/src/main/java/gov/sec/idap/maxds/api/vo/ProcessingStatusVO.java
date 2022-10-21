package gov.sec.idap.maxds.api.vo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ProcessingStatusVO
{
	public String id;
	
	public String processingGroupId;
	
	public Long cik;
	
	public String termId;
	
	public String logStatus;
	
	public String description;
	
	public String userName;
	
	public String currentTime;
	
	public long processingTimeInSec = 0;
	
	public int noOfEntitiesBeingProcessed = 0;
	
	public Date lastModified = new Date();

	public int processingSequence = 0;

	public static ProcessingStatusVO BuildProcessingStatusVO(String groupId, Long cik, String termId, String logStatus,
			String description, long processingTimeInSec, int noOfEntities, String userName) {
		ProcessingStatusVO ret = new ProcessingStatusVO();
		ret.id = groupId; // so that we are updating the same record...
		ret.processingGroupId = groupId;
		ret.cik = cik;
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
}