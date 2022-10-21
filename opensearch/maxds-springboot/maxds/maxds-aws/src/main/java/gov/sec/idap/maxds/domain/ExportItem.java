/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author SRIKANTH
 */
public class ExportItem implements Comparator<ExportItem> {
    
    private String company;
    private String cik;
    private String ticker;
    private String accession;
    private String reportingPeriod;
    private String periodEndDate;
    private List<Double> termResultValues;
    private List<String> validationStatuses;
    private List<String> validationMessages;
    private List<String> resolvedExpressions;
    
    @Override
    public int compare(ExportItem o1, ExportItem o2) {
        
       int ret = o1.company.compareTo(o2.company);
        if( ret != 0) return ret;
        return o1.reportingPeriod.compareTo(o2.reportingPeriod);
        
    }

    /**
     * @return the company
     */
    public String getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return the ticker
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * @param ticker the ticker to set
     */
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    /**
     * @return the reportingPeriod
     */
    public String getReportingPeriod() {
        return reportingPeriod;
    }

    /**
     * @param reportingPeriod the reportingPeriod to set
     */
    public void setReportingPeriod(String reportingPeriod) {
        this.reportingPeriod = reportingPeriod;
    }

    /**
     * @return the termResultValues
     */
    public List<Double> getTermResultValues() {
        return termResultValues;
    }

    /**
     * @param termResultValues the termResultValues to set
     */
    public void setTermResultValues(List<Double> termResultValues) {
        this.termResultValues = termResultValues;
    }

    /**
     * @return the validationStatuses
     */
    public List<String> getValidationStatuses() {
        return validationStatuses;
    }

    /**
     * @param validationStatuses the validationStatuses to set
     */
    public void setValidationStatuses(List<String> validationStatuses) {
        this.validationStatuses = validationStatuses;
    }

    /**
     * @return the validationMessages
     */
    public List<String> getValidationMessages() {
        return validationMessages;
    }

    /**
     * @param validationMessages the validationMessages to set
     */
    public void setValidationMessages(List<String> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * @return the periodEndDate
     */
    public String getPeriodEndDate() {
        return periodEndDate;
    }

    /**
     * @param periodEndDate the periodEndDate to set
     */
    public void setPeriodEndDate(String periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    /**
     * @return the cik
     */
    public String getCik() {
        return cik;
    }
   
    /**
     * @param cik the cik to set
     */
    public void setCik(String cik) {
        this.cik = cik;
    }

    /**
     * @return the resolvedExpressions
     */
    public List<String> getResolvedExpressions() {
        return resolvedExpressions;
    }

    /**
     * @param resolvedExpressions the resolvedExpressions to set
     */
    public void setResolvedExpressions(List<String> resolvedExpressions) {
        this.resolvedExpressions = resolvedExpressions;
    }

    /**
     * @return the accession
     */
    public String getAccession() {
        return accession;
    }

    /**
     * @param accession the accession to set
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }
}
