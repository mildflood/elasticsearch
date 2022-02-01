package gov.sec.idap.maxds.elasticsearch.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sec.idap.maxds.domain.ProcessingStatusCode;
import gov.sec.idap.maxds.domain.TermRule;
import gov.sec.idap.maxds.elasticsearch.document.ProcessingLogDoc;
import gov.sec.idap.maxds.elasticsearch.repository.ProcessingLogRepository;

@Service("processingLogService")
public class ProcessingLogService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private ProcessingLogRepository repository;
	private RestHighLevelClient client;
	private ObjectMapper objectMapper;

	@Autowired
	public ProcessingLogService(ProcessingLogRepository repository, RestHighLevelClient client,
			ObjectMapper objectMapper) {
		this.repository = repository;
		this.client = client;
		this.objectMapper = objectMapper;
	}

	public List<ProcessingLogDoc> findByProcessingGroupId(String id) {
		return repository.findByProcessingGroupId(id);
//		List<ProcessingLogDoc> docs = new ArrayList<ProcessingLogDoc>();
//		try {
//			// Build Query
//			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//			boolQueryBuilder.must(new MatchQueryBuilder("processingGroupId", id));
//			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//			searchSourceBuilder.query(boolQueryBuilder);
//			// Generate the actual request to send to ES.
//			SearchRequest searchRequest = new SearchRequest();
//			searchRequest.source(searchSourceBuilder);
//			SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
//			docs = getSearchResult(response);
//		} catch (Exception e) {
//			log.error("Elasticsearch failed: " + e.getMessage());
//		}
//		return docs;
	}

	private List<ProcessingLogDoc> getSearchResult(SearchResponse response) {
		SearchHit[] searchHit = response.getHits().getHits();
		List<ProcessingLogDoc> processingLogs = new ArrayList<>();
		for (SearchHit hit : searchHit) {
			processingLogs.add(objectMapper.convertValue(hit.getSourceAsMap(), ProcessingLogDoc.class));
		}
		return processingLogs;
	}

	public Iterable<ProcessingLogDoc> getLatestProcessingLogs(int limit) {
		//PageRequest request = new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "lastModified"));
		Iterable<ProcessingLogDoc> docs = repository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastModified")));
		return docs;
	}

	public void cancelPendingProcessingItems(String userId) {
		while (true) {
			ProcessingLogDoc doc = getItemCurrentlyBeingProcessed();
			if (doc == null)
				break;
			doc.setLogStatus(ProcessingStatusCode.Canceled.toString());
			doc.setCurrentTime(ProcessingLogDoc.GetUTCdatetimeAsString());
			doc.setUserName(userId);
			doc.setLastModified(new Date());
			doc.setDescription("Canceled : " + doc.getDescription());
			repository.save(doc);
		}
		while (true) {
			ProcessingLogDoc doc = getNextTermToProcess();
			if (doc == null)
				break;
			doc.setLogStatus(ProcessingStatusCode.Canceled.toString());
			doc.setCurrentTime(ProcessingLogDoc.GetUTCdatetimeAsString());
			doc.setUserName(userId);
			doc.setLastModified(new Date());
			doc.setDescription("Canceled : " + doc.getDescription());
			repository.save(doc);
		}
	}

	public void ScheduleTermsForProcessing(List<TermRule> trs) {

		int seq = 1;
		String processingGroupId = UUID.randomUUID().toString();
		;
		ArrayList<ProcessingLogDoc> itemsToSave = new ArrayList<>();
		for (TermRule tr : trs) {
			ProcessingLogDoc s = new ProcessingLogDoc();
			s.setProcessingGroupId(String.format("%s%s", processingGroupId, tr.getTermId()));
			s.setId(s.getProcessingGroupId());
			s.setTermId(tr.getTermId());
			s.setUserName(tr.getUserid());
			s.setLogStatus(ProcessingStatusCode.Scheduled.toString());
			s.setDescription(String.format("Term %s is scheduled to be processed.", tr.getName()));
			s.setCurrentTime(ProcessingLogDoc.GetUTCdatetimeAsString());
			s.setProcessingSequence(seq);
			itemsToSave.add(s);
			seq++;
		}
		repository.saveAll(itemsToSave);
	}

	public ProcessingLogDoc getNextTermToProcess() {

		// PageRequest request = new PageRequest(0, 1, new Sort(Sort.Direction.ASC,
		// "processingSequence"));

		//List<ProcessingLogDoc> docs = new ArrayList<ProcessingLogDoc>();
		List<ProcessingLogDoc> items = repository.findByLogStatus(ProcessingStatusCode.Scheduled.toString(), PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "processingSequence")))
				.getContent();
		if (items == null || items.size() == 0) {

			return null;
		}
		return items.get(0);
		
//		try {
//			// Build Query
//			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//			boolQueryBuilder.must(new MatchQueryBuilder("logStatus", ProcessingStatusCode.Scheduled.toString()));
//			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//			searchSourceBuilder.query(boolQueryBuilder);
//			searchSourceBuilder.sort("processingSequence", SortOrder.ASC);
//			// Generate the actual request to send to ES.
//			SearchRequest searchRequest = new SearchRequest();
//			searchRequest.source(searchSourceBuilder);
//
//			SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
//			docs = getSearchResult(response);
//		} catch (Exception e) {
//			log.error("Elasticsearch failed: " + e.getMessage());
//		}
//
//		if (docs == null || docs.size() == 0) {
//
//			return null;
//		}
//		return docs.get(0);
	}

	public ProcessingLogDoc getItemCurrentlyBeingProcessed() {

		//PageRequest request = new PageRequest(0, 1);
		List<ProcessingLogDoc> items = repository.findByLogStatus(ProcessingStatusCode.InProgress.toString(), PageRequest.of(0, 1))
				.getContent();
		return items.get(0);

//		List<ProcessingLogDoc> docs = new ArrayList<ProcessingLogDoc>();
//		try {
//			// Build Query
//			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//			boolQueryBuilder.must(new MatchQueryBuilder("logStatus", ProcessingStatusCode.InProgress.toString()));
//			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//			searchSourceBuilder.query(boolQueryBuilder);
//			// Generate the actual request to send to ES.
//			SearchRequest searchRequest = new SearchRequest();
//			searchRequest.source(searchSourceBuilder);
//
//			SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
//			docs = getSearchResult(response);
//		} catch (Exception e) {
//			log.error("Elasticsearch failed: " + e.getMessage());
//		}
//		if (docs == null || docs.isEmpty()) {
//			return null;
//		}
//		return docs.get(0);
	}

	public Boolean IsCanceled(String processingId) {

		// ProcessingLogDoc doc = repository.findFirstById(processingId);
		List<ProcessingLogDoc> docs = findByProcessingGroupId(processingId);
		ProcessingLogDoc doc = null;
		if (docs != null && docs.size() != 0) {
			doc = docs.get(0);
		}
		if (doc != null) {
			return doc.getLogStatus().equals(ProcessingStatusCode.Canceled.toString());
		}
		return false;
	}

	public void SaveProgressStatus(String groupId, String entityId, String termId, String description,
			long processingTimeInSec, int noOfEntities, String userName) {
		String logStatus = ProcessingStatusCode.InProgress.toString();
		ProcessingLogDoc s = ProcessingLogDoc.BuildProcessingLog(groupId, entityId, termId, logStatus, description,
				processingTimeInSec, noOfEntities, userName);
		repository.save(s);
	}

	public void SaveErrorStatus(String groupId, String entityId, String termId, String description, String userName) {
		String logStatus = ProcessingStatusCode.Error.toString();
		ProcessingLogDoc s = ProcessingLogDoc.BuildProcessingLog(groupId, entityId, termId, logStatus, description, 0,
				0, userName);
		repository.save(s);
	}

	public void SaveCompletedStatus(String groupId, String entityId, String termId, String description,
			long processingTimeInSec, int noOfEntities, String userName) {
		String logStatus = ProcessingStatusCode.Completed.toString();
		ProcessingLogDoc s = ProcessingLogDoc.BuildProcessingLog(groupId, entityId, termId, logStatus, description,
				processingTimeInSec, noOfEntities, userName);
		repository.save(s);
	}

	public int getScheduledCount(String scheduled) {
		//List<ProcessingLogDoc> items = repository.findScheduledLog(scheduled);
		List<ProcessingLogDoc> docs = new ArrayList<ProcessingLogDoc>();
		try {
			// Build Query
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.must(new MatchQueryBuilder("logStatus", scheduled));
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(boolQueryBuilder);
			// Generate the actual request to send to ES.
			SearchRequest searchRequest = new SearchRequest();
			searchRequest.source(searchSourceBuilder);

			SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
			docs = getSearchResult(response);
		} catch (Exception e) {
			log.error("Elasticsearch failed: " + e.getMessage());
		}
		if (docs != null) {
			return docs.size();
		} 
		return 0;
	}

}
