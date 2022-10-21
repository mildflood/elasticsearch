/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.repository;


import gov.sec.idap.maxds.solr.document.TermMapInformationDoc;
import java.util.Collection;
import java.util.List;
import org.springframework.data.solr.repository.Query;

import org.springframework.data.solr.repository.SolrCrudRepository;


public interface TermMapInformationDocRepository extends SolrCrudRepository<TermMapInformationDoc, String> {
    
    
    public List<TermMapInformationDoc> findByMapName(String id); 
    
    
    public List<TermMapInformationDoc> findByMapNameAndTermIdIn(String id, Collection<String> termIds); 
    
   
    public void deleteByTermIdAndMapName(String termId, String mapName);
    public void deleteByTermId(String termId);
    
    public TermMapInformationDoc findOneByTermIdAndMapName(String termId, String mapName);
    public TermMapInformationDoc findOneByTermId(String termId);
    
    public void deleteByMapName(String mapName);
    
        
    
    
}