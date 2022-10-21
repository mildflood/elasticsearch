/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.document;

import gov.sec.idap.maxds.domain.TermMapInformation;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "MaxDS_TermMapInformation")
public class TermMapInformationDoc {

    @Id
    public String id;
    @Field("termId_s")
    public String termId;
    @Field("mapName_s")
    public String mapName;
    @Field("mapTermId_s")
    public String mapTermId;
    @Field("mapTermName_s")
    public String mapTermName;
    @Field("mapTermDescription_s")
    public String mapTermDescription;
    @Field("mapTermMappingInfo_s")
    public String mapTermMappingInfo;

    public TermMapInformationDoc() {

    }

    public TermMapInformationDoc(TermMapInformation mapInfo) {
        this.id = mapInfo.id;
        this.termId = mapInfo.termId;
        this.mapName = mapInfo.mapName;
        this.mapTermId = mapInfo.mapTermId;
        this.mapTermName = mapInfo.mapTermName;
        this.mapTermDescription = mapInfo.mapTermDescription;
        this.mapTermMappingInfo = mapInfo.mapTermMappingInfo;

    }

    public TermMapInformation getTermMapInformation() {
        TermMapInformation ret = new TermMapInformation();
        ret.id = this.id;
        ret.termId = this.termId;
        ret.mapName = this.mapName;
        ret.mapTermId = this.mapTermId;
        ret.mapTermName = this.mapTermName;
        ret.mapTermDescription = this.mapTermDescription;
        ret.mapTermMappingInfo = this.mapTermMappingInfo;

        return ret;
    }
}
