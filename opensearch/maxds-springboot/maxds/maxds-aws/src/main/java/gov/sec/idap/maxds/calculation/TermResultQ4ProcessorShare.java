/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;

import java.util.HashMap;

/**
 *
 * @author SRIKANTH
 */
public class TermResultQ4ProcessorShare extends TermResultQ4ProcessorSimple {
    
     public TermResultQ4ProcessorShare(HashMap<String, TermResultsDoc> origResults)
    {
        super(origResults);
    }
    
   @Override
   protected  TermResultsDoc createQ4( TermResultsDoc fyResult,
            TermResultsDoc q1Result, TermResultsDoc q2Result, TermResultsDoc q3Result)
    {
        if( q1Result != null && q2Result != null && q3Result != null)
        {
      
            TermResultsDoc q4Result = cloneTermResultsDoc(fyResult);
            
            //annula values are usually weighted averages when shares are 
            //defined as duration items....
            //so we want to multiply  by 4 to get to the right number of shares..
            q4Result.value *= 4;
            
            
            q4Result.value -= q1Result.value;
            q4Result.value -= q2Result.value;
            q4Result.value -= q3Result.value;
            q4Result.resolvedExpression = 
            String.format("4*(%f) -(%f)-(%f)-(%f)", fyResult.value, 
                    q1Result.value, q2Result.value, q2Result.value);
                return q4Result;
        }
        
        return null;
    } 
    
}
