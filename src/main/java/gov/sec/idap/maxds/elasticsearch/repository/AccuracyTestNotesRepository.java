package gov.sec.idap.maxds.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.AccuracyTestNotesDoc;

@Repository
public interface AccuracyTestNotesRepository extends ElasticsearchRepository<AccuracyTestNotesDoc, String>{

}
