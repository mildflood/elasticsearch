package gov.sec.idap.maxds.model;

public class ReportDataVO 
{
	public String profileID;
	public String collectionType;
	public String collectionName;
	public String collectionURL;
	public String getProfileID() {
		return profileID;
	}
	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}
	public String getCollectionType() {
		return collectionType;
	}
	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public String getCollectionURL() {
		return collectionURL;
	}
	public void setCollectionURL(String collectionURL) {
		this.collectionURL = collectionURL;
	}
	@Override
	public String toString() {
		return "ReportDataVO [profileID=" + profileID + ", collectionType=" + collectionType + ", collectionName="
				+ collectionName + ", collectionURL=" + collectionURL + "]";
	}
	
	
}
