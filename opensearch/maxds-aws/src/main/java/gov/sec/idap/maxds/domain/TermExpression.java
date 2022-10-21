/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class TermExpression
{
    public String expression ;  
    public String axisExpression ;
    public String memberExpression ;
  
    
    public TermExpressionTypeCode type = TermExpressionTypeCode.na;
    
    public String conceptName;
    public List<String> NamedAxisList;
    public List<String> NamedMemberList;
    
    public List<String> conceptMatchMultipleList;
    public List<String> virtualFactMemberExclusionList ;
    public Boolean useVirtualParentNew = true;
    
    
    public String virtualFactAxis;
   
    
    public Boolean useMaxAxisCount = false;
    public int maxAxisCount = 0;
    public List<DimensionExpressionSet> dimensionExpressionSets;
    
    
    
    public String balType;
    public String perType;
    public Boolean isShareItemType;
    public List<String> containsWords;
    public List<String> doesNotContainsWords;
    
    public Boolean usePositiveValuesOnly = false;
    public Boolean useNegativeValuesOnly = false;
    public Boolean reverseNegativeValues = false;
    
    
    private List<ExpressionFormula> formulaList;
    
    
 
    public int rank = 0;

    /**
     * @return the formulaList
     */
    public List<ExpressionFormula> getFormulaList() {
        return formulaList;
    }

    /**
     * @param formulaList the formulaList to set
     */
    public void setFormulaList(List<ExpressionFormula> formulaList) {
        this.formulaList = formulaList;
    }
  
}