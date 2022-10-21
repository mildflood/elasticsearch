/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.List;

/**
 *
 * @author SRIKANTH
 */
public class TermRuleOverride {
    
    
    private String name;
    private Boolean mergeBaseExpressions = true;
    private List<EntityOverride> entityOverrides = null;
    private List<TermExpression> expressions = null;

    /**
     * @return the entityOverrides
     */
    public List<EntityOverride> getEntityOverrides() {
        return entityOverrides;
    }

    /**
     * @param entityOverrides the entityOverrides to set
     */
    public void setEntityOverrides(List<EntityOverride> entityOverrides) {
        this.entityOverrides = entityOverrides;
    }

    /**
     * @return the expressions
     */
    public List<TermExpression> getExpressions() {
        return expressions;
    }

    /**
     * @param expressions the expressions to set
     */
    public void setExpressions(List<TermExpression> expressions) {
        this.expressions = expressions;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the mergeBaseExpressions
     */
    public Boolean getMergeBaseExpressions() {
        return mergeBaseExpressions;
    }

    /**
     * @param mergeBaseExpressions the mergeBaseExpressions to set
     */
    public void setMergeBaseExpressions(Boolean mergeBaseExpressions) {
        this.mergeBaseExpressions = mergeBaseExpressions;
    }
}
