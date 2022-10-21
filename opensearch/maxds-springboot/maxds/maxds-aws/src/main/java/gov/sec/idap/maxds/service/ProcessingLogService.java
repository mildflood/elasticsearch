/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import gov.sec.idap.maxds.domain.ProcessingStatusCode;
import gov.sec.idap.maxds.domain.TermRule;
import gov.sec.idap.maxds.domain.USGAAPTaxonomyElement;
import gov.sec.idap.maxds.solr.document.LookupDoc;
import gov.sec.idap.maxds.solr.document.ProcessingLogDoc;
import gov.sec.idap.maxds.solr.repository.ProcessingLogRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

//@Service("processingLogService")
public class ProcessingLogService {

    @Autowired
    private ProcessingLogRepository repository;

    public List<ProcessingLogDoc> findByProcessingGroupId(String id) {
        return repository.findByProcessingGroupId(id);
    }

    public Iterable<ProcessingLogDoc> getLatestProcessingLogs(int limit) {
        //PageRequest request = new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "lastModified_dt"));
        return repository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastModified_dt")));
    }
    
    public void cancelPendingProcessingItems(String userId)
    {
        
         while(true){
            
            ProcessingLogDoc doc = getItemCurrentlyBeingProcessed();
            
            if( doc == null) break;
            
            doc.logStatus = ProcessingStatusCode.Canceled.toString();
            doc.currentTime = ProcessingLogDoc.GetUTCdatetimeAsString();
            doc.userName = userId;
            doc.lastModified = new Date();
            doc.description = "Canceled : " + doc.description;
            repository.save(doc);
            
        }
        
        while(true){
            
            ProcessingLogDoc doc = getNextTermToProcess();
            
            if( doc == null) break;
            
            doc.logStatus = ProcessingStatusCode.Canceled.toString();
            doc.currentTime = ProcessingLogDoc.GetUTCdatetimeAsString();
            doc.userName = userId;
            doc.lastModified = new Date();
            doc.description = "Canceled : " + doc.description;
            repository.save(doc);
            
        }
    }

    public void ScheduleTermsForProcessing(List<TermRule> trs) {

        int seq = 1;
        String processingGroupId = UUID.randomUUID().toString();;
        ArrayList<ProcessingLogDoc> itemsToSave = new ArrayList<>();
        for (TermRule tr : trs) {

            ProcessingLogDoc s = new ProcessingLogDoc();
            s.processingGroupId = String.format("%s%s", processingGroupId, tr.getTermId());
            s.id = s.processingGroupId;
            s.termId = tr.getTermId();
            s.userName = tr.getUserid();
            s.logStatus = ProcessingStatusCode.Scheduled.toString();
            s.description = String.format("Term %s is scheduled to be processed.", tr.getName());
            s.currentTime = ProcessingLogDoc.GetUTCdatetimeAsString();
            s.processingSequence = seq;
            itemsToSave.add(s);
            seq++;
        }

        repository.saveAll(itemsToSave);

    }
    
    public ProcessingLogDoc getNextTermToProcess(){
        
        //PageRequest request = new PageRequest(0, 1, new Sort(Sort.Direction.ASC, "processingSequence_i"));
        List<ProcessingLogDoc> items = repository.findByStatus( ProcessingStatusCode.Scheduled.toString(),
        		PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "processingSequence_i"))).getContent();
        
        
        if( items == null || items.size() == 0){
            
            return null;
        }
        
        return items.get(0);
    }
    
    public ProcessingLogDoc getItemCurrentlyBeingProcessed(){
        
        //PageRequest request = new PageRequest(0, 1);
        List<ProcessingLogDoc> items = repository.findByStatus( ProcessingStatusCode.InProgress.toString(),
        		PageRequest.of(0, 1)).getContent();
        
        
        if( items == null || items.isEmpty()){
            
            return null;
        }
        
        return items.get(0);
    }
    
    public Boolean IsCanceled( String processingId){
        
        ProcessingLogDoc doc = repository.findFirstById(processingId);
        
        if( doc != null ){
            
            return doc.logStatus.equals(ProcessingStatusCode.Canceled.toString());
        }
        
        return false;
    }

    public void SaveProgressStatus(String groupId,
            String entityId, String termId,
            String description, long processingTimeInSec, int noOfEntities, String userName) {
        String logStatus = ProcessingStatusCode.InProgress.toString();
        ProcessingLogDoc s = ProcessingLogDoc.BuildProcessingLog(groupId, entityId,
                termId, logStatus, description, processingTimeInSec, noOfEntities, userName);
        repository.save(s);
    }

    public void SaveErrorStatus(String groupId,
            String entityId, String termId,
            String description, String userName) {
        String logStatus = ProcessingStatusCode.Error.toString();
        ProcessingLogDoc s = ProcessingLogDoc.BuildProcessingLog(groupId, entityId, termId, logStatus, description, 0, 0, userName);
        repository.save(s);
    }

    public void SaveCompletedStatus(String groupId,
            String entityId, String termId,
            String description, long processingTimeInSec, int noOfEntities, String userName) {
        String logStatus = ProcessingStatusCode.Completed.toString();
        ProcessingLogDoc s = ProcessingLogDoc.BuildProcessingLog(groupId, entityId, termId, logStatus, description, processingTimeInSec,
                noOfEntities, userName);
        repository.save(s);
    }
    
    public int getScheduledCount(String scheduled) {
    	List<ProcessingLogDoc> items = repository.findScheduledLog(scheduled);
    	return items.size();
    }

}
