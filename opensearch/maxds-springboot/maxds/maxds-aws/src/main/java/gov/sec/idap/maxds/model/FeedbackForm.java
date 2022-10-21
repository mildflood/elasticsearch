package gov.sec.idap.maxds.model;

public class FeedbackForm {

	
private String issue;
private String category;
private String name;
private String email;
private String message;
private int phone;
private String user;


public String getUser() {
	return user;
}
public void setUser(String user) {
	this.user = user;
}
public String getIssue() {
	return issue;
}
public void setIssue(String issue) {
	this.issue = issue;
}
public String getCategory() {
	return category;
}
public void setCategory(String category) {
	this.category = category;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public int getPhone() {
	return phone;
}
public void setPhone(int phone) {
	this.phone = phone;
}


	
}
