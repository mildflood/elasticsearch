/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;




@JsonIgnoreProperties(ignoreUnknown = true)
@SolrDocument(collection = "Idap_FavoredFacts")
public class NormalizedFact {

    public String id;

    @JsonProperty("sub_cik")
    @Field("sub_cik")
    public Integer cik;

    @JsonProperty("cell_elt")
    @Field("cell_elt")
    public String elementName;
    
    @JsonProperty("sub_act_standard")
    @Field("sub_act_standard")
    public String accountingStandard;
    
    
    @JsonProperty("num_ddate")
    @Field("num_ddate")
    public String periodEndDate;
    
    

    @JsonProperty("fact_fy_i")
    @Field("fact_fy_i")
    public int FY;

    @JsonProperty("fact_fp")
    @Field("fact_fp")
    public FQTypeCode FQ = FQTypeCode.na;

    @JsonProperty("num_value")
    @Field("num_value")
    public double value = 0;

    @JsonProperty("tag_custom")
    @Field("tag_custom")
    public Boolean isExtended;

    @JsonProperty("tag_datatype")
    @Field("tag_datatype")
    public String elementType;
    
    @JsonProperty("sub_name")
    @Field("sub_name")
    public String companyName;

    public String tag_crdr;
    public String tag_iord;
    public Date sub_filed_dt;
    public String num_uom;

    public String dim_segments;
    public String sub_adsh;
    

       
    //Fact Level Properties
   

   
    public List<FactDimension> dimensions = null;
    public String dimensionAxisKey = null;

    public List<NormalizedFact> dimensionalFacts = null;

    public String getCikString() {
        return StringUtils.leftPad(cik.toString(), 10, "0");
    }

    public String getFiscalKey() {
        return String.format("%d:%s", this.FY, this.FQ);
    }
    
     public String getFiscalKeyWithEntity() {
        return String.format("%s:%d:%s",this.getCikString(), this.FY, this.FQ);
    }

    public void PopulateDimensionalInfo() {

        if (dim_segments == null) {
            return;
        }
        this.dimensions = new ArrayList<>();
        //FiniteLivedIntangibleAssetsByMajorClass=DevelopedTechnologyRights;
        //"BalanceSheetLocation=OtherCurrentLiabilities;FiniteLivedIntangibleAssetsByMajorClass=DevelopedTechnologyRights;ProductOrService=Besponsa;"
        //dim_segments  needs to be split into axis dim pairs...
        String[] segments = this.dim_segments.split(";");

        if (segments == null) {
            return;
        }
        for (String segment : segments) {
            if (segment == null) {
                return;
            }

            String[] pair = segment.split("=");

            if (pair != null && pair.length == 2) {
                FactDimension fd = new FactDimension();
                fd.dimName = pair[0];
                fd.memberName = pair[1];

                this.dimensions.add(fd);
                
                if( dimensionAxisKey == null)
                {
                    dimensionAxisKey = fd.dimName;
                }
                else
                {
                    dimensionAxisKey += fd.dimName;
                }
            }
        }
    }

    public String getDimensionDisplayInfo() {
        StringBuilder str = new StringBuilder();

        if (this.dimensions != null) {
            for (FactDimension fd : this.dimensions) {
                str.append(fd.toString());
                str.append(" ");
            }
        }

        return str.toString();
    }
    
    public int buildFilingDateInt()
    {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(sub_filed_dt);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH)+1;
        if(day > 31) {
        	month = month + 1;
        	day = 1;
        }
        
       return year*10000+month*100+day;
                
        
    }
    
    public PeriodType getPeriodType()
    {
        
        if( tag_iord != null )
        {
            return tag_iord.equals("I") ? PeriodType.instant : PeriodType.duration ;
        }
        
        return PeriodType.na;
    }

}
