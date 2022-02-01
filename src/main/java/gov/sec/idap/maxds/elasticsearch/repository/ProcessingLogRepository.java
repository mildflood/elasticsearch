package gov.sec.idap.maxds.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.solr.repository.Query;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.ProcessingLogDoc;

@Repository
public interface ProcessingLogRepository extends ElasticsearchRepository<ProcessingLogDoc, String>{

	@Query("{\"bool\": {\"must\":[{\"match\" : { \"processingGroupId\" : \"?0\" }}]}}")
	public List<ProcessingLogDoc> findByProcessingGroupId(String id);

    //@Query(value = "logStatus:?0")
    @Query("{\"bool\": {\"must\":[{\"match\" : { \"logStatus\" : \"?0\" }}]}}")
    Page<ProcessingLogDoc> findByLogStatus(String status, Pageable pageable);
    
//    @Query("{\"bool\": {\"must\":[{\"match\" : { \"processingGroupId\" : \"?0\" }}]}}")
//    public ProcessingLogDoc findFirstById(String id);
    
//    //@Query(value = "logStatus:?0")
//    @Query("{\"bool\": {\"must\":[{\"match\" : { \"logStatus\" : \"?0\" }}]}}")
//    public List<ProcessingLogDoc> findScheduledLog(String scheduled);
//    
//    
//    //@Query(value = "logStatus:?InProgress")
//    @Query("{\"bool\": {\"must\":[{\"match\" : { \"logStatus\" : \"InProgress\" }}]}}")
//    public List<ProcessingLogDoc> findInprogressLog();
    
    @Query(" \"match_all\": { \"boost\" : 1.2 }")
    Page<ProcessingLogDoc> findAll(Pageable pageable);
}
