/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "MaxDS_LookupReferences")
public class LookupDoc {

    public enum LookupType
    {
            termRulePriorityGroup,
            termRuleCategory,
            taxonomyElement,
            termMapGroup,
            none
    }    
    
    @Id  
    private String id;
    @Field("name_s")
    private String name;
    @Field
    private String label_s;
    @Field
    private String description_s;
    @Field("type_s")
    private LookupType type = LookupType.none;
    @Field("isTextBlock_b")
    private Boolean isTextBlock;
     
    /**
     * @return the label
     */
    public String getLabel() {
        return label_s;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label_s = label;
    }
    
    

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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
     * @return the description
     */
    public String getDescription() {
        return description_s;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description_s = description;
    }

    /**
     * @return the type
     */
    public LookupType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(LookupType type) {
        this.type = type;
    }

    /**
     * @return the isTextBlock
     */
    public Boolean getIsTextBlock() {
        return isTextBlock;
    }

    /**
     * @param isTextBlock the isTextBlock to set
     */
    public void setIsTextBlock(Boolean isTextBlock) {
        this.isTextBlock = isTextBlock;
    }
}
