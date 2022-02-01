package gov.sec.idap.maxds.elasticsearch.document;

import gov.sec.idap.maxds.api.vo.TermResultsDocVO;
import gov.sec.idap.maxds.api.vo.TermResultsDocVO.TermResultsDocType;
import gov.sec.idap.maxds.domain.FQTypeCode;
import gov.sec.idap.maxds.domain.PeriodType;
import gov.sec.idap.maxds.domain.TermExpressionTypeCode;
import gov.sec.idap.maxds.domain.ValidationStatus;
import gov.sec.idap.maxds.elasticsearch.helper.Indices;
import gov.sec.idap.maxds.model.DerivationTrail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
@Document(indexName = Indices.TERM_RESULT_INDEX)
@Setting(settingPath = "static/es-settings.json")
public class TermResultsDoc implements Comparator<TermResultsDoc> {
	@Override
	public int compare(TermResultsDoc o1, TermResultsDoc o2) {
		int ret = o1.entityId.compareTo(o2.entityId);
		if (ret != 0) {
			return ret;
		}
		ret = Integer.compare(o1.FY, o2.FY);
		if (ret != 0) {
			return ret;
		}
		return o1.FQ.compareTo(o2.FQ);
	}

//	public enum TermResultsDocType {
//		primaryTermResult, OtherTermResult, derivedZeroTermResult, derivedNonZeroTermResult, derivedZeroHasTermResult,
//	}

	public TermResultsDoc() {
	}

	@Id
	@Field(type = FieldType.Keyword)
	private String id;

	@Field
	public DerivationTrail derivationTrails;

	@JsonProperty("entityId")
	@Field(type = FieldType.Text)
	private String entityId;

	@JsonProperty("myDocType")
	@Field
	public TermResultsDocType myDocType = TermResultsDocType.primaryTermResult;

	@JsonProperty("termId")
	@Field(type = FieldType.Text)
	public String termId;

	@JsonProperty("FY")
	@Field(type = FieldType.Integer)
	public int FY = 0;

	@JsonProperty("FQ")
	@Field
	public FQTypeCode FQ = FQTypeCode.na;

	@JsonProperty("termName")
	@Field(type = FieldType.Text)
	public String termName;

	@JsonProperty("cik")
	@Field(type = FieldType.Text)
	public String cik;

	@JsonProperty("stockSymbol")
	@Field(type = FieldType.Text)
	public String stockSymbol;

	@JsonProperty("companyName")
	@Field(type = FieldType.Text)
	public String companyName;

	@JsonProperty("periodType")
	@Field
	public PeriodType periodType = PeriodType.na;

	@JsonProperty("rank")
	@Field(type = FieldType.Integer)
	public int rank;

	@JsonProperty("effectiveRank")
	@Field(type = FieldType.Integer)
	public int effectiveRank;

	@JsonProperty("expType")
	@Field
	public TermExpressionTypeCode expType = TermExpressionTypeCode.na;

	@JsonProperty("expression")
	@Field(type = FieldType.Text)
	public String expression;

	@JsonProperty("resolvedExpression")
	@Field(type = FieldType.Text)
	public String resolvedExpression;

	@JsonProperty("elementName")
	@Field(type = FieldType.Text)
	public String elementName;

	@JsonProperty("dimExpression")
	@Field(type = FieldType.Text)
	public String dimExpression;

	@JsonProperty("dimMemberName")
	@Field(type = FieldType.Text)
	public String dimMemberName;

	@JsonProperty("hasDimensionalData")
	@Field(type = FieldType.Boolean)
	public Boolean hasDimensionalData = false;

	@JsonProperty("lastModified")
	@Field(type = FieldType.Date)
	public Date lastModified = new Date();

	@JsonProperty("isFilingPeriod")
	@Field(type = FieldType.Boolean)
	public Boolean isFilingPeriod = true;

	@JsonProperty("value")
	@Field(type = FieldType.Double)
	public double value = 0;

	@JsonProperty("filingDate")
	@Field(type = FieldType.Integer)
	public int filingDate;

	@JsonProperty("percentVarianceWithPrevPeriod")
	@Field(type = FieldType.Double)
	public double percentVarianceWithPrevPeriod = Double.MIN_VALUE;

	@JsonProperty("percentVarianceWithPrevYear")
	@Field(type = FieldType.Double)
	public double percentVarianceWithPrevYear = Double.MIN_VALUE;

	@JsonProperty("validationMessages")
	@Field(type = FieldType.Object)
	public List<String> validationMessages = new ArrayList<String>();

	@JsonProperty("validationStatus")
	@Field(type = FieldType.Object)
	public ValidationStatus validationStatus = ValidationStatus.na;

	@JsonProperty("verifiedNotes")
	@Field(type = FieldType.Text)
	public String verifiedNotes;

	@JsonProperty("periodEndDate_s")
	@Field(type = FieldType.Text)
	public String periodEndDate;

	@JsonProperty("accession_s")
	@Field(type = FieldType.Text)
	public String accession;

	public String getCikString() {
		return StringUtils.leftPad(cik, 10, "0");
	}

	// For API return VO. Adjust data and format for API needs
	public TermResultsDocVO createVO() {
		TermResultsDocVO ret = new TermResultsDocVO();
		ret.derivationTrails = this.derivationTrails.createVO();
		ret.myDocType = this.myDocType;
		ret.FQ = this.FQ;
		ret.FY = this.FY;
		ret.termId = this.termId;
		ret.entityId = this.entityId;
		ret.termName = this.termName;
		ret.cik = Long.parseLong(this.cik == null ? "0000000000" : this.cik);
		ret.stockSymbol = this.stockSymbol;
		ret.companyName = this.companyName;
		ret.periodType = this.periodType;
		ret.rank = this.rank;
		ret.effectiveRank = this.effectiveRank;
		ret.expType = this.expType;
		ret.expression = this.expression;
		ret.resolvedExpression = this.resolvedExpression;
		ret.elementName = this.elementName;
		ret.dimExpression = this.dimExpression;
		ret.dimMemberName = this.dimMemberName;
		ret.hasDimensionalData = this.hasDimensionalData;
		ret.lastModified = this.lastModified;
		ret.isFilingPeriod = this.isFilingPeriod;
		ret.value = this.value;
		ret.filingDate = this.filingDate;
		ret.percentVarianceWithPrevPeriod = this.percentVarianceWithPrevPeriod;
		ret.percentVarianceWithPrevYear = this.percentVarianceWithPrevYear;
		ret.validationStatus = this.validationStatus;
		ret.validationMessages = this.validationMessages;

		return ret;
	}

	@Override
	public String toString() {
		return "TermResultsDoc{" + "termId=" + termId + ", entityId=" + entityId + ", termId=" + termId + ", FY=" + FY
				+ ", FQ=" + FQ + ", rank=" + rank + ", validationStatus=" + validationStatus + '}';
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DerivationTrail getDerivationTrails() {
		return derivationTrails;
	}

	public void setDerivationTrails(DerivationTrail derivationTrails) {
		this.derivationTrails = derivationTrails;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public TermResultsDocType getMyDocType() {
		return myDocType;
	}

	public void setMyDocType(TermResultsDocType myDocType) {
		this.myDocType = myDocType;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
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

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getCik() {
		return cik;
	}

	public void setCik(String cik) {
		this.cik = cik;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public PeriodType getPeriodType() {
		return periodType;
	}

	public void setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getEffectiveRank() {
		return effectiveRank;
	}

	public void setEffectiveRank(int effectiveRank) {
		this.effectiveRank = effectiveRank;
	}

	public TermExpressionTypeCode getExpType() {
		return expType;
	}

	public void setExpType(TermExpressionTypeCode expType) {
		this.expType = expType;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getResolvedExpression() {
		return resolvedExpression;
	}

	public void setResolvedExpression(String resolvedExpression) {
		this.resolvedExpression = resolvedExpression;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getDimExpression() {
		return dimExpression;
	}

	public void setDimExpression(String dimExpression) {
		this.dimExpression = dimExpression;
	}

	public String getDimMemberName() {
		return dimMemberName;
	}

	public void setDimMemberName(String dimMemberName) {
		this.dimMemberName = dimMemberName;
	}

	public Boolean getHasDimensionalData() {
		return hasDimensionalData;
	}

	public void setHasDimensionalData(Boolean hasDimensionalData) {
		this.hasDimensionalData = hasDimensionalData;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Boolean getIsFilingPeriod() {
		return isFilingPeriod;
	}

	public void setIsFilingPeriod(Boolean isFilingPeriod) {
		this.isFilingPeriod = isFilingPeriod;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getFilingDate() {
		return filingDate;
	}

	public void setFilingDate(int filingDate) {
		this.filingDate = filingDate;
	}

	public double getPercentVarianceWithPrevPeriod() {
		return percentVarianceWithPrevPeriod;
	}

	public void setPercentVarianceWithPrevPeriod(double percentVarianceWithPrevPeriod) {
		this.percentVarianceWithPrevPeriod = percentVarianceWithPrevPeriod;
	}

	public double getPercentVarianceWithPrevYear() {
		return percentVarianceWithPrevYear;
	}

	public void setPercentVarianceWithPrevYear(double percentVarianceWithPrevYear) {
		this.percentVarianceWithPrevYear = percentVarianceWithPrevYear;
	}

	public List<String> getValidationMessages() {
		return validationMessages;
	}

	public void setValidationMessages(List<String> validationMessages) {
		this.validationMessages = validationMessages;
	}

	public ValidationStatus getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(ValidationStatus validationStatus) {
		this.validationStatus = validationStatus;
	}

	public String getVerifiedNotes() {
		return verifiedNotes;
	}

	public void setVerifiedNotes(String verifiedNotes) {
		this.verifiedNotes = verifiedNotes;
	}

	public String getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(String periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

}
