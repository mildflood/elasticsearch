package gov.sec.idap.maxds.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.data.solr.repository.Query;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.AccuracyTestNotesDoc;

@Repository
public interface AccuracyTestNotesRepository extends ElasticsearchRepository<AccuracyTestNotesDoc, String>{
	 //@Query(value="entityId_s:?0 AND fy_i:?1 AND fq_s:?2")
	 @Query("{\"bool\": {\"must\":[{\"match\" : { \"entityId_s\" : \"?0\" }},{\"match\" : { \"fy_i\" : \"?1\" }},{\"match\" : { \"fq_s\" : \"?2\" }}]}}")
	 List<AccuracyTestNotesDoc> findByEntityIdAndFYAndFQ(String entityId, int fy, String fq);
}