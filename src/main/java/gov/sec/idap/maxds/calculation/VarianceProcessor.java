/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import gov.sec.idap.maxds.solr.document.TermResultsDoc;
import gov.sec.idap.maxds.domain.FQTypeCode;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author SRIKANTH
 */
public class VarianceProcessor {
    
    
    
    HashMap<String, TermResultsDoc> termResultsMap;
     
    
    public VarianceProcessor(HashMap<String, TermResultsDoc> origResults)
    {
        termResultsMap = origResults;
        
    }
    
    public HashMap<String, TermResultsDoc>  addVarianceInfo()
    {
        for (Map.Entry<String, TermResultsDoc> entry : termResultsMap.entrySet()) 
        {
            String key = entry.getKey();
                       
            TermResultsDoc value = entry.getValue();
            value.percentVarianceWithPrevPeriod = Double.MIN_VALUE;
            value.percentVarianceWithPrevYear = Double.MIN_VALUE;
            
           TermResultsDoc valuePrevPeriod = getPreviousPeriod(key, value);
           if( valuePrevPeriod != null && valuePrevPeriod.value != 0)
           {
               
               value.percentVarianceWithPrevPeriod = (value.value - valuePrevPeriod.value)/ valuePrevPeriod.value;
           }
            TermResultsDoc valueSamePeriodPrevYear = getSamePeriodPreviousYear(key, value);
           if( valueSamePeriodPrevYear != null && valueSamePeriodPrevYear.value != 0)
           {
               value.percentVarianceWithPrevYear = (value.value - valueSamePeriodPrevYear.value)/ valueSamePeriodPrevYear.value;
           }
            
        }
        return termResultsMap;
    }
    
    private TermResultsDoc getPreviousPeriod(String key, TermResultsDoc tr)
    {
        String otherKey = getPreviousPeriodKey(tr);
        if( this.termResultsMap.containsKey(otherKey))
        {
            return this.termResultsMap.get(otherKey);
        }
        return null;
    }
    private TermResultsDoc getSamePeriodPreviousYear(String key,TermResultsDoc tr)
    {
        String otherKey = getSamePeriodPreviousYearKey(tr);
        if( this.termResultsMap.containsKey(otherKey))
        {
            return this.termResultsMap.get(otherKey);
        }
        return null;
    }
    
    private String getPreviousPeriodKey(TermResultsDoc tr)
    {
        switch( tr.FQ)
        {
            case Q1:
                return buildTermResultkey(tr.entityId,  FQTypeCode.Q4, tr.FY-1);
            
                
            case Q2:
                return buildTermResultkey(tr.entityId,FQTypeCode.Q1, tr.FY);
                
            case Q3:
               return buildTermResultkey(tr.entityId,FQTypeCode.Q2, tr.FY);
                
            case Q4:
               return buildTermResultkey(tr.entityId,FQTypeCode.Q3, tr.FY);
           case FY:
                return buildTermResultkey(tr.entityId,FQTypeCode.FY, tr.FY-1);
                
        }
        
        return "";
    }
    private String getSamePeriodPreviousYearKey(TermResultsDoc tr)
    {
        switch( tr.FQ)
        {
            case Q1:
                return buildTermResultkey(tr.entityId,FQTypeCode.Q1, tr.FY-1);
            
                
            case Q2:
                return buildTermResultkey(tr.entityId,FQTypeCode.Q2, tr.FY-1);
                
            case Q3:
               return buildTermResultkey(tr.entityId,FQTypeCode.Q3, tr.FY-1);

                
            case Q4:
               return buildTermResultkey(tr.entityId,FQTypeCode.Q4, tr.FY-1);

                
            case FY:
                return buildTermResultkey(tr.entityId,FQTypeCode.FY, tr.FY-1);
                
        }
        return "";
    }
    
      private String buildTermResultkey( String entityId,   FQTypeCode fq, int fy)
    {
        return String.format("%s %s %d",entityId, fq , fy);
    }
    
}
