/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.repository;

import gov.sec.idap.maxds.solr.document.AccuracyTestNotesDoc;
import java.util.List;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 *
 * @author srika
 */
public interface AccuracyTestNotesRepository extends SolrCrudRepository<AccuracyTestNotesDoc, String> {

     @Query(value="entityId_s:?0 AND fy_i:?1 AND fq_s:?2")
    List<AccuracyTestNotesDoc> findByEntityIdAndFYAndFQ(String entityId, int fy, String fq);
}
