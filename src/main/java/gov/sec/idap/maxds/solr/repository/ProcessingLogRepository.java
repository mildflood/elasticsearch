/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.repository;


import gov.sec.idap.maxds.solr.document.ProcessingLogDoc;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 *
 * @author srika
 */
public  interface ProcessingLogRepository extends SolrCrudRepository<ProcessingLogDoc, String> {
  
     public List<ProcessingLogDoc> findByProcessingGroupId(String id);
     
    @Query(value = "logStatus_s:?0")
    Page<ProcessingLogDoc> findByStatus(String status, Pageable pageable);
    
     
    public ProcessingLogDoc findFirstById(String id);
    
    
    @Query(value = "logStatus_s:?0")
    public List<ProcessingLogDoc> findScheduledLog(String scheduled);
    
    
    @Query(value = "logStatus_s:?InProgress")
    public List<ProcessingLogDoc> findInprogressLog();

}
