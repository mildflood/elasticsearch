package gov.sec.idap.maxds.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;

@Repository
public interface TermResultsDocRepository extends ElasticsearchRepository<TermResultsDoc, String> {

}
