/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import gov.sec.idap.maxds.elasticsearch.document.Entity;

/**
 *
 * @author SRIKANTH
 */
public class EntityOverride {
    
    
        private String division ;
        private String sector ;
        private String industry ;
        private String entityId ;
        
        
    public Boolean isMatch(Entity entity)
    {
        if( entityId != null && !entityId.equals(entity.getEntityId())) return false;
        if( sector != null && !sector.equals(entity.getSector())) return false;
        if( industry != null && !industry.equals(entity.getIndustry())) return false;
        if( division != null && !division.equals(entity.getDivision())) return false;
        
        
        
        return true;
    }

    /**
     * @return the division
     */
    public String getDivision() {
        return division;
    }

    /**
     * @param division the division to set
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * @return the sector
     */
    public String getSector() {
        return sector;
    }

    /**
     * @param sector the sector to set
     */
    public void setSector(String sector) {
        this.sector = sector;
    }

    /**
     * @return the industry
     */
    public String getIndustry() {
        return industry;
    }

    /**
     * @param industry the industry to set
     */
    public void setIndustry(String industry) {
        this.industry = industry;
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
    
}
