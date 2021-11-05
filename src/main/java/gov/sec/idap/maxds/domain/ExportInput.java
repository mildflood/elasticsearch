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
public class ExportInput {

    /**
     * @return the includeValidationInfos
     */
    public Boolean getIncludeValidationInfos() {
        return includeValidationInfos;
    }

    /**
     * @param includeValidationInfos the includeValidationInfos to set
     */
    public void setIncludeValidationInfos(Boolean includeValidationInfos) {
        this.includeValidationInfos = includeValidationInfos;
    }
    
    private Boolean includeFiscalYears = true;
    private Boolean includeFiscalQuarters = true;
    private int startYear = 2016;
    private int endYear = 2020;
    private String entityId;
    private String termId;
    private List<String> entityList;
    private List<String> termIdList;
    private String exportType;
    private Boolean isForAllEntities = false;
    private Boolean includeValidationInfos = true;
    

    /**
     * @return the includeFiscalYears
     */
    public Boolean getIncludeFiscalYears() {
        return includeFiscalYears;
    }

    /**
     * @param includeFiscalYears the includeFiscalYears to set
     */
    public void setIncludeFiscalYears(Boolean includeFiscalYears) {
        this.includeFiscalYears = includeFiscalYears;
    }

    /**
     * @return the includeFiscalQuarters
     */
    public Boolean getIncludeFiscalQuarters() {
        return includeFiscalQuarters;
    }

    /**
     * @param includeFiscalQuarters the includeFiscalQuarters to set
     */
    public void setIncludeFiscalQuarters(Boolean includeFiscalQuarters) {
        this.includeFiscalQuarters = includeFiscalQuarters;
    }

    /**
     * @return the startYear
     */
    public int getStartYear() {
        return startYear;
    }

    /**
     * @param startYear the startYear to set
     */
    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    /**
     * @return the endYear
     */
    public int getEndYear() {
        return endYear;
    }

    /**
     * @param endYear the endYear to set
     */
    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    /**
     * @return the entityList
     */
    public List<String> getEntityList() {
        return entityList;
    }

    /**
     * @param entityList the entityList to set
     */
    public void setEntityList(List<String> entityList) {
        this.entityList = entityList;
    }

    /**
     * @return the termIdList
     */
    public List<String> getTermIdList() {
        return termIdList;
    }

    /**
     * @param termIdList the termIdList to set
     */
    public void setTermIdList(List<String> termIdList) {
        this.termIdList = termIdList;
    }
    
    @Override
    public String toString() {
        return "Ys:" + includeFiscalYears + ",Qs:" + includeFiscalQuarters +",st:" + startYear + ",end:" + endYear 
                + ",elist:" + entityList + ",tlist:" + termIdList+ ",entityId:" + entityId + ",termId:" + termId;
    }

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
     * @return the exportType
     */
    public String getExportType() {
        return exportType;
    }

    /**
     * @param exportType the exportType to set
     */
    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    /**
     * @return the isForAllEntities
     */
    public Boolean getIsForAllEntities() {
        return isForAllEntities;
    }

    /**
     * @param isForAllEntities the isForAllEntities to set
     */
    public void setIsForAllEntities(Boolean isForAllEntities) {
        this.isForAllEntities = isForAllEntities;
    }
}
