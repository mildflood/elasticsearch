/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import gov.sec.idap.maxds.calculation.Domain.FormulaResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.FormulaTermResults;

import gov.sec.idap.maxds.domain.TermResult;

import gov.sec.idap.maxds.domain.ValidationStatus;
import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;
import gov.sec.idap.maxds.elasticsearch.service.TermResultService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author SRIKANTH
 */
public class ValidationProcessor {
    HashMap<String, TermResultsDoc> termResultsMap;
        String  myEntityIds;
    TermRuleInfoProvider myTermRuleProvider;
    TermResultService myTermResultService;

     
    
    public ValidationProcessor(String e, TermRuleInfoProvider tr,
            TermResultService tresults ,
            HashMap<String, TermResultsDoc> results)
    {
        termResultsMap = results;
        this.myEntityIds = e;
        this.myTermRuleProvider = tr;
        this.myTermResultService = tresults;
        
    }
    
    public HashMap<String, TermResultsDoc>  validate()
    {
        if( myTermRuleProvider.validationExpressions.isEmpty())
            return termResultsMap;
        
        for( FormulaResolvedTermExpression formulaExp :myTermRuleProvider.validationExpressions  )
        {
            validateExpression(formulaExp);
        }
        return termResultsMap;
    }
    
    private void validateExpression( FormulaResolvedTermExpression formulaExp)
    {
        List<TermResultsDoc> termResults = 
                this.myTermRuleProvider.GetTermResultsByEntityListAndTermNames(
                        myEntityIds,
                        formulaExp, this.myTermResultService);
        
        if( termResults == null) termResults = new ArrayList<>();
        
        FormulaTermResults formulaData = new FormulaTermResults(termResults, 
        myTermRuleProvider.myTermRule.getPeriodType());
        
        for (Map.Entry<String, TermResultsDoc> entry : termResultsMap.entrySet()) 
        {
            String key = entry.getKey();
            TermResultsDoc value = entry.getValue();
           
            setValidationResult( key, value, formulaExp,formulaData );
                       
            
        }
        
        
    }
    
    private void setValidationResult(String key , TermResultsDoc origResult, 
            FormulaResolvedTermExpression exp, FormulaTermResults formulaData)
    {
         if( origResult.validationMessages == null)
        {
            origResult.validationMessages =  new ArrayList<>();
        }
        
        if( formulaData.resultsMap.containsKey(key))
        {
            FormulaTermResults.FormulaTermResult ftr = formulaData.resultsMap.get(key);
            
            TermResultsDoc valResult = formulaData.createResult( exp, ftr);
            
            if( valResult == null)
            {
                origResult.validationStatus = ValidationStatus.InValid;
                origResult.validationMessages.add(String.format( "Invalid:Missing required component(s): %s" , 
                        formulaData.getMissingRequiredComponentNames(exp, ftr)));
                return;
            }
            
            if( valResult.value ==  origResult.value)
            {
              
               origResult.validationStatus = ValidationStatus.Valid;
                origResult.validationMessages.add(String.format("Valid:%s %s = %s",
                        myTermRuleProvider.myTermRule.getName(),
                        TermResult.decimalFormat.format(origResult.value), 
                        valResult.resolvedExpression));
            }
            else
            {
                origResult.validationStatus = ValidationStatus.InValid;
                origResult.validationMessages.add( String.format("Invalid:%s <> %s %s",
                        TermResult.decimalFormat.format(origResult.value),
                         TermResult.decimalFormat.format(valResult.value),
                        valResult.resolvedExpression));
            }
                        
        }
        else
        {
            
            
            if( formulaData.hasRequiredComponemnts(exp))
            {
                origResult.validationStatus = ValidationStatus.InValid;
                origResult.validationMessages.add( String.format( "Invalid:Missing required component(s): %s" , 
                        formulaData.getMissingRequiredComponentNames(exp, null)));
            }
            else
            {
                origResult.validationStatus = ValidationStatus.ValidNoData;
                origResult.validationMessages.add( String.format( "Information:Requires at least one of the following optional component(s) to build validation: %s" , 
                        formulaData.getMissingOptionalComponentNames(exp)));
            }
        }
       
    }
    
}
