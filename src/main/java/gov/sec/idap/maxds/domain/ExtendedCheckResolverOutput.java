/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SRIKANTH
 */
public class ExtendedCheckResolverOutput {
    
    private String companyName ;
    private String extendedElement;
    private String axisMemberName;
    
    public List<FactDimension> dimensions = null;
     public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

     public String getExtendedElement() {
        return extendedElement;
    }

    public void setExtendedElement(String extendedElement) {
        this.extendedElement = extendedElement;
    }
  public String getAxisMemberName() {
        return axisMemberName;
    }

    public void setAxisMemberName(String axisMemberName) {
        this.axisMemberName = axisMemberName;
    }

    
    public void PopulateDimensionalInfo() {

        if (axisMemberName == null) {
            return;
        }
        this.dimensions = new ArrayList<>();
        //FiniteLivedIntangibleAssetsByMajorClass=DevelopedTechnologyRights;
        //"BalanceSheetLocation=OtherCurrentLiabilities;FiniteLivedIntangibleAssetsByMajorClass=DevelopedTechnologyRights;ProductOrService=Besponsa;"
        //dim_segments  needs to be split into axis dim pairs...
        String[] segments = this.axisMemberName.split(";");

        if (segments == null) {
            return;
        }
        for (String segment : segments) {
            if (segment == null) {
                return;
            }

            String[] pair = segment.split("=");

            if (pair != null && pair.length == 2) {
                FactDimension fd = new FactDimension();
                fd.dimName = pair[0];
                fd.memberName = pair[1];

                this.dimensions.add(fd);
                
                
            }
        }
    }
}
