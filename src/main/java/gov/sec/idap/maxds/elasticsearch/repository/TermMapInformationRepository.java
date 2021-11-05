package gov.sec.idap.maxds.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.TermMapInformationDoc;

@Repository
public interface TermMapInformationRepository extends ElasticsearchRepository<TermMapInformationDoc, String> {

}
