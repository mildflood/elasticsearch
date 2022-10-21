package gov.sec.idap.maxds.elasticsearch.document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.sec.idap.maxds.domain.FQTypeCode;
import gov.sec.idap.maxds.domain.FactDimension;
import gov.sec.idap.maxds.domain.PeriodType;
import gov.sec.idap.maxds.elasticsearch.helper.Indices;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Component;

@Component
@Document(indexName = Indices.NORMALIZED_FACT_INDEX)
@Setting(settingPath = "es-settings.json")
public class NormalizedFact {
	
	@Id
    @Field(type = FieldType.Keyword)
    private String id;

    @JsonProperty("sub_cik")
    @Field(type = FieldType.Long)
    private Integer sub_cik;

    @JsonProperty("cell_elt")
    @Field(type = FieldType.Keyword)
    private String cell_elt;
    
    @JsonProperty("sub_act_standard")
    @Field(type = FieldType.Keyword)
    private String sub_act_standard;
    
    @JsonProperty("num_ddate")
    @Field(type = FieldType.Keyword)
    private String num_ddate;
    //public String periodEndDate;
    
    @JsonProperty("fact_fy_i")
    @Field(type = FieldType.Long)
    private int fact_fy_i;

    @JsonProperty("fact_fp")
    @Field(type = FieldType.Keyword)
    private FQTypeCode fact_fp = FQTypeCode.na;

    @JsonProperty("num_value")
    @Field(type = FieldType.Float)
    private double num_value = 0;

    @JsonProperty("tag_custom")
    @Field(type = FieldType.Boolean)
    private Boolean tag_custom;
    //public Boolean isExtended;

    @JsonProperty("tag_datatype")
    @Field(type = FieldType.Keyword)
    //public String elementType;
    private String tag_datatype;
    
    @JsonProperty("sub_name")
    @Field(type = FieldType.Keyword)
    //public String companyName;
    private String sub_name;
    
    @JsonProperty("tag_crdr")
    @Field(type = FieldType.Keyword)
    private String tag_crdr;
    
    @JsonProperty("tag_iord")
    @Field(type = FieldType.Keyword)
    private String tag_iord;

    @JsonProperty("sub_filed_dt")
    @Field(type = FieldType.Date)
    private Date sub_filed_dt;
    
    @JsonProperty("num_uom")
    @Field(type = FieldType.Keyword)
    private String num_uom;

    @JsonProperty("dim_segments")
    @Field(type = FieldType.Keyword)
    private String dim_segments;
    
    @JsonProperty("sub_adsh")
    @Field(type = FieldType.Keyword)
    private String sub_adsh;
    
    @JsonProperty("num_qtrs")
    @Field(type = FieldType.Long)
    private String num_qtrs;
    
    @JsonProperty("sub_form")
    @Field(type = FieldType.Keyword)
    private String sub_form;
    
    @JsonProperty("sub_fyend")
    @Field(type = FieldType.Keyword)
    private String sub_fyend;

    @JsonProperty("sub_filed")
    @Field(type = FieldType.Keyword)
    private String sub_filed;

    @JsonProperty("dtCreated")
    @Field(type = FieldType.Date)
    private Date dtCreated;

    @JsonProperty("factkey")
    @Field(type = FieldType.Keyword)
    private String factkey;
    
    //Fact Level Properties

    public List<FactDimension> dimensions = null;
    public String dimensionAxisKey = null;

    public List<NormalizedFact> dimensionalFacts = null;
    
    public String getCikString() {
        return StringUtils.leftPad(sub_cik.toString(), 10, "0");
    }

    public String getFiscalKey() {
        return String.format("%d:%s", this.fact_fy_i, this.fact_fp);
    }
    
     public String getFiscalKeyWithEntity() {
        return String.format("%s:%d:%s",this.getCikString(), this.fact_fy_i, this.fact_fp);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getSub_cik() {
		return sub_cik;
	}

	public void setSub_cik(Integer sub_cik) {
		this.sub_cik = sub_cik;
	}

	public String getCell_elt() {
		return cell_elt;
	}

	public void setCell_elt(String cell_elt) {
		this.cell_elt = cell_elt;
	}

	public String getSub_act_standard() {
		return sub_act_standard;
	}

	public void setSub_act_standard(String sub_act_standard) {
		this.sub_act_standard = sub_act_standard;
	}

	public String getNum_ddate() {
		return num_ddate;
	}

	public void setNum_ddate(String num_ddate) {
		this.num_ddate = num_ddate;
	}

	public int getFact_fy_i() {
		return fact_fy_i;
	}

	public void setFact_fy_i(int fact_fy_i) {
		this.fact_fy_i = fact_fy_i;
	}

	public FQTypeCode getFact_fp() {
		return fact_fp;
	}

	public void setFact_fp(FQTypeCode fact_fp) {
		this.fact_fp = fact_fp;
	}

	public double getNum_value() {
		return num_value;
	}

	public void setNum_value(double num_value) {
		this.num_value = num_value;
	}

	public Boolean getTag_custom() {
		return tag_custom;
	}

	public void setTag_custom(Boolean tag_custom) {
		this.tag_custom = tag_custom;
	}

	public String getTag_datatype() {
		return tag_datatype;
	}

	public void setTag_datatype(String tag_datatype) {
		this.tag_datatype = tag_datatype;
	}

	public String getSub_name() {
		return sub_name;
	}

	public void setSub_name(String sub_name) {
		this.sub_name = sub_name;
	}

	public String getTag_crdr() {
		return tag_crdr;
	}

	public void setTag_crdr(String tag_crdr) {
		this.tag_crdr = tag_crdr;
	}

	public String getTag_iord() {
		return tag_iord;
	}

	public void setTag_iord(String tag_iord) {
		this.tag_iord = tag_iord;
	}

	public Date getSub_filed_dt() {
		return sub_filed_dt;
	}

	public void setSub_filed_dt(Date sub_filed_dt) {
		this.sub_filed_dt = sub_filed_dt;
	}

	public String getNum_uom() {
		return num_uom;
	}

	public void setNum_uom(String num_uom) {
		this.num_uom = num_uom;
	}

	public String getDim_segments() {
		return dim_segments;
	}

	public void setDim_segments(String dim_segments) {
		this.dim_segments = dim_segments;
	}

	public String getSub_adsh() {
		return sub_adsh;
	}

	public void setSub_adsh(String sub_adsh) {
		this.sub_adsh = sub_adsh;
	}

	public String getNum_qtrs() {
		return num_qtrs;
	}

	public void setNum_qtrs(String num_qtrs) {
		this.num_qtrs = num_qtrs;
	}

	public String getSub_form() {
		return sub_form;
	}

	public void setSub_form(String sub_form) {
		this.sub_form = sub_form;
	}

	public String getSub_fyend() {
		return sub_fyend;
	}

	public void setSub_fyend(String sub_fyend) {
		this.sub_fyend = sub_fyend;
	}

	public String getSub_filed() {
		return sub_filed;
	}

	public void setSub_filed(String sub_filed) {
		this.sub_filed = sub_filed;
	}

	public Date getDtCreated() {
		return dtCreated;
	}

	public void setDtCreated(Date dtCreated) {
		this.dtCreated = dtCreated;
	}

	public String getFactkey() {
		return factkey;
	}

	public void setFactkey(String factkey) {
		this.factkey = factkey;
	}

	public List<FactDimension> getDimensions() {
		return dimensions;
	}

	public void setDimensions(List<FactDimension> dimensions) {
		this.dimensions = dimensions;
	}

	public String getDimensionAxisKey() {
		return dimensionAxisKey;
	}

	public void setDimensionAxisKey(String dimensionAxisKey) {
		this.dimensionAxisKey = dimensionAxisKey;
	}

	public List<NormalizedFact> getDimensionalFacts() {
		return dimensionalFacts;
	}

	public void setDimensionalFacts(List<NormalizedFact> dimensionalFacts) {
		this.dimensionalFacts = dimensionalFacts;
	}
    
}
