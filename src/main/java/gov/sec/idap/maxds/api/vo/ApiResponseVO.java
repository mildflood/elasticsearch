package gov.sec.idap.maxds.api.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "processResponse")
@XmlAccessorType(XmlAccessType.NONE)
public class ApiResponseVO implements Serializable 
{
    private static final long serialVersionUID = 1L;

    @XmlAttribute
    public String id;
    @XmlElement
	public String termId;
    @XmlElement
	public Long cik;
    @XmlElement
	public String processingGroupId;
    @XmlElement
	public String errorMessage;
    @XmlElement
	public String statusLink;
	@XmlElement
	public String resultLink;
	
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}
	public Long getCik() {
		return cik;
	}
	public void setCik(Long cik) {
		this.cik = cik;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatusLink() {
		return statusLink;
	}
	public void setStatusLink(String statusLink) {
		this.statusLink = statusLink;
	}
	public String getProcessingGroupId() {
		return processingGroupId;
	}
	public void setProcessingGroupId(String processingGroupId) {
		this.processingGroupId = processingGroupId;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getResultLink() {
		return resultLink;
	}
	public void setResultLink(String resultLink) {
		this.resultLink = resultLink;
	}

}
