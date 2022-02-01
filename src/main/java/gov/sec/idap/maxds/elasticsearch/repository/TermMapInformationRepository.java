package gov.sec.idap.maxds.elasticsearch.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.TermMapInformationDoc;

@Repository
public interface TermMapInformationRepository extends ElasticsearchRepository<TermMapInformationDoc, String> {
    public List<TermMapInformationDoc> findByMapName(String id); 
    public List<TermMapInformationDoc> findByMapNameAndTermIdIn(String id, Collection<String> termIds); 

    public void deleteByTermIdAndMapName(String termId, String mapName);
    public void deleteByTermId(String termId);
    
    public TermMapInformationDoc findOneByTermIdAndMapName(String termId, String mapName);
    public TermMapInformationDoc findOneByTermId(String termId);
    
    public void deleteByMapName(String mapName);
}
