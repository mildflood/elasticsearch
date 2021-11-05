/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation.Domain;

import gov.sec.idap.maxds.domain.DemoteTypeCode;
import gov.sec.idap.maxds.domain.TermExpression;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author SRIKANTH
 */
public class FormulaResolvedTermExpression  extends ResolvedTermExpression {
    
    public String expressionToUse = null;
    public ArrayList<TermFormula> formulas = new ArrayList<>();
     
  
    
    public FormulaResolvedTermExpression( TermExpression te)
    {
        this.expression = te;
        initialize();
    }
    
    public class TermFormula
    {
        public String termName;
        public Boolean allowNull;
        public Boolean isBeginning;
        public Boolean isT4Q;
        
     
    }
    
    private void initialize()
    {
       expressionToUse = this.expression.expression.replace("sec:", "");
   
       String regExPattern  = "\\{\\s*(.+?)\\s*\\}";
       Pattern regex = Pattern.compile(regExPattern);
        
       Matcher m = regex.matcher(expressionToUse);
         
       while (m.find()) 
       {  
          String groupString = m.group();
          AddTermFormula(groupString);  
                     
       }  
       expressionToUse = expressionToUse.replace("=0", "");
    }
    
    
    
    
    private void AddTermFormula(String match)
    {
        TermFormula tf = new TermFormula();
        String tn = match.replace("{", "").replace("}", "");
        tf.allowNull = tn.contains("=0");
        tf.termName = tn.replace("=0", "");
        tf.isBeginning = tn.contains("[Beginning]");
        tf.isT4Q = tn.contains("[T4Q]");
        tf.termName = tf.termName.replace("[Beginning]", "");
        tf.termName = tf.termName.replace("[T4Q]", "");
        
        this.formulas.add(tf);
    }
    
    public ArrayList<String> GeTermNamesUsed()
    {
        ArrayList<String> ret= new ArrayList<>();
        if( this.formulas.size() == 0) return ret;
        
        for( TermFormula tf : this.formulas)
        {
            ret.add(tf.termName);
        }
       return ret;
    }
   
   
   
}
