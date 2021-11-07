
package gov.sec.idap.maxds.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

	@SerializedName("responseHeader")
	@Expose
	private ResponseHeader responseHeader;
	@SerializedName("response")
	@Expose
	private Response_ response;

	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
	}

	public Response_ getResponse() {
		return response;
	}

	public void setResponse(Response_ response) {
		this.response = response;
	}

}