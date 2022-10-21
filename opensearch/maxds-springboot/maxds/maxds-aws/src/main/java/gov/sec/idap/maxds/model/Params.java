
package gov.sec.idap.maxds.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Params {

	@SerializedName("q")
	@Expose
	private String q;
	@SerializedName("fl")
	@Expose
	private String fl;

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String getFl() {
		return fl;
	}

	public void setFl(String fl) {
		this.fl = fl;
	}

}