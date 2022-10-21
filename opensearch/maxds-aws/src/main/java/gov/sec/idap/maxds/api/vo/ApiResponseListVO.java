package gov.sec.idap.maxds.api.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name="processResponses")
public class ApiResponseListVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<ApiResponseVO> apiResponseVOs = new ArrayList<ApiResponseVO>();
	 
    public List<ApiResponseVO> getApiResponseVOs() {
        return apiResponseVOs;
    }
 
    public void setApiResponseVOs(List<ApiResponseVO> apiResponseVOs) {
        this.apiResponseVOs = apiResponseVOs;
    }
}