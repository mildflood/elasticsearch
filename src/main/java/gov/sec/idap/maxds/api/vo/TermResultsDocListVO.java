package gov.sec.idap.maxds.api.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name="processResults")
public class TermResultsDocListVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<TermResultsDocVO> termResultsDocVOs = new ArrayList<TermResultsDocVO>();
	 
    public List<TermResultsDocVO> getTermResultsDocVOs() {
        return termResultsDocVOs;
    }
 
    public void setTermResultsDocVOs(List<TermResultsDocVO> termResultsDocVOs) {
        this.termResultsDocVOs = termResultsDocVOs;
    }
}