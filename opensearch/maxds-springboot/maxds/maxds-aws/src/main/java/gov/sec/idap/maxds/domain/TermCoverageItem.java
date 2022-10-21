/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.List;

/**
 *
 * @author SRIKANTH
 */
public class TermCoverageItem {
    
    private String termId;
    private String termName;
    private List<TermCoveragePeriodItem> periodCoverageInfos;

    /**
     * @return the termId
     */
    public String getTermId() {
        return termId;
    }

    /**
     * @param termId the termId to set
     */
    public void setTermId(String termId) {
        this.termId = termId;
    }

    /**
     * @return the termName
     */
    public String getTermName() {
        return termName;
    }

    /**
     * @param termName the termName to set
     */
    public void setTermName(String termName) {
        this.termName = termName;
    }

    /**
     * @return the periodCoverageInfos
     */
    public List<TermCoveragePeriodItem> getPeriodCoverageInfos() {
        return periodCoverageInfos;
    }

    /**
     * @param periodCoverageInfos the periodCoverageInfos to set
     */
    public void setPeriodCoverageInfos(List<TermCoveragePeriodItem> periodCoverageInfos) {
        this.periodCoverageInfos = periodCoverageInfos;
    }

    

   
    
}
