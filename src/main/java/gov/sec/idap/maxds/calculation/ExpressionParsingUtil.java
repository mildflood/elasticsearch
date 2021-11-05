/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import gov.sec.idap.maxds.calculation.Domain.ConceptMatchMultipleResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ConceptMatchResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ConceptMatchWithDimResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ExtendedCheckResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ExtendedCheckWithDimensionResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.FormulaResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ResolvedTermExpression;
import gov.sec.idap.maxds.domain.DimensionExpressionSet;
import gov.sec.idap.maxds.domain.TermExpression;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SRIKANTH
 */
public class ExpressionParsingUtil {
    
    
    public static ResolvedTermExpression BuildResolvedTerm(TermExpression te)
    {
        switch( te.type)
        {
            case ConceptMatch:
                return createConceptMatchExpression(te);
               
            case ConceptMatchMultiple:
                return createConceptMatchMultipleExpression(te);
                
            case ConceptMatchWithDim:
                return createConceptMatchWithDimExpression(te);
                
                
            case ExtendedCheck:
                return createExtendedCheckExpression(te);
                
           case ExtendedCheckWithDim:
                return createExtendedCheckWithDimExpression(te);
                
         
            case Formula:
                return createFormulaResolvedTermExpression(te);
        }
        
        return null;
    }
    
    private static FormulaResolvedTermExpression createFormulaResolvedTermExpression(TermExpression te)
    {
        FormulaResolvedTermExpression ret = new FormulaResolvedTermExpression(te);
   
       
        
        
        
        return ret;
    }
    
    public static String[] getItemsFromExpressionStringSimple(String expression) {

        String exp = expression.replace("{sec:", "");
        exp = exp.replace("{us-gaap_", "");
        exp = exp.replace("{", "");
        exp = exp.replace("}", "");
        return exp.split("[&\\|]+");
    }
    
    public static ExtendedCheckResolvedTermExpression createExtendedCheckExpression(TermExpression te)
    {
        ExtendedCheckResolvedTermExpression ret = new ExtendedCheckResolvedTermExpression();
        setExtendedExpression( ret, te);
               
        
        return ret;
    }
    
     public static ExtendedCheckWithDimensionResolvedTermExpression createExtendedCheckWithDimExpression(TermExpression te)
    {
        ExtendedCheckWithDimensionResolvedTermExpression ret = new ExtendedCheckWithDimensionResolvedTermExpression();
        setExtendedExpression( ret, te);
               
        ret.NamedAxisList = ParseItemList(te.axisExpression);
        ret.NamedMemberList = ParseItemList(te.memberExpression);
        return ret;
    }
    
    private static void setExtendedExpression(ExtendedCheckResolvedTermExpression resolvedExp ,TermExpression te )
    {
        resolvedExp.expression = te;
        resolvedExp.balanceType = "none";
        if (te.expression.contains("isDebit") )
        {
            resolvedExp.balanceType = "D";
        }
        else if (te.expression.contains("isCredit") )
        {
            resolvedExp.balanceType = "C";
        }
        else if (te.expression.contains("isNA") )
        {
            resolvedExp.balanceType = "na";
        }
        if (te.expression.contains("isInstant"))
        {
            resolvedExp.periodType = "I";
        }
        else
        {
            resolvedExp.periodType = "D";
        }

        resolvedExp.isShareItemType = te.expression.contains("isSharesItemType");
        resolvedExp.containsWords = GetParsedNames(te.expression,"nameContains(", ")" );
        resolvedExp.doesNotContainsWords = GetParsedNames(te.expression,"nameNotContains(", ")" );
       
        
    }
    
    private static ArrayList<String> GetParsedNames( String parseExp, String matchWithPrefix, String matchWithSuffix )
    {
        ArrayList<String> ret = new ArrayList<>();
        
        int index = 0;
        int prefixLength = matchWithPrefix.length();
        while(true)
        {
            int pos = parseExp.indexOf(matchWithPrefix, index);
            if( pos < index ) break;
            index = pos + prefixLength;
            int end = parseExp.indexOf(matchWithSuffix, index );
            if(  end < index ) break;
            String name = parseExp.substring(index, end);
            ret.add(name);
            
        }
        
        return ret;
    }
    
    private static ConceptMatchResolvedTermExpression createConceptMatchExpression(TermExpression te)
    {
        ConceptMatchResolvedTermExpression ret = new ConceptMatchResolvedTermExpression();
        ret.expression = te;
        ret.elementName = parseConceptMatch(te.expression);
        return ret;
    }
    
   private static ConceptMatchMultipleResolvedTermExpression createConceptMatchMultipleExpression(TermExpression te)
    {
        ConceptMatchMultipleResolvedTermExpression ret = new ConceptMatchMultipleResolvedTermExpression();
        ret.expression = te;
       
        return ret;
    }
    
    
    private static ConceptMatchWithDimResolvedTermExpression createConceptMatchWithDimExpression
            (TermExpression te)
    {
        ConceptMatchWithDimResolvedTermExpression ret = new ConceptMatchWithDimResolvedTermExpression();
        ret.expression = te;
        ret.elementName = parseConceptMatch(te.expression);
        
        //make all items lower case to make things easy for comparison
        if( ret.expression.dimensionExpressionSets != null )
        {
            for( DimensionExpressionSet set :ret.expression.dimensionExpressionSets  )
            {
                set.axisExclusionList= convertListOfStringToLower(set.axisExclusionList);
                set.axisInclusionList= convertListOfStringToLower(set.axisInclusionList);
                set.memberInclusionList= convertListOfStringToLower(set.memberInclusionList);
                set.memberExclusionList= convertListOfStringToLower(set.memberExclusionList);
            }
        }
        
       
        return ret;
    }
            
            
    private static List<String> convertListOfStringToLower(List<String> input)
    {
        if( input == null) return null;
        
        List<String> ret = new ArrayList<>();
        
        input.forEach((str) -> {
                   ret.add(str.toLowerCase());
                });
        
        return ret;
        
    }
    
            
    
    private static String parseConceptMatch(String expression)
    {
        return removeCurlyBraces(expression);
    }
    
    private static String removeCurlyBraces(String expression)
    {
        return expression.replace("{", "").replace("}", "");
    }
    
    private static ArrayList<String> ParseItemList(String expression)
    {
      ArrayList<String> ret = new ArrayList<>();
      if(expression == null || expression.length() == 0 ) return ret;
       
       if( expression.contains("||"))
       {
           String[] items = expression.split("\\|\\|");
           for( String exp : items)
           {
               ret.add(removeCurlyBraces(exp));
           }
  
       }
       else
       {
           ret.add(removeCurlyBraces(expression));
       }
       
       return ret;
    }
    
}
