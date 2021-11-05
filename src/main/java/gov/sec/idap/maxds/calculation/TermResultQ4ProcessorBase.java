/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import gov.sec.idap.maxds.domain.FQTypeCode;
import gov.sec.idap.maxds.domain.PeriodType;
import gov.sec.idap.maxds.solr.document.TermResultsDoc;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author SRIKANTH
 */
public abstract class TermResultQ4ProcessorBase {
    
   
   
    
    protected HashMap<String, TermResultsDoc> termResultsMap;
    protected HashMap<String, TermResultsDoc> q4TermResultsMap;
    
    public TermResultQ4ProcessorBase(HashMap<String, TermResultsDoc> origResults)
    {
        termResultsMap = origResults;
        q4TermResultsMap = new HashMap<>();
    }
    
    
    public HashMap<String, TermResultsDoc>  addQ4Results()
    {
        for (Map.Entry<String, TermResultsDoc> entry : termResultsMap.entrySet()) 
        {
            String key = entry.getKey();
            if(! key.contains("FY")) continue;
            
            TermResultsDoc value = entry.getValue();
            
            String newkey = key.replace("FY", "Q4");
            if( termResultsMap.containsKey(newkey))
            {
                //we already have Q4 , this is possible if we are using formula and
                //we have q4 for the other terms...
                continue;
            }
            BuildQ4Value(value, key, newkey);
        }
        
        return mergeQ4Results();
        
    }

    protected abstract void BuildQ4Value(TermResultsDoc value, String fyKey, String q4Key);
     
    protected HashMap<String, TermResultsDoc> mergeQ4Results()
    {
        for (Map.Entry<String, TermResultsDoc> entry : q4TermResultsMap.entrySet()) 
        {
            termResultsMap.put(entry.getKey(), entry.getValue());
            
        }
  
        return termResultsMap;
    }
    
    protected void addInstanceQ4Value( TermResultsDoc fyResult, String q4Key)
    {
        if( fyResult.periodType == PeriodType.instant)
        {
            //need to create a copy of the term result and call it Q4
            TermResultsDoc q4Val = cloneTermResultsDoc(fyResult);
            q4Val.FQ = FQTypeCode.Q4;
            q4Val.resolvedExpression = fyResult.resolvedExpression;
            q4TermResultsMap.put(q4Key,q4Val);
        }
    }
    
    protected TermResultsDoc cloneTermResultsDoc(TermResultsDoc sourceObject) {

        TermResultsDoc ret = new TermResultsDoc();
        ret.myDocType = sourceObject.myDocType;
        ret.FQ = sourceObject.FQ;
        ret.FY = sourceObject.FY;
        ret.termId = sourceObject.termId;
        ret.entityId = sourceObject.entityId;
        ret.termName = sourceObject.termName;
        ret.cik = sourceObject.cik;
        ret.stockSymbol = sourceObject.stockSymbol;
        ret.companyName = sourceObject.companyName;
        ret.periodType = sourceObject.periodType;
        ret.rank = sourceObject.rank;
        ret.effectiveRank = sourceObject.effectiveRank;
        ret.expType = sourceObject.expType;
        ret.expression = sourceObject.expression;
        ret.resolvedExpression = sourceObject.resolvedExpression;
        ret.elementName = sourceObject.elementName;
        ret.dimExpression = sourceObject.dimExpression;
        ret.dimMemberName = sourceObject.dimMemberName;
        ret.hasDimensionalData = sourceObject.hasDimensionalData;
        ret.lastModified = sourceObject.lastModified;
        ret.isFilingPeriod = sourceObject.isFilingPeriod;
        ret.value = sourceObject.value;
        ret.filingDate = sourceObject.filingDate;
        ret.percentVarianceWithPrevPeriod = sourceObject.percentVarianceWithPrevPeriod;
        ret.percentVarianceWithPrevYear = sourceObject.percentVarianceWithPrevYear;
        ret.validationStatus = sourceObject.validationStatus;
        ret.validationMessages = sourceObject.validationMessages;

        return ret;
    }
}
