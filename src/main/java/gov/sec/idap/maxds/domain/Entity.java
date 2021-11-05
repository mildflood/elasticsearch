/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.Objects;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;




@JsonIgnoreProperties(ignoreUnknown = true)
public class Entity {
    
	@Id
	private String id;    
        private String entityId ;
        private String cik ;
        private String altCik;
        
        private String companyName ;
       
        private String division ;
        private String sector ;
        private String industry ;
        private String sic ;
        private String state ;
      
        private String filerCategory ;
        public String geoState;
        public Long _version_;
        public String tradingSymbol;
        public ValidationStatus validationStatus = ValidationStatus.na;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public String getAltCik() {
        return altCik;
    }

    public void setAltCik(String altCik) {
        this.altCik = altCik;
    }

  
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getSic() {
        return sic;
    }

    
    public void setSic(String sic) {
        this.sic = sic;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

  

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.entityId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.entityId, other.entityId)) {
            return false;
        }
        return true;
    }

    /**
     * @return the filerCategory
     */
    public String getFilerCategory() {
        return filerCategory;
    }
        
        
        
}
