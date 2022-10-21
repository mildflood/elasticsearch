/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextBlockInfo {
    
     @JsonProperty("sub_cik")
    public Integer cik;
     
     
   
    
      @JsonProperty("fact_fy_i")
     public int FY = 0;
      
       @JsonProperty("fact_fp")
     public String FQ;
     
     @JsonProperty("cell_elt")
    public String textBlockName;
     
      public String getEntityId() {
        return StringUtils.leftPad(cik.toString(), 10, "0");
    }
}
