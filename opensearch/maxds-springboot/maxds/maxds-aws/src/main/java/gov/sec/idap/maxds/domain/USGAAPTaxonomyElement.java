/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import gov.sec.idap.maxds.elasticsearch.document.LookupDoc;

public class USGAAPTaxonomyElement {
    
    public USGAAPTaxonomyElement()
    {
        
        
        
    }
    
    public USGAAPTaxonomyElement( LookupDoc doc)
    {
        this.id = doc.getName();
        this.elementDefaultLabel = doc.getLabel();
        this.elementDefinitionUS = doc.getDescription();
        this.isTextBlock = doc.getIsTextBlock();
    }
   
    public String id;
    public String elementDefaultLabel;
    public String elementDefinitionUS;
    public BalanceType balType = BalanceType.na;
    public PeriodType periodType = PeriodType.na;
    public Boolean isTextBlock;
    
}
