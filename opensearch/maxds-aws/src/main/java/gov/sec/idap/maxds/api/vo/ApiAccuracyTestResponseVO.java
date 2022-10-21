package gov.sec.idap.maxds.api.vo;

public class ApiAccuracyTestResponseVO {
	
	private String jobName;
	private String userId;
	private String startTime;
	private String statusLink;
	private String description;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStatusLink() {
		return statusLink;
	}
	public void setStatusLink(String statusLink) {
		this.statusLink = statusLink;
	}
}
