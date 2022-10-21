/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

public class DerviedZeroExpression {

    public String expression;
    public DerviedZeroTypeCode type = DerviedZeroTypeCode.na;
    public int rank = 0;
    public int score = 1;
    public Boolean allTermsRequired = false;
    public String division;
    public String sector;
    public String industry;
}
