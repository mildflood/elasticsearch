package gov.sec.idap.maxds.elasticsearch.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sec.idap.maxds.domain.GroupTermMapInformation;
import gov.sec.idap.maxds.domain.PostRequestResult;
import gov.sec.idap.maxds.domain.TermMapGroup;
import gov.sec.idap.maxds.domain.TermMapInformation;
import gov.sec.idap.maxds.domain.TermMapInformationDisplay;
import gov.sec.idap.maxds.elasticsearch.document.LookupDoc;
import gov.sec.idap.maxds.elasticsearch.document.TermMapInformationDoc;
import gov.sec.idap.maxds.elasticsearch.document.TermRuleDoc;
import gov.sec.idap.maxds.elasticsearch.helper.Indices;
import gov.sec.idap.maxds.elasticsearch.repository.LookupDocRepository;
import gov.sec.idap.maxds.elasticsearch.repository.TermMapInformationRepository;
import gov.sec.idap.maxds.elasticsearch.repository.TermRuleRepository;

@Service("termMapService")
public class TermMapService {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private static final String NEW_LINE_SEPARATOR = "\n";
    
    private ObjectMapper objectMapper;
    
    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ReferenceDataService lookupDataService;

    @Autowired
    private TermMapInformationRepository mapInformationRepository;

    @Autowired
    private TermRuleRepository termRuleRepository;
    
    @Autowired
    private TermRuleService termruleService;

    public PostRequestResult deleteGroup(String groupName) {
        if (groupName.equals("Compustat")) {
            PostRequestResult ret = new PostRequestResult();
            ret.status = false;
            ret.errorMessage = "Hard coded prevention of deleting compustat group. Will be removed before updating SEC code";
            return ret;
        }

        //need to remove the map information....
        BulkByScrollResponse response =
        		  new DeleteByQueryRequestBuilder((ElasticsearchClient) client, DeleteByQueryAction.INSTANCE)
        		    .filter(QueryBuilders.matchQuery("mapName_s", groupName)) 
        		    .source(Indices.TERM_MAP_INFO_INDEX)                                  
        		    .get();                                             
        		long deleted = response.getDeleted();  
        lookupDataService.deleteByNameAndType(groupName, LookupDoc.LookupType.termMapGroup.toString());

        PostRequestResult ret = new PostRequestResult();
        ret.status = true;
        return ret;
    }

    public PostRequestResult deleteGroupItem(GroupTermMapInformation info) {
        //only delete the groups passed in as some groups might be invisible...
        for (TermMapInformation mapInfo : info.mappedInfoSets) {
        	BulkByScrollResponse response =
          		  new DeleteByQueryRequestBuilder((ElasticsearchClient) client, DeleteByQueryAction.INSTANCE)
          		    .filter(QueryBuilders.matchQuery("termId_s", info.termId)) 
          		    .filter(QueryBuilders.matchQuery("mapName_s", mapInfo.mapName)) 
          		    .source(Indices.TERM_MAP_INFO_INDEX)                                  
          		    .get();                                             
          		long deleted = response.getDeleted();  
        }

        PostRequestResult ret = new PostRequestResult();
        ret.status = true;
        return ret;
    }

    public GroupTermMapInformation saveTermMapInformation(GroupTermMapInformation info) {
        if (info.termId == null || info.termId.isEmpty()) {
            int counter = 1;
            while (true) {
                //need to auto gen term Id...
                info.termId = "TBD" + String.format("%05d", counter);
                TermMapInformationDoc existingInfo = this.findOneByTermId(info.termId);
                if (existingInfo == null) {
                    break;
                }
                counter++;
            }

        } else {
        	BulkByScrollResponse response =
            		  new DeleteByQueryRequestBuilder((ElasticsearchClient) client, DeleteByQueryAction.INSTANCE)
            		    .filter(QueryBuilders.matchQuery("termId_s", info.termId)) 
            		    .source(Indices.TERM_MAP_INFO_INDEX)                                  
            		    .get();                                             
            		long deleted = response.getDeleted(); 
        }
        List<TermMapInformationDoc> saveList = new ArrayList<>();
        for (TermMapInformation mapInfo : info.mappedInfoSets) {
            mapInfo.termId = info.termId;
            mapInfo.id = mapInfo.mapName + "_" + mapInfo.mapTermId;
            if (mapInfo.mapTermId != null) {
                saveList.add(new TermMapInformationDoc(mapInfo));
            }

        }
        mapInformationRepository.saveAll(saveList);

        return info;
    }
    
    public List<GroupTermMapInformation> getAllTermMapInformations() {

        Iterable<TermRuleDoc> rules = termRuleRepository.findAll();

        HashMap<String, TermRuleDoc> validRules = new HashMap();
        for (TermRuleDoc tr : rules) {
            validRules.put(tr.getTermId(), tr);
        }

        HashMap<String, String> unknownTermRules = new HashMap();

        Iterable<TermMapInformationDoc> mapInfos = mapInformationRepository.findAll();
        HashMap<String, TermMapInformationDoc> mapsByTermIdAndGroup = new HashMap();
        for (TermMapInformationDoc ti : mapInfos) {
            String key = String.format("%s:%s", ti.getMapName(), ti.getMapTermId());
            mapsByTermIdAndGroup.put(key, ti);

            if (!validRules.containsKey(ti.getTermId())) {
                unknownTermRules.put(ti.getTermId(), ti.getTermId());
            }
        }
        List<TermMapGroup> groups = lookupDataService.getAllMappingGroups();

        HashMap<String, GroupTermMapInformation> mapsByTermId = new HashMap();

        for (TermRuleDoc tr : rules) {

            GroupTermMapInformation data = new GroupTermMapInformation();
            data.termId = tr.getTermId();
            data.name = tr.getName();
            data.description = tr.getDescription();
            data.isTermRule = true;
            data.mappedInfoSets = new ArrayList<>();
            for (TermMapGroup group : groups) {
                String key = String.format("%s:%s", group.mapName, data.termId);

                if (mapsByTermIdAndGroup.containsKey(key)) {
                    data.mappedInfoSets.add(mapsByTermIdAndGroup.get(key).getTermMapInformation());
                } else {
                    TermMapInformation empty = new TermMapInformation();
                    empty.mapName = group.mapName;
                    empty.termId = tr.getTermId();
                    data.mappedInfoSets.add(empty);
                }
            }

            mapsByTermId.put(data.termId, data);
        }

        //now to add the ones that are not real term rules...
        for (String termId : unknownTermRules.keySet()) {

            GroupTermMapInformation data = new GroupTermMapInformation();
            data.termId = termId;
            data.isTermRule = false;
            data.mappedInfoSets = new ArrayList<>();
            for (TermMapGroup group : groups) {
                String key = String.format("%s:%s", group.mapName, data.termId);

                if (mapsByTermIdAndGroup.containsKey(key)) {
                    data.mappedInfoSets.add(mapsByTermIdAndGroup.get(key).getTermMapInformation());
                } else {
                    TermMapInformation empty = new TermMapInformation();
                    empty.mapName = group.mapName;
                    empty.termId = termId;
                    data.mappedInfoSets.add(empty);
                }
            }

            mapsByTermId.put(data.termId, data);

        }

        List<GroupTermMapInformation> ret = new ArrayList<>(mapsByTermId.values());

        Collections.sort(ret, new GroupTermMapInformation());
        return ret;

    }

    public HashMap<String, TermMapInformationDoc> findCompustatIds(Collection<String> termIds) {
        HashMap<String, TermMapInformationDoc> ret = new HashMap<>();
        List<TermMapInformationDoc> results = this.findByMapNameAndTermIdIn("Compustat", termIds);

        results.forEach((doc) -> {
            ret.put(doc.getTermId(), doc);
        });

        return ret;
    }

    public List<TermMapInformationDisplay> getTermMapInformation() {

        Iterable<TermMapInformationDoc> mapInfos = mapInformationRepository.findAll();

        HashMap<String, TermMapInformationDisplay> mapsByTermId = new HashMap();

        Iterable<TermRuleDoc> rules = termRuleRepository.findAll();
        HashMap<String, TermRuleDoc> rulebyId = new HashMap();
        for (TermRuleDoc tr : rules) {
            rulebyId.put(tr.getTermId(), tr);
        }

        for (TermMapInformationDoc info : mapInfos) {

            if (info.getTermId() == null || info.getTermId().length() == 0) {
                continue;
            }

            TermMapInformationDisplay current = null;
            if (!mapsByTermId.containsKey(info.getTermId())) {
                current = new TermMapInformationDisplay();
                current.termId = info.getTermId();
                current.mappedInfoSets = new ArrayList();
                if (rulebyId.containsKey(info.getTermId())) {
                    current.name = rulebyId.get(info.getTermId()).getName();
                    current.description = rulebyId.get(info.getTermId()).getDescription();
                }
                mapsByTermId.put(info.getTermId(), current);
            }

            current.mappedInfoSets.add(info.getTermMapInformation());
        }

        for (TermRuleDoc tr : rules) {

            if (!mapsByTermId.containsKey(tr.getTermId())) {
                //we do not have any mappings for these terms...
                TermMapInformationDisplay current = new TermMapInformationDisplay();
                current.termId = tr.getTermId();
                current.name = tr.getName();
                current.description = tr.getDescription();
                current.mappedInfoSets = new ArrayList();
                mapsByTermId.put(tr.getTermId(), current);
            }
        }

        List<TermMapInformationDisplay> ret = new ArrayList<>(mapsByTermId.values());

        Collections.sort(ret, new TermMapInformationDisplay());
        return ret;

    }

    public void exportTermMapInformation(ServletOutputStream stream) {
        try {

            List<String> header = new ArrayList();
            header.add("MAXDS Term ID");
            header.add("MAXDS Term Name");
            header.add("MAXDS Description");
            List<TermMapGroup> groups = lookupDataService.getAllMappingGroups();
            for (TermMapGroup group : groups) {
                header.add(group.mapName + " Term ID");
                header.add(group.mapName + " Term Name");
                header.add(group.mapName + " Description");
                header.add(group.mapName + " Mapping");
            }

            OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
            CSVPrinter csvFilePrinter = new CSVPrinter(streamWriter, csvFileFormat);
            //LOG.debug("Header: " + header);
            csvFilePrinter.printRecord(header);

            List<TermMapInformationDisplay> results = getTermMapInformation();

            for (TermMapInformationDisplay map : results) {
                List vals = new ArrayList();
                vals.add(map.termId);
                vals.add(map.name);
                vals.add(map.description);

                HashMap<String, String> dataPoints = new HashMap();
                for (TermMapInformation info : map.mappedInfoSets) {
                    dataPoints.put(info.mapName + " Term ID", info.mapTermId);
                    dataPoints.put(info.mapName + " Term Name", info.mapTermName);
                    dataPoints.put(info.mapName + " Description", info.mapTermDescription);
                    dataPoints.put(info.mapName + " Mapping", info.mapTermMappingInfo);

                }

                for (TermMapGroup group : groups) {

                    vals.add(getDataPoint(group.mapName + " Term ID", dataPoints));
                    vals.add(getDataPoint(group.mapName + " Term Name", dataPoints));
                    vals.add(getDataPoint(group.mapName + " Description", dataPoints));
                    vals.add(getDataPoint(group.mapName + " Mapping", dataPoints));
                }

                csvFilePrinter.printRecord(vals);
            }

            streamWriter.flush();
            streamWriter.close();
            csvFilePrinter.close();
        } catch (IOException e) {
            LOG.error("Exception raised in exportTermMapInformation. Exp Message: "
                    + e.getMessage());
        }

    }
    
    private TermMapInformationDoc findOneByTermId(String termId) {
    	TermMapInformationDoc doc = null;
		try {
			// Build Query
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.must(new MatchQueryBuilder("termId_s", termId));
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(boolQueryBuilder);
			// Generate the actual request to send to ES.
			SearchRequest searchRequest = new SearchRequest();
			searchRequest.source(searchSourceBuilder);
			SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
			List<TermMapInformationDoc> serverList = getSearchResult(response);
			if(serverList != null) {
				doc = serverList.get(0);
			}
		} catch (Exception e) {
			LOG.error("Elasticsearch failed: " + e.getMessage());
		}
		return doc;
    }
    
    private List<TermMapInformationDoc> findByMapNameAndTermIdIn(String mapName, Collection<String> termIds) {
    	List<TermMapInformationDoc> results = new ArrayList<>();
    	try {
    		for(String termId: termIds) {
    			List<TermMapInformationDoc> tempResults = new ArrayList<>();
				// Build Query
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
				boolQueryBuilder.must(new MatchQueryBuilder("mapName_s", mapName));
				boolQueryBuilder.must(new MatchQueryBuilder("termId_s", termId));
				SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
				searchSourceBuilder.query(boolQueryBuilder);
				// Generate the actual request to send to ES.
				SearchRequest searchRequest = new SearchRequest();
				searchRequest.source(searchSourceBuilder);
				SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
				tempResults = getSearchResult(response);
				results.addAll(tempResults);
    		}
    	} catch (Exception e) {
			LOG.error("Elasticsearch failed: " + e.getMessage());
		}
    	return results;
    }

    private String getDataPoint(String key, HashMap<String, String> dataPoints) {
        return dataPoints.containsKey(key) ? dataPoints.get(key) : "";
    }
    
    private List<TermMapInformationDoc> getSearchResult(SearchResponse response) {

		SearchHit[] searchHit = response.getHits().getHits();

		List<TermMapInformationDoc> docs = new ArrayList<>();

		for (SearchHit hit : searchHit) {
			docs.add(objectMapper.convertValue(hit.getSourceAsMap(), TermMapInformationDoc.class));
		}

		return docs;
	}

}