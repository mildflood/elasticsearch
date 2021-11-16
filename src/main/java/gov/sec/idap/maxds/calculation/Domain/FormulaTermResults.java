/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation.Domain;

import static gov.sec.idap.maxds.domain.TermResult.decimalFormat;

import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import gov.sec.idap.maxds.domain.PeriodType;
import gov.sec.idap.maxds.domain.TermResult;
import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;

/**
 *
 * @author SRIKANTH
 */
public class FormulaTermResults {
    
   
    PeriodType termType;
    public HashMap<String, FormulaTermResult> resultsMap = new HashMap<>();
    public class FormulaTermResult
    {
        public HashMap<String, TermResultsDoc> resultByName = new HashMap<>();
    }
    
    public FormulaTermResults( List<TermResultsDoc> termResults, PeriodType type)
    {
        this.termType = type;
        initialize(termResults);
    }
    
    protected void initialize(List<TermResultsDoc> termResults)
    {
        for(TermResultsDoc tr : termResults )
        {
            String key = String.format("%s %s %d",tr.getEntityId(), tr.FQ, tr.FY);
            FormulaTermResult cur = this.resultsMap.get(key);
            if( cur == null)
            {
                cur = new FormulaTermResult();
                this.resultsMap.put(key, cur);
            }
            
            cur.resultByName.put(tr.termName, tr);
        }
    }
    
    //just the value is populated...and nul means no term 
    public TermResultsDoc createResult(FormulaResolvedTermExpression exp, FormulaTermResult ftr )
    {
       String resolvedExpression = exp.expressionToUse;
       String resolvedExpressionDisplay = exp.expressionToUse;
        TermResultsDoc baseTr = null;
        for(FormulaResolvedTermExpression.TermFormula tf : exp.formulas )
        {
            if(ftr.resultByName.containsKey(tf.termName))
            {
                baseTr = ftr.resultByName.get(tf.termName);
                
                resolvedExpression = resolvedExpression.replace("{" + tf.termName + "}",
                        String.format("%f",  baseTr.value));
                resolvedExpressionDisplay = resolvedExpressionDisplay.replace("{" + tf.termName + "}", 
                        String.format("%s %s", tf.termName, TermResult.decimalFormat.format(baseTr.value)));
            }
            else
            {
                if( ! tf.allowNull)
                   return null; //missing required term...
                
                resolvedExpression = resolvedExpression.replace("{" + tf.termName + "}", "0");
                resolvedExpressionDisplay = resolvedExpressionDisplay.replace("{" + tf.termName + "}", 
                        String.format("%s (NULL)", tf.termName));
            }
           
            
            
        }
        if( baseTr != null)
        {
          
            //need to evaluate expression
            TermResultsDoc ret = new TermResultsDoc();
           
            //ret.value = eval/..
            try
            {
                ret.value = evaluate(resolvedExpression);
            }
            catch(Exception exception)
            {
                return null;
            }
            ret.FQ = baseTr.FQ;
            ret.FY = baseTr.FY;
            ret.cik = baseTr.cik;
            ret.accession = baseTr.accession;
            ret.filingDate = baseTr.filingDate;
            ret.setEntityId(baseTr.getEntityId());
            ret.periodType = termType;
            ret.resolvedExpression = String.format("%s = %s",resolvedExpressionDisplay ,
                    decimalFormat.format(ret.value));
            return ret;
        }
        
        return null;
    }
    
    public String getMissingRequiredComponentNames(FormulaResolvedTermExpression exp, 
            FormulaTermResult ftr )
    {
        StringBuilder str = new StringBuilder();
    
        for(FormulaResolvedTermExpression.TermFormula tf : exp.formulas )
        {
            if( tf.allowNull) continue;
            
            if(ftr != null && ftr.resultByName.containsKey(tf.termName)) 
                continue;
            
            str.append(String.format("{%s} ", tf.termName));
            
        }
        
        
        return str.toString();
    }
    
    public String getMissingOptionalComponentNames(FormulaResolvedTermExpression exp )
    {
        StringBuilder str = new StringBuilder();
    
        for(FormulaResolvedTermExpression.TermFormula tf : exp.formulas )
        {
                       
            str.append(String.format("{%s} ", tf.termName));
            
        }
        
        
        return str.toString();
    }
    
    
    public Boolean hasRequiredComponemnts(FormulaResolvedTermExpression exp)
    {
        for(FormulaResolvedTermExpression.TermFormula tf : exp.formulas )
        {
            if( !tf.allowNull) return true;
        }
        
        return false;
    }
   
    private static ScriptEngine engine = null;
    
    private double evaluate(String resolvedExpression) throws ScriptException
    {
        if( engine == null)
        {
            ScriptEngineManager mgr = new ScriptEngineManager();
            engine = mgr.getEngineByName("JavaScript");
        }
    
        return (double)engine.eval(resolvedExpression);
    }
    
    public TermResultsDoc getExpressionItemValue( String periodKey, String expressionItem)
    {
        if( this.resultsMap.containsKey(periodKey))
        {
            FormulaTermResult periodValues = resultsMap.get(periodKey);
            if( periodValues.resultByName.containsKey(expressionItem))
            {
                return periodValues.resultByName.get(expressionItem);
            }
        }
        return null;
    }
}
