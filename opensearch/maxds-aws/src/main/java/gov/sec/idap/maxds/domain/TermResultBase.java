/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 *
 * @author SRIKANTH
 */
public class TermResultBase  implements Comparator<TermResultBase> {

    @Override
    public int compare(TermResultBase o1, TermResultBase o2) {
        
        int ret = o1.entityId.compareTo(o2.entityId);
        if( ret != 0) return ret;
        
        ret = Integer.compare(o1.FY, o2.FY);
        if( ret != 0) return ret;
        
        return o1.FQ.compareTo(o2.FQ);
        
    }
    
     public String entityId;
     public String termId;
     public int FY = 0;
     public FQTypeCode FQ = FQTypeCode.na;
     

     public String termName;
     public String cik;
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
     public String periodEndDate;
     public String accession;
     public Boolean isFilingPeriod = true;

     public double value = 0;
      public int filingDate;
     public double percentVarianceWithPrevPeriod = Double.MIN_VALUE;
     public double percentVarianceWithPrevYear = Double.MIN_VALUE;
     
     public List<String> validationMessages = null;
     
     public ValidationStatus validationStatus = ValidationStatus.na;
     
     
    public void setFQEnum(String fqStr) {
        this.FQ = FQTypeCode.FY;
        switch (fqStr) {
            case "Q1":
                FQ = FQTypeCode.Q1;
                break;
            case "Q2":
                FQ = FQTypeCode.Q2;
                break;
            case "Q3":
                FQ = FQTypeCode.Q3;
                break;
            case "Q4":
                FQ = FQTypeCode.Q4;
                break;
        }
    }
     
}
