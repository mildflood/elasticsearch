/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;


public class TermResultsDerivedNonZero {
    
     private String entityId;
     private String termId;
     private int FY = 0;
     private String FQ;

    /**
     * @return the entityId
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * @param entityId the entityId to set
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

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
     * @return the FY
     */
    public int getFY() {
        return FY;
    }

    /**
     * @param FY the FY to set
     */
    public void setFY(int FY) {
        this.FY = FY;
    }

    /**
     * @return the FQ
     */
    public String getFQ() {
        return FQ;
    }

    /**
     * @param FQ the FQ to set
     */
    public void setFQ(String FQ) {
        this.FQ = FQ;
    }
     
}
