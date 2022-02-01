/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.repository;


import gov.sec.idap.maxds.solr.document.TermRuleDoc;

import java.util.Collection;
import java.util.List;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 *
 * @author srira
 */
public interface TermRuleDocRepository extends SolrCrudRepository<TermRuleDoc, String> {


    @Query("*:*")
    @Override
    public Iterable<TermRuleDoc> findAll();
    
    @Query(value="termId:?0")
    public List<TermRuleDoc> findByTermId(String searchTerm);
    
    @Query(value="name:\"?0\"")
    public List<TermRuleDoc> findByTermName(String searchTerm);
    
    @Query(value="*:*", fields = "termId, name, description, financialStatement, priorityGroup,hasValidations")
    public List<TermRuleDoc> findLimited();
   
     @Query(value="*:*", fields="termId, name, description")
    public List<TermRuleDoc> findNameInfos();
    
    public void deleteByTermId(String termId);
    
    @Query(value="includeInAccuracyTests_b:true")
    public List<TermRuleDoc> findByIncludeInAccuracyTests();
    
    @Query(value="termId:?0")
    public List<TermRuleDoc> findByTermIds(Collection<String> termId);
    
}
