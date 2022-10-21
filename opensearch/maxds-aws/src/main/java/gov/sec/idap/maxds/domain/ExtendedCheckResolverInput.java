/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

/**
 *
 * @author SRIKANTH
 */
public class ExtendedCheckResolverInput {
    
    private String division ;
    private String sector ;
    private String sic ;
    private String filerCategory;
    private TermExpression expression;
    
    
     public TermExpression getExpression() {
        return expression;
    }

    public void setExpression(TermExpression expression) {
        this.expression = expression;
    }
    
    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

   

    public String getSic() {
        return sic;
    }

    public void setSic(String sic) {
        this.sic = sic;
    }

    @Override
    public String toString() {
        return "division:" + this.division + ",sector:" + this.sector +",industry:" + this.sic + ",expression:" + this.expression;
    }

    /**
     * @return the filerCategory
     */
    public String getFilerCategory() {
        return filerCategory;
    }

    /**
     * @param filerCategory the filerCategory to set
     */
    public void setFilerCategory(String filerCategory) {
        this.filerCategory = filerCategory;
    }
}
