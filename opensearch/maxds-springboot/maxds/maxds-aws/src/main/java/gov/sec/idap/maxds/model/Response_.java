
package gov.sec.idap.maxds.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response_ {

	@SerializedName("numFound")
	@Expose
	private Integer numFound;
	@SerializedName("start")
	@Expose
	private Integer start;
	@SerializedName("docs")
	@Expose
	private List<Doc> docs = null;

	public Integer getNumFound() {
		return numFound;
	}

	public void setNumFound(Integer numFound) {
		this.numFound = numFound;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public List<Doc> getDocs() {
		return docs;
	}

	public void setDocs(List<Doc> docs) {
		this.docs = docs;
	}

}