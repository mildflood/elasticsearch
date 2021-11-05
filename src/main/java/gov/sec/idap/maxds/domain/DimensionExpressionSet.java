/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.List;

/**
 *
 * @author srika
 */
public class DimensionExpressionSet {
    
    public ExpressionSetTypeCode axisType;
    public String axisName;
    public List<String> axisInclusionList;
    public List<String> axisExclusionList;
    
    public ExpressionSetTypeCode memberType;
    public String memberName;
    public List<String> memberInclusionList;
    public List<String> memberExclusionList;
    
    
}
