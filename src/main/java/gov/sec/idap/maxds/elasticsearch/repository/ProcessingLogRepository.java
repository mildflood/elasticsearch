package gov.sec.idap.maxds.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.ProcessingLogDoc;

@Repository
public interface ProcessingLogRepository extends ElasticsearchRepository<ProcessingLogDoc, String>{

}
