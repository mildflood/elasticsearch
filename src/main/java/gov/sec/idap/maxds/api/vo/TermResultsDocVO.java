package gov.sec.idap.maxds.api.vo;

import java.util.Date;
import java.util.List;

import gov.sec.idap.maxds.domain.FQTypeCode;
import gov.sec.idap.maxds.domain.PeriodType;
import gov.sec.idap.maxds.domain.TermExpressionTypeCode;
import gov.sec.idap.maxds.domain.ValidationStatus;
import gov.sec.idap.maxds.model.DerivationTrail;
 
public class TermResultsDocVO
{
    public String id;
    public DerivationTrailVO derivationTrails;   
    public gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc.TermResultsDocType myDocType = gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc.TermResultsDocType.primaryTermResult;

    public String entityId;
    
    public String termId;
    
    public int FY = 0;
    
    public FQTypeCode FQ = FQTypeCode.na;
    
    public String termName;
    
    public Long cik;
    
    public String stockSymbol;
    
    public String companyName;
    
    public PeriodType periodType = PeriodType.na;
    
    public int rank;
    
    public int effectiveRank;
    
    public TermExpressionTypeCode expType = TermExpressionTypeCode.na;
    
    public String expression;
    
    public String resolvedExpression;
    
    public String elementName;
    
    public String dimExpression;
    
    public String dimMemberName;
    
    public Boolean hasDimensionalData = false;
    
    public Date lastModified = new Date();
    
    public Boolean isFilingPeriod = true;
    
    public double value = 0;
    
    public int filingDate;
    
    public double percentVarianceWithPrevPeriod = Double.MIN_VALUE;
    
    public double percentVarianceWithPrevYear = Double.MIN_VALUE;
    
    public List<String> validationMessages = null;
    
    public ValidationStatus validationStatus = ValidationStatus.na;
    
    public String verifiedNotes;
    
    public String periodEndDate;

    public String accession;

    public enum TermResultsDocType {

        primaryTermResult,
        OtherTermResult,
        derivedZeroTermResult,
        derivedNonZeroTermResult,
        derivedZeroHasTermResult,
    }
    public TermResultsDocVO() {

    }

    @Override
    public String toString() {
        return "TermResultsDoc{" + "termId=" + termId + ", entityId=" + entityId + ", termId=" + termId + ", FY=" + FY + ", FQ=" + FQ + ", rank=" + rank + ", validationStatus=" + validationStatus + '}';
    }
    
}
