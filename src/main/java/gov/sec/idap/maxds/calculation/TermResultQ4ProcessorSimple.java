/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import gov.sec.idap.maxds.domain.FQTypeCode;
import gov.sec.idap.maxds.domain.PeriodType;
import gov.sec.idap.maxds.domain.TermResult;
import gov.sec.idap.maxds.solr.document.TermResultsDoc;

import java.util.HashMap;

/**
 *
 * @author SRIKANTH
 */
public class  TermResultQ4ProcessorSimple extends TermResultQ4ProcessorBase {
    
    
   
    
    public TermResultQ4ProcessorSimple(HashMap<String, TermResultsDoc> origResults)
    {
        super(origResults);
    }
    
    
  

    protected void BuildQ4Value(TermResultsDoc value, String fYKey, String q4Key) {
        TermResultsDoc q4Val = null;
        if( value.periodType == PeriodType.instant)
        {
            //need to create a copy of the term result and call it Q4
            addInstanceQ4Value(value,q4Key );
            return;
        }
        
        
        if( value.periodType == PeriodType.duration)
        {
            //need to find Q3 and then take a difference between q3 and q4
            String q3Key = fYKey.replace("FY", "Q3");
            String q2Key = fYKey.replace("FY", "Q2");
            String q1Key = fYKey.replace("FY", "Q1");
            
            TermResultsDoc q3Result = null;
            TermResultsDoc q2Result = null;
            TermResultsDoc q1Result = null;
            
            
            if( termResultsMap.containsKey(q3Key))
            {
                q3Result = termResultsMap.get(q3Key);
                if( termResultsMap.containsKey(q2Key))
                {
                    q2Result = termResultsMap.get(q2Key);
                    if( termResultsMap.containsKey(q1Key))
                    {
                        q1Result = termResultsMap.get(q1Key);
                    }
                }
                
            }
            q4Val = createQ4(value, q1Result,q2Result, q3Result );
                
        
            if( q4Val != null )
            {
                q4Val.FQ = FQTypeCode.Q4;
                q4TermResultsMap.put(q4Key,q4Val);
            }
        }
    }
    
    
    
    protected TermResultsDoc createQ4( TermResultsDoc fyResult,
            TermResultsDoc q1Result, TermResultsDoc q2Result, TermResultsDoc q3Result)
    {
        if( q1Result != null && q2Result != null && q3Result != null)
        {
      
            TermResultsDoc q4Result = cloneTermResultsDoc(fyResult);
            q4Result.value -= q1Result.value;
            q4Result.value -= q2Result.value;
            q4Result.value -= q3Result.value;
                
            q4Result.resolvedExpression = 
            String.format("FY %s - Q1 (%s)- Q2 (%s)- Q3 (%s)",
                    TermResult.decimalFormat.format(fyResult.value) , 
                    TermResult.decimalFormat.format(q1Result.value) ,
                    TermResult.decimalFormat.format(q2Result.value) ,
                    TermResult.decimalFormat.format(q3Result.value) );
                return q4Result;
        }
        
        return null;
    }

}
