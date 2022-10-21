/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.text.DecimalFormat;
import java.util.List;

public class TermResult extends  TermResultBase {
	 
    public static DecimalFormat decimalFormat = new DecimalFormat("#,##0.####");
    
    public String id = null;
     

     public List<TermResult> otherTermResults = null;

     
     
     public TermResult CreateCopy()
     {
         TermResult ret = new TermResult();
        ret.FQ = this.FQ;
        ret.FY = this.FY;
        ret.rank = this.rank;
        ret.termId = this.termId;
        ret.value = this.value;
        ret.companyName = this.companyName;
        ret.entityId = this.entityId;
                 ret.filingDate = this.filingDate;
        ret.termName = this.termName;
        ret.expression = this.expression;
        ret.expType = this.expType;
        ret.stockSymbol = this.stockSymbol;
        ret.cik = this.cik;
        ret.elementName = this.elementName;
        ret.periodType = this.periodType;
        
        ret.dimMemberName = this.dimMemberName;
        ret.dimExpression = this.dimExpression;
        ret.hasDimensionalData = this.hasDimensionalData;
        ret.isFilingPeriod = this.isFilingPeriod;
        
         return ret;
     }

}
