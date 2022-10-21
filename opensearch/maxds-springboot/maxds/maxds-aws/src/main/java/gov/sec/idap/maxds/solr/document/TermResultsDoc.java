/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.document;

import gov.sec.idap.maxds.api.vo.TermResultsDocVO;
import gov.sec.idap.maxds.api.vo.TermResultsDocVO.TermResultsDocType;
import gov.sec.idap.maxds.domain.FQTypeCode;
import gov.sec.idap.maxds.domain.PeriodType;
import gov.sec.idap.maxds.domain.TermExpressionTypeCode;

import gov.sec.idap.maxds.domain.ValidationStatus;
import gov.sec.idap.maxds.model.DerivationTrail;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "MaxDS_TermResults")
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

    @Id
    public String id;

    @Field
    public DerivationTrail derivationTrails;

    @Field
    public TermResultsDocType myDocType = TermResultsDocType.primaryTermResult;

    @Field
    public String entityId;
    @Field
    public String termId;
    @Field
    public int FY = 0;
    @Field
    public FQTypeCode FQ = FQTypeCode.na;
    @Field
    public String termName;
    @Field
    public String cik;
    @Field
    public String stockSymbol;
    @Field
    public String companyName;
    @Field
    public PeriodType periodType = PeriodType.na;
    @Field
    public int rank;
    @Field
    public int effectiveRank;
    @Field
    public TermExpressionTypeCode expType = TermExpressionTypeCode.na;
    @Field
    public String expression;
    @Field
    public String resolvedExpression;
    @Field
    public String elementName;
    @Field
    public String dimExpression;
    @Field
    public String dimMemberName;
    @Field
    public Boolean hasDimensionalData = false;
    @Field
    public Date lastModified = new Date();
    @Field
    public Boolean isFilingPeriod = true;
    @Field
    public double value = 0;
    @Field
    public int filingDate;
    @Field
    public double percentVarianceWithPrevPeriod = Double.MIN_VALUE;
    @Field
    public double percentVarianceWithPrevYear = Double.MIN_VALUE;
    @Field
    public List<String> validationMessages = null;
    @Field
    public ValidationStatus validationStatus = ValidationStatus.na;
    @Field
    public String verifiedNotes;
    @Field("periodEndDate_s")
    public String periodEndDate;
    
    @Field("accession_s")
    public String accession;
    
    public String getCikString() {
        return StringUtils.leftPad(cik, 10, "0");
    }

//    public enum TermResultsDocType {
//
//        primaryTermResult,
//        OtherTermResult,
//        derivedZeroTermResult,
//        derivedNonZeroTermResult,
//        derivedZeroHasTermResult,
//    }
    public TermResultsDoc() {

    }
	
//     public void setFQEnum(String fqStr) {
//        this.FQ = FQTypeCode.FY;
//        switch (fqStr) {
//            case "Q1":
//                FQ = FQTypeCode.Q1;
//                break;
//            case "Q2":
//                FQ = FQTypeCode.Q2;
//                break;
//            case "Q3":
//                FQ = FQTypeCode.Q3;
//                break;
//            case "Q4":
//                FQ = FQTypeCode.Q4;
//                break;
//        }
//    }	
    
    //For API return VO. Adjust data and format for API needs
    public TermResultsDocVO createVO() {
        TermResultsDocVO ret = new TermResultsDocVO();
        ret.derivationTrails = this.derivationTrails.createVO();
        ret.myDocType = this.myDocType;
        ret.FQ = this.FQ;
        ret.FY = this.FY;
        ret.termId = this.termId;
        ret.entityId = this.entityId;
        ret.termName = this.termName;
        ret.cik = Long.parseLong(this.cik == null? "0000000000": this.cik);
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
        return "TermResultsDoc{" + "termId=" + termId + ", entityId=" + entityId + ", termId=" + termId + ", FY=" + FY + ", FQ=" + FQ + ", rank=" + rank + ", validationStatus=" + validationStatus + '}';
    }
    
    
}
