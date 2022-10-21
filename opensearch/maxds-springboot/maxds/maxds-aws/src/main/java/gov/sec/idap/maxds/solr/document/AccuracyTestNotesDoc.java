/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.document;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "MaxDS_AccuracyTestNotes")
public class AccuracyTestNotesDoc {

    @Id
    public String id;
    @Field("entityId_s")
    public String entityId;
    @Field("fy_i")
    public int FY = 0;
    @Field("fq_s")
    public String FQ = "FY";
    @Field("termId_s")
    public String termId;
    @Field("isCheckedCS_b")
    public Boolean isCheckedCS = false;
     @Field("isCheckedMaxDS_b")
    public Boolean isCheckedMaxDS = false;

    @Field("notes_s")
    public String notes;

}
