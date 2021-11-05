/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation.Domain;

import java.util.ArrayList;
import java.util.List;

import gov.sec.idap.maxds.domain.DimensionExpressionSet;
import gov.sec.idap.maxds.domain.ExpressionSetTypeCode;
import gov.sec.idap.maxds.domain.FactDimension;
import gov.sec.idap.maxds.domain.NormalizedFact;

public class ConceptMatchWithDimResolvedTermExpression extends ResolvedTermExpression {

    public String elementName;
    public ArrayList<String> NamedAxisList = new ArrayList<>();
    public ArrayList<String> NamedMemberList = new ArrayList<>();

    public Boolean canUseDimensionalFact(NormalizedFact fact) {
        if (this.expression.dimensionExpressionSets == null
                || this.expression.dimensionExpressionSets.isEmpty()) {
            return false;
        }

        if (fact.dimensions == null || fact.dimensions.isEmpty()) {
            return false;
        }

        for (DimensionExpressionSet set : this.expression.dimensionExpressionSets) {
            if (!checkExpressionSet(set, fact)) {
                return false;
            }
        }

        return true;
    }

    private Boolean checkExpressionSet(DimensionExpressionSet set, NormalizedFact fact) {

        if (set.axisType == ExpressionSetTypeCode.ExactMatch) {
            for (FactDimension fd : fact.dimensions) {

                if (isMatch(set, fd)) {
                    return true;
                }
            }
        } else {
            for (FactDimension fd : fact.dimensions) {
                if (isMatchForIncludeExclude(set, fd)) {
                    return true;
                }
            }
        }

        //check all the facts for include exclude rules...
        return false;
    }

    private Boolean isMatch(DimensionExpressionSet set, FactDimension fd) {

        if (!fd.dimName.equalsIgnoreCase(set.axisName)) {
            return false;
        }

        if (set.memberType == ExpressionSetTypeCode.Any) {
            return true;
        }

        if (set.memberType == ExpressionSetTypeCode.ExactMatch) {
            if (!fd.memberName.equalsIgnoreCase(set.memberName)) {
                return false;
            }
        } else {
            return checkInList(fd.memberName, set.memberInclusionList, set.memberExclusionList);
        }

        return true;
    }

    private Boolean isMatchForIncludeExclude(DimensionExpressionSet set, FactDimension fd) {

       
        //match on contains and does not contain...
        if (!checkInList(fd.dimName, set.axisInclusionList,
                set.axisExclusionList)) {
            return false;
        }

        if (set.memberType == ExpressionSetTypeCode.Any) {
            return true;
        }

        if (set.memberType == ExpressionSetTypeCode.ExactMatch) {
            if (!fd.memberName.equalsIgnoreCase(set.memberName)) {
                return false;
            }
        } else {
            return checkInList(fd.memberName, set.memberInclusionList,
                    set.memberExclusionList);
        }

        return true;
    }

    private Boolean checkInList(String name,
            List<String> contains, List<String> doesNotContain) {
        name = name.toLowerCase();

        if (doesNotContain != null) {
            for (String str : doesNotContain) {
                if (name.contains(str)) {
                    return false;
                }
            }
        }
        if (contains != null) {
            for (String str : contains) {
                if (!name.contains(str)) {
                    return false;
                }
            }
        }

        return true;
    }

}
