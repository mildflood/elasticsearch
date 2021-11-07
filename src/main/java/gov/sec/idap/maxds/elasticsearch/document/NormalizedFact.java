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
@Document(indexName = Indices.LOOKUP_REFERENCES_INDEX)
@Setting(settingPath = "static/es-settings.json")
public class NormalizedFact {
	public String id;

    @JsonProperty("sub_cik")
    @Field(type = FieldType.Integer)
    public Integer cik;

    @JsonProperty("cell_elt")
    @Field(type = FieldType.Text)
    public String elementName;
    
    @JsonProperty("sub_act_standard")
    @Field(type = FieldType.Text)
    public String accountingStandard;
    
    @JsonProperty("num_ddate")
    @Field(type = FieldType.Text)
    public String periodEndDate;
    
    @JsonProperty("fact_fy_i")
    @Field(type = FieldType.Integer)
    public int FY;

    @JsonProperty("fact_fp")
    @Field(type = FieldType.Object)
    public FQTypeCode FQ = FQTypeCode.na;

    @JsonProperty("num_value")
    @Field(type = FieldType.Double)
    public double value = 0;

    @JsonProperty("tag_custom")
    @Field(type = FieldType.Boolean)
    public Boolean isExtended;

    @JsonProperty("tag_datatype")
    @Field(type = FieldType.Text)
    public String elementType;
    
    @JsonProperty("sub_name")
    @Field(type = FieldType.Text)
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCik() {
		return cik;
	}

	public void setCik(Integer cik) {
		this.cik = cik;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getAccountingStandard() {
		return accountingStandard;
	}

	public void setAccountingStandard(String accountingStandard) {
		this.accountingStandard = accountingStandard;
	}

	public String getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(String periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public int getFY() {
		return FY;
	}

	public void setFY(int fY) {
		FY = fY;
	}

	public FQTypeCode getFQ() {
		return FQ;
	}

	public void setFQ(FQTypeCode fQ) {
		FQ = fQ;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Boolean getIsExtended() {
		return isExtended;
	}

	public void setIsExtended(Boolean isExtended) {
		this.isExtended = isExtended;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
