/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

public enum FQTypeCode
{
    Q1,
    Q2,
    Q3,
    Q4,
    FY,
    L4,
    na;
    
    public static FQTypeCode getFYTypeFromCode(String fyTypeCode) {
       
        switch (fyTypeCode) {
            case "Q1":
                return FQTypeCode.Q1;
            case "Q2":
                return FQTypeCode.Q2;
            case "Q3":
                return FQTypeCode.Q3;
            case "Q4":
                return FQTypeCode.Q4;
            case "L4":
                return FQTypeCode.L4;
            default:
                return FQTypeCode.FY;
        }                
    }
}
