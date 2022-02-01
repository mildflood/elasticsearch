/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation.Domain;

import gov.sec.idap.maxds.domain.FactDimension;
import gov.sec.idap.maxds.elasticsearch.document.NormalizedFact;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SRIKANTH
 */
public class ExtendedCheckWithDimensionResolvedTermExpression extends ExtendedCheckResolvedTermExpression {

    public ArrayList<String> NamedAxisList = new ArrayList<>();
    public ArrayList<String> NamedMemberList = new ArrayList<>();
    

    public String GetValidDimensionMatchingInfo(NormalizedFact dimensionFact, String elementName) {
        return GetDimensionMatchingInfo(dimensionFact.dimensions);
    }

    public String GetDimensionMatchingInfo(List<FactDimension> dimensions) {
        if (dimensions == null) {
            return null;
        }

        //we are supporting only a single dimension at this point..
        //so just checking for one...
        FactDimension fd = dimensions.get(0);

        return getMatchingDimensionInfo(fd.dimName, fd.memberName);
    }

    public String getMatchingDimensionInfo(String dimName, String memberName) {

        for (String axis : this.NamedAxisList) {
            if (dimName.equals(axis)) {
                for (String member : this.NamedMemberList) {
                    if (memberName.equals(member)) {
                        return String.format("%s@%s", memberName, dimName);
                    }
                }
            }
        }

        return null;
    }

}
