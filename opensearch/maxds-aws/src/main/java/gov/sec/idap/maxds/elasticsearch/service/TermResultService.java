package gov.sec.idap.maxds.elasticsearch.service;

import static gov.sec.idap.maxds.calculation.DerivedZeroTermExpressionProcessor.buildkey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sec.idap.maxds.api.vo.TermResultsDocVO;
import gov.sec.idap.maxds.calculation.Domain.FYFQInfo;

import gov.sec.idap.maxds.domain.FQTypeCode;
import gov.sec.idap.maxds.domain.SECFilings;
import gov.sec.idap.maxds.domain.TermResultsDerivedNonZero;
import gov.sec.idap.maxds.domain.TermResultsDerivedZero;
import gov.sec.idap.maxds.domain.TermRule;
import gov.sec.idap.maxds.domain.TermRuleStatistics;
import gov.sec.idap.maxds.domain.TextBlockInfo;
import gov.sec.idap.maxds.domain.ValidationStatus;
import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.document.FilingDoc;
import gov.sec.idap.maxds.elasticsearch.document.NormalizedFact;
import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;
import gov.sec.idap.maxds.elasticsearch.helper.Indices;
import gov.sec.idap.maxds.elasticsearch.repository.TermResultsDocRepository;

@Service("termResultService")
public class TermResultService {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private RestHighLevelClient client;

    @Autowired
    private EntityService entityService;

//    @Autowired
//    private SecApiService idapAPIService;

    @Autowired
    private TermRuleService termRuleService;
    
    @Autowired
    private FilingService filingService;
    
    @Autowired
    private NormalizedFactService normalizedFactService; 

    @Autowired
    private TermResultsDocRepository resultsSolrRepo;
    
    @Autowired
	private ObjectMapper objectMapper;

    @Value("${maxds.min.fy.to.process:2016}")
    int minFYToProcess;
    @Value("${maxds.max.fy.to.process:2020}")
    int maxFYToProcess;

    @Value("${maxds.skip.fq:false}")
    Boolean skipFQProcessing;

    public Boolean canSkipQ4Processing() {

        return this.skipFQProcessing;
    }

    private String getFiscalConfigurationFactQueryLimits() {

        return skipFQProcessing ? String.format("AND fact_fy_i:[%d TO *] AND fact_fp: \"FY\"", minFYToProcess)
                : String.format("AND fact_fy_i:[%d TO *]", minFYToProcess);
    }

    private String getFiscalConfigurationFilingQueryLimits() {

        return skipFQProcessing ? String.format("AND fy_l:[%d TO *] AND fp:FY", minFYToProcess)
                : String.format("AND fy_l:[%d TO *]", minFYToProcess);
    }

    public List<TermResultsDoc> findByTermId(String termId) {
        return resultsSolrRepo.findByTermIdAndMyDocType(termId, TermResultsDocVO.TermResultsDocType.primaryTermResult.toString());
    }

    public List<TermResultsDoc> findBaseInfoByTermIdAndEntity(String termId, String entityId) {

        if (entityId == null || entityId.isEmpty()) {
            return resultsSolrRepo.findByTermIdAndMyDocType(termId, TermResultsDocVO.TermResultsDocType.primaryTermResult.toString());
        }
        List<TermResultsDoc> dataResults1 = resultsSolrRepo.findByEntityIdAndTermIdAndMyDocType(entityId, termId,
                TermResultsDocVO.TermResultsDocType.primaryTermResult.toString());

        List<TermResultsDoc> dataResults = new ArrayList<>(dataResults1);

        //TODO need to check what happens here... 
        List<TermResultsDoc> derivedZeros
                = resultsSolrRepo.findByEntityIdAndTermIdAndMyDocType(entityId, termId,
                        TermResultsDocVO.TermResultsDocType.derivedZeroTermResult.toString());

        if (derivedZeros.size() > 0) {
            dataResults.addAll(derivedZeros);

        }

        return dataResults;
    }

    public List<TermResultsDoc> findByEntity(String entityId) {
        List<TermResultsDoc> ret = resultsSolrRepo.findByEntityIdAndMyDocType(entityId,
                TermResultsDocVO.TermResultsDocType.primaryTermResult.toString());

        return new ArrayList<>(ret);
    }

    public void deleteByTermRule(String termId) {
        resultsSolrRepo.deleteByTermId(termId);
    }

    public void deleteByTermName(String termName) {
        resultsSolrRepo.deleteByTermName(termName);
    }

    public void deleteByTermIdAndEntityId(String termId, String entityId) {
    	BulkByScrollResponse response = null;
    	DeleteByQueryRequest request = new DeleteByQueryRequest(Indices.TERM_RESULT_INDEX);
    	   	
    	BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    	boolQueryBuilder.must(new MatchQueryBuilder("entityId", entityId));
    	request.setQuery(boolQueryBuilder);
		boolQueryBuilder.must(new MatchQueryBuilder("termId", termId));
		request.setQuery(boolQueryBuilder);
		try {
	        response = client.deleteByQuery(request, RequestOptions.DEFAULT);
	    } catch (Exception e) {
	        LOG.error("Exception in delete: " + e.getMessage());
	    }
		if (response != null) {
			LOG.debug("Number of record deleted from " + Indices.TERM_RESULT_INDEX +" : " + response.getDeleted());
		} else {
			LOG.debug("Nothing deleted from " + Indices.TERM_RESULT_INDEX);
		}   	
        //resultsSolrRepo.deleteByTermIdAndEntityId(termId, entityId);
    }

    public void saveTermResults(List<TermResultsDoc> termResults) {
        resultsSolrRepo.saveAll(termResults);
    }

    public List<Entity> getEntitiesForTermRuleAndRank(String termId, int rankId, Boolean justRank,
            Boolean mapped,
            int minYearToCheck, int maxYearToCheck, Boolean includeQuarterly,
            String division, String sector, String sic, String filerCategory, String entityInputId) {

        HashMap<String, Entity> entityMap = new HashMap<String, Entity>();

        if (entityInputId != null && !entityInputId.isEmpty() && !entityInputId.equals("NULL")) {
            entityMap.put(entityInputId, entityService.getEntityById(entityInputId));
        } else if (sector != null && !sector.isEmpty()) {
            entityMap = entityService.findEntitiesByDivisionSectorAndSicCode(
                    division, sector, sic, filerCategory);

        }
        if (entityMap.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> entities = rankId == 99 ? new ArrayList<>() : getDistinctEntitiesByType(
                TermResultsDocVO.TermResultsDocType.primaryTermResult.toString(),
                termId,
                rankId,
                minYearToCheck, maxYearToCheck, includeQuarterly,
                entityMap.keySet());

        List<String> derivedZeroEntities = null;
        if (rankId == 0 || rankId == 99) {

            derivedZeroEntities = getDistinctEntitiesByType(
                    TermResultsDocVO.TermResultsDocType.derivedZeroTermResult.toString(),
                    termId,
                    0,
                    minYearToCheck, maxYearToCheck, includeQuarterly,
                    entityMap.keySet());
        }
        HashMap<String, Boolean> entitiesWithValidationErrors = null;
        if (rankId != 99 && !entities.isEmpty()) {

            //we need the list of entities with vakidation errors..
            TermRule tr = termRuleService.getTermRuleByTermId(termId);
            if (tr != null
                    && tr.getValidationExpressions() != null
                    && !tr.getValidationExpressions().isEmpty()) {

                LOG.info("Getting entities with validation errors");
                entitiesWithValidationErrors
                        = getDistinctEntitiesWithValidationErrorsByType(
                                TermResultsDocVO.TermResultsDocType.primaryTermResult.toString(),
                                termId,
                                rankId,
                                minYearToCheck, maxYearToCheck, includeQuarterly,
                                entities);

            }
        }

        HashMap<String, Boolean> mappedEntities = new HashMap<>();
        entities.forEach((en) -> {
            mappedEntities.put(en, true);
        });

        if (derivedZeroEntities != null) {
            derivedZeroEntities.forEach((en) -> {
                mappedEntities.put(en, true);
            });
        }

        List<Entity> ret = new ArrayList<>();
        for (Entity obj : entityMap.values()) {
            String entityId = obj.getEntityId();

            if (mappedEntities.containsKey(entityId) == mapped) {
                LOG.info("Setting validation Error status before checking");

                if (entitiesWithValidationErrors != null
                        && entitiesWithValidationErrors.containsKey(entityId)) {

                    LOG.info("Setting validation Error status");
                    obj.validationStatus = ValidationStatus.InValid;
                }
                ret.add(obj);

            }

        }

        return ret;
    }

    public TermRuleStatistics getTermRuleStatisticsByRank(
            String termId,
            int minYearToCheck, int maxYearToCheck, Boolean includeQuarterly,
            String division, String sector, String sic, String filerCategory,
            String entityId) {
        TermRuleStatistics ret = new TermRuleStatistics();

        HashMap<String, String> entityMap = new HashMap<String, String>();
        if (entityId != null && !entityId.isEmpty() && !entityId.equals("NULL")) {
            entityMap.put(entityId, entityId);
        } else if (division != null && !division.isEmpty()) {
            entityMap = entityService.findByDivisionSectorAndSicCode(division, sector, sic, filerCategory);

            LOG.info("Total number of entities found for criteria : " + entityMap.size());

            if (entityMap.isEmpty()) {
                return ret;
            }

        }

        HashMap<Integer, TermRuleStatistics.TermRuleStatisticsByRank> result
                = buildStatisticsByRank(termId, minYearToCheck, maxYearToCheck, includeQuarterly,
                        entityMap);

        for (Integer rank : result.keySet()) {
            TermRuleStatistics.TermRuleStatisticsByRank termRuleStatisticsByRank
                    = result.get(rank);
            ret.resultsByRank.add(termRuleStatisticsByRank);

            ret.mappedEntityCount += termRuleStatisticsByRank.mappedEntityCount;
        }

        ret.totalEntityCount = entityMap == null
                ? (int) entityService.count() : entityMap.size();
        ret.unMappedEntityCount = ret.totalEntityCount - ret.mappedEntityCount;

        double val = ret.totalEntityCount > 0
                ? ((double) ret.mappedEntityCount) / ret.totalEntityCount : 0;
        ret.coverage = String.format("%.3f", val);
        return ret;

    }

    private List<String> getDistinctEntitiesByType(String type,
            String termId, int rank,
            int minYearToCheck, int maxYearToCheck, Boolean includeQuarterly,
            Collection<String> entityMap) {
        SearchPage<TermResultsDoc> searchPage;
        SearchHits<TermResultsDoc> searchHits = null;
        List<String> coveredEntities = new ArrayList<>();
        		
        LOG.info("getDistinctEntitiesByType Started: " + termId + type);
        LOG.info("Entity Count : " + entityMap.size());
        if (includeQuarterly) {
        	//Commented to display negative ranks records as well on coverage details
            /*if (rank > 0 && rank != 99) {*/
        	if (rank > 0 && rank != 99) {
                searchPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFYRangeRank(termId, entityMap,
                        minYearToCheck, maxYearToCheck, type, rank, PageRequest.of(0, 20000));
            } else {
            	searchPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFYRange(termId, entityMap,
                        minYearToCheck, maxYearToCheck, type, PageRequest.of(0, 20000));
            }

        } else {
        	//Commented to display negative ranks records as well on coverage details
            /*if (rank > 0 && rank != 99) {*/
        	if (rank > 0 && rank != 99) {
        		searchPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFYRangeFQRank(termId, entityMap,
                        minYearToCheck, maxYearToCheck, "FY", type, rank, PageRequest.of(0, 20000));
            } else {
            	searchPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFYRangeFQRank(termId, entityMap,
                        minYearToCheck, maxYearToCheck, "FY", type, rank, PageRequest.of(0, 20000));
            }
        }

        LOG.info("getDistinctEntitiesByType completed starting to calcualte facet data: " + termId +" " + type);
        
        if (searchPage != null) {
        	searchHits = searchPage.getSearchHits();
        	List<SearchHit<TermResultsDoc>> resultList = searchHits.getSearchHits();
        	for (SearchHit<TermResultsDoc> re: resultList) {
        		TermResultsDoc termResultDoc = re.getContent();
        		coveredEntities.add(termResultDoc.getEntityId());
        	}
        }    
        List<String> coveredEntitiesDistinct = coveredEntities.stream().distinct().collect(Collectors.toList());
//        facetPage.getAllFacets().stream().forEach(page -> page.forEach(facet -> {
//            coveredEntities.add(facet.getValue());
//        }));
        LOG.info("getDistinctEntitiesByType completed : " + termId + " " + type);
        return coveredEntitiesDistinct; //coveredEntities;

    }

    private HashMap<String, Boolean> getDistinctEntitiesWithValidationErrorsByType(String type,
            String termId, int rank,
            int minYearToCheck, int maxYearToCheck, Boolean includeQuarterly,
            Collection<String> entityMap) {
        SearchPage<TermResultsDoc> searchPage;
        SearchHits<TermResultsDoc> searchHits = null;
        
        HashMap<String, Boolean> coveredEntities = new HashMap<>();

        LOG.info("getDistinctEntitiesByType Started: " + termId + type);
        LOG.info("Entity Count : " + entityMap.size());
        if (includeQuarterly) {

            if (rank > 0 && rank != 99) {
            	searchPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFYRangeRankWithValError(termId, entityMap,
                        minYearToCheck, maxYearToCheck, type, rank, PageRequest.of(0, 20000));
            } else {
            	searchPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFYRangeWithValError(termId, entityMap,
                        minYearToCheck, maxYearToCheck, type, PageRequest.of(0, 20000));
            }

        } else {

            if (rank > 0 && rank != 99) {
            	searchPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFYRangeFQRankWithValError(termId, entityMap,
                        minYearToCheck, maxYearToCheck, "FY", type, rank, PageRequest.of(0, 20000));
            } else {
            	searchPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFYRangeFQWithValError(termId, entityMap,
                        minYearToCheck, maxYearToCheck, "FY", type, PageRequest.of(0, 20000));
            }

        }

        LOG.info("getDistinctEntitiesByType completed starting to calcualte facet data: " + termId + " " + type);
        if (searchPage != null) {
        	searchHits = searchPage.getSearchHits();
        	List<SearchHit<TermResultsDoc>> resultList = searchHits.getSearchHits();
        	for (SearchHit<TermResultsDoc> re: resultList) {
        		TermResultsDoc termResultDoc = re.getContent();
        		if (coveredEntities.keySet() != null && !coveredEntities.keySet().contains(termResultDoc.getEntityId())) {
        			coveredEntities.put(termResultDoc.getEntityId(), true);
        		}
        	}
        }    
//        facetPage.getAllFacets().stream().forEach(page -> page.forEach(facet -> {
//            coveredEntities.put(facet.getValue(), true);
//        }));
        LOG.info("getDistinctEntitiesByType completed : " + termId + " " + type);
        return coveredEntities;

    }

    public HashMap<Integer, TermRuleStatistics.TermRuleStatisticsByRank> buildStatisticsByRank(
            String termId,
            int minYearToCheck, int maxYearToCheck, Boolean includeQuarterly,
            HashMap<String, String> entityMap) {
    	
    	ArrayList<String> entityIds = new ArrayList<>(entityMap.keySet());
    	SearchHits<TermResultsDoc> searchHits = null;

        HashMap<Integer, TermRuleStatistics.TermRuleStatisticsByRank> statisticsByRank = new HashMap<>();

        SearchPage<TermResultsDoc> searchPage = null;

        if (includeQuarterly) {
        	searchPage = resultsSolrRepo.findEntityRankPivotFacetByTermIdEntitiesFYRange(termId,
                    entityIds, minYearToCheck, maxYearToCheck,
                    TermResultsDocVO.TermResultsDocType.primaryTermResult.toString(),
                    PageRequest.of(0, 20000));

        } else {
        	searchPage = resultsSolrRepo.findEntityRankPivotFacetByTermIdEntitiesFYRangeFQ(termId,
                    entityMap.keySet(), minYearToCheck, maxYearToCheck,
                    TermResultsDocVO.TermResultsDocType.primaryTermResult.toString(),
                    PageRequest.of(0, 20000));
        }
        
        HashMap<Integer, List<String>> entriesByRank = new HashMap<>();
        if (searchPage != null) {
        	searchHits = searchPage.getSearchHits();
        	List<SearchHit<TermResultsDoc>> resultList = searchHits.getSearchHits();
        	for (SearchHit<TermResultsDoc> re: resultList) {
        		TermResultsDoc termResultDoc = re.getContent();
        		if (termResultDoc != null) {
        			int rank = termResultDoc.getRank();
        			String entityId = termResultDoc.getEntityId();
        			if (entriesByRank.get(rank) == null) {	 // new rank entry       			
	            		List<String> entityIdList = new ArrayList<String>();
	            		entityIdList.add(entityId);
	            		entriesByRank.put(rank, entityIdList);
        			} else {   // existing rank entry
        				entriesByRank.get(rank).add(entityId);
        			}
        		}
        	}
        }
        		  

//        List<FacetPivotFieldEntry> entries
//                = facetPage.getPivot("rank,entityId");
        
        //HashMap<Integer, FacetPivotFieldEntry> entriesByRank = new HashMap<>();
//        for (FacetPivotFieldEntry rE : entries) {
//            int rank = Integer.parseInt(rE.getValue());
//            entriesByRank.put(rank, rE);
//        }

        HashMap<String, Boolean> distinctEntities = new HashMap<>();

        for (Map.Entry<Integer, List<String>> entry : entriesByRank.entrySet()) {
            int rank = entry.getKey();
            TermRuleStatistics.TermRuleStatisticsByRank stat = new TermRuleStatistics.TermRuleStatisticsByRank();

            stat.mappedEntityCount = 0;
            stat.rankId = rank;

            for (String ee : (List<String>) entry.getValue()) {

                String entity = ee;
                stat.nonDistinctMappedEntityCount++;

                if (!distinctEntities.containsKey(entity)) {
                    stat.mappedEntityCount++;
                    distinctEntities.put(entity, Boolean.FALSE);
                }
            }

            statisticsByRank.put(rank, stat);
        }

        searchPage = resultsSolrRepo.findEntityRankPivotFacetByTermIdEntitiesFYRange(termId,
                entityIds, minYearToCheck, maxYearToCheck,
                TermResultsDocVO.TermResultsDocType.derivedZeroTermResult.toString(),
                null);

        if (searchPage != null) {
        	searchHits = searchPage.getSearchHits();
        	List<SearchHit<TermResultsDoc>> resultList = searchHits.getSearchHits();
        	for (SearchHit<TermResultsDoc> re: resultList) {
        		TermResultsDoc termResultDoc = re.getContent();
        		if (termResultDoc != null) {
        			int rank = termResultDoc.getRank();
        			String entityId = termResultDoc.getEntityId();
        			if (entriesByRank.get(rank) == null) {	 // new rank entry       			
	            		List<String> entityIdList = new ArrayList<String>();
	            		entityIdList.add(entityId);
	            		entriesByRank.put(rank, entityIdList);
        			} else {   // existing rank entry
        				entriesByRank.get(rank).add(entityId);
        			}
        		}
        	}
        }

        for (Map.Entry<Integer, List<String>> entry : entriesByRank.entrySet()) {
            int rank = 99;
            TermRuleStatistics.TermRuleStatisticsByRank stat = new TermRuleStatistics.TermRuleStatisticsByRank();

            stat.mappedEntityCount = 0;
            stat.rankId = 99;
            for (String ee : (List<String>)entry.getValue()) {

                String entity = ee;
                stat.nonDistinctMappedEntityCount++;

                if (!distinctEntities.containsKey(entity)) {
                    stat.mappedEntityCount++;
                    distinctEntities.put(entity, Boolean.FALSE);
                }
            }

            statisticsByRank.put(rank, stat);
        }

        return statisticsByRank;
    }

    public HashMap<Integer, List<String>> buildEntityCoverageByYear(
            String termId, int minYearToCheck, int maxYearToCheck,
            Boolean includeQuarterly,
            List<String> entitities, Boolean useMissingCollection, Boolean isForAllEntities) {

        HashMap<Integer, List<String>> retValue = new HashMap<>();
        SearchHits<TermResultsDoc> searchHits = null;

        String docType = useMissingCollection ? TermResultsDocVO.TermResultsDocType.derivedZeroTermResult.toString()
                : TermResultsDocVO.TermResultsDocType.primaryTermResult.toString();

        //FacetPage<TermResultsDoc> facetPage = null;
        SearchPage<TermResultsDoc> searchPage = null;

        if (includeQuarterly) {

            if (isForAllEntities) {
            	searchPage = resultsSolrRepo.findAllEntityYearPivotFacetByTermIdEntitiesFYRange(termId,
                        minYearToCheck, maxYearToCheck,
                        docType,
                        null);
            } else {
                searchPage = resultsSolrRepo.findEntityYearPivotFacetByTermIdEntitiesFYRange(termId,
                        entitities, minYearToCheck, maxYearToCheck,
                        docType,
                null);
            }

        } else {
            if (isForAllEntities) {
                searchPage = resultsSolrRepo.findAllEntityYearPivotFacetByTermIdEntitiesFYRangeFQ(termId,
                        minYearToCheck, maxYearToCheck,
                        docType,
                        null);
            } else {
                searchPage = resultsSolrRepo.findEntityYearPivotFacetByTermIdEntitiesFYRangeFQ(termId,
                        entitities, minYearToCheck, maxYearToCheck,
                        docType,
                        null);
            }
        }

//        List<FacetPivotFieldEntry> entries
//                = facetPage.getPivot("FY,entityId");
        
        HashMap<Integer, List<String>> entriesByYear = new HashMap<>();
        
        if (searchPage != null) {
        	searchHits = searchPage.getSearchHits();
        	List<SearchHit<TermResultsDoc>> resultList = searchHits.getSearchHits();
        	for (SearchHit<TermResultsDoc> re: resultList) {
        		TermResultsDoc termResultDoc = re.getContent();
        		if (termResultDoc != null) {
        			int fy = termResultDoc.getFY();
        			String entityId = termResultDoc.getEntityId();
        			if (entriesByYear.get(fy) == null) {	 // new rank entry       			
	            		List<String> entityIdList = new ArrayList<String>();
	            		entityIdList.add(entityId);
	            		entriesByYear.put(fy, entityIdList);
        			} else {   // existing rank entry
        				 if(!entriesByYear.get(fy).contains(entityId)) { //existing year entry doesn't have this entityId
        					 entriesByYear.get(fy).add(entityId);
        				 }
        			}
        		}
        	}
        }

        //for (FacetPivotFieldEntry rE : entries) {
        for (Map.Entry<Integer, List<String>> entry : entriesByYear.entrySet()) {
            int year = entry.getKey();

            List<String> entityList = new ArrayList<>();
            for (String ee : (List<String>)entry.getValue()) {
                entityList.add(ee);
            }

            retValue.put(year, entityList);
        }

        return retValue;
    }

    public List<TermResultsDoc> findbyEntityIdAndTermName(String entityId, String termName) {

        return resultsSolrRepo.findByEntityIdAndTermNameAndMyDocType(entityId, termName,
                TermResultsDocVO.TermResultsDocType.primaryTermResult.toString());
    }

    public List<TermResultsDoc> findbyEntityIdListAndTermId(String entityIds, String termId) {

        List<TermResultsDoc> dataList = resultsSolrRepo.findByEntityIdListAndTermIdAndMyDocType(entityIds, termId,
                TermResultsDocVO.TermResultsDocType.primaryTermResult.toString());

        if (dataList == null) {
            return dataList;
        }

        List<TermResultsDoc> ret = new ArrayList<>();
        for (TermResultsDoc trd : dataList) {

            if (trd.FY < this.minFYToProcess) {
                continue;
            }

            if (this.skipFQProcessing && trd.FQ != FQTypeCode.FY) {
                continue;
            }

            ret.add(trd);
        }

        return ret;
    }

    public List<TermResultsDoc> findByTermsEntitiesAndFY(Collection<String> termIds,
            Collection<String> entityIds, int start, int end, Boolean isForAllEntities) {
        if (isForAllEntities) {
            return resultsSolrRepo.findByTermsAndFY(termIds, start, end);
        } else {
            return resultsSolrRepo.findByTermsInAndEntitiesInAndFYBetween(termIds, entityIds, start, end);
        }
    }

    public HashMap<String, TermResultsDoc> getResultsByTermId(Collection<String> termIds,
            String entityId, int fiscalYear) {

        List<TermResultsDoc> results = resultsSolrRepo.findByTermsInAndEntityIdAndFYAndFQ(termIds, entityId, fiscalYear, "FY", PageRequest.of(0, 2000));
        HashMap<String, TermResultsDoc> ret = new HashMap<>();

        results.forEach((doc) -> {
            ret.put(doc.termId, doc);
        });

        return ret;

    }

    public List<TermResultsDoc> findByTermsEntitiesAndFYQuarterlyOnly(Collection<String> termIds,
            Collection<String> entityIds, int start, int end, Boolean isForAllEntities) {
        if (isForAllEntities) {
            return resultsSolrRepo.findByTermsAndFYQuarterlyOnly(termIds, start, end);
        } else {
            return resultsSolrRepo.findByTermsEntitiesAndFYQuarterlyOnly(termIds, entityIds, start, end);
        }

    }

    public List<TermResultsDoc> findByTermsEntitiesAndFYYealyOnly(Collection<String> termIds,
            Collection<String> entityIds, int start, int end, Boolean isForAllEntities) {
        if (isForAllEntities) {
            return resultsSolrRepo.findByTermsAndFYYealyOnly(termIds, start, end);
        } else {
            return resultsSolrRepo.findByTermsEntitiesAndFYYealyOnly(termIds, entityIds, start, end);
        }
    }

    public List<TermResultsDoc> findByTermIdEntityIdAndFY(String termId,
            String entityId, int start, int end) {
        return resultsSolrRepo.findByTermIdEntityIdAndFY(termId, entityId, start, end);
    }

    public List<TermResultsDoc> findByTermIdEntityIdAndFYQuarterlyOnly(String termId,
            String entityId, int start, int end) {
        return resultsSolrRepo.findByTermIdEntityIdAndFYQuarterlyOnly(termId, entityId, start, end);
    }

    public List<TermResultsDoc> findByTermIdEntityIdAndFYYealyOnly(String termId,
            String entityId, int start, int end) {
        return resultsSolrRepo.findByTermIdEntityIdAndFYYealyOnly(termId, entityId, start, end);
    }

    public List<SECFilings> findFilingsByEntity(String entityIds) {
    	List<SECFilings> results = new ArrayList<SECFilings>();
//        String query = String.format("entityId:%s %s AND -formType:S-1 AND -formType:POS AM", entityIds,
//                getFiscalConfigurationFilingQueryLimits());
//
//        return idapAPIService.findFilingsByQuery(query);
             
    	List<FilingDoc> filingDocs = filingService.findFilingsByEntityIds(entityIds);
    	if (filingDocs != null) {
    		for (FilingDoc doc : filingDocs) {
    			SECFilings filing = new SECFilings();
    			filing.entityId = doc.getEntityId();
    			filing.filingPeriod = Integer.parseInt(doc.getFilingPeriod());
    			filing.fiscalYearEnd = doc.getFiscalYearEnd();
    			filing.formType = doc.getFormType();
    			filing.FY = Integer.parseInt(doc.getFy_l());
    			filing.FQ = doc.getFp();
    			results.add(filing);
    		}
    	}
    	return results;
    } 

    public HashMap<String, FYFQInfo> getValidPeriods(String entityId) {
        HashMap<String, FYFQInfo> ret = new HashMap<String, FYFQInfo>();
        List<SECFilings> filings = this.findFilingsByEntity(entityId);

        for (SECFilings f : filings) {

            if (f.FY == 0) {
                continue;
            }
            if (f.FQ == null) {
                continue;
            }

            if (f.FY < this.minFYToProcess) {
                continue;
            }

            if (skipFQProcessing && !f.FQ.equals("FY")) {
                continue;
            }
//            if (f.formType.equals("POS AM")) {
//                continue;
//            }
//            if (f.formType.equals("S-1/A")) {
//                continue;
//            }
//            if (f.formType.equals("S-1")) {
//                continue;
//            }

            String key = buildkey(f.entityId, f.FQ, f.FY);

            if (!ret.containsKey(key)) {
                ret.put(key, new FYFQInfo(f.entityId, f.FY, f.FQ));
            }

        }

        return ret;
    }

    
    
    public HashMap<String, FYFQInfo> getMissingFilingPeriodsForEntityList(HashMap<String, Entity> entityList,
            HashMap<String, FYFQInfo> allFilingPeriods) {
        HashMap<String, FYFQInfo> ret = new HashMap<>();
        
        for (int fy = this.minFYToProcess; fy <= this.maxFYToProcess; fy++) {

            for (Entity e : entityList.values()) {
                String key = buildkey(e.getEntityId(), "FY", fy);
                if (!allFilingPeriods.containsKey(key)) {
                    ret.put(key, new FYFQInfo(e.getEntityId(), fy, "FY"));
                }

                if (!skipFQProcessing) {
                    key = buildkey(e.getEntityId(), "Q1", fy);
                    if (!allFilingPeriods.containsKey(key)) {
                        ret.put(key, new FYFQInfo(e.getEntityId(), fy, "Q1"));
                    }
                    key = buildkey(e.getEntityId(), "Q2", fy);
                    if (!allFilingPeriods.containsKey(key)) {
                        ret.put(key, new FYFQInfo(e.getEntityId(), fy, "Q2"));
                    }

                    key = buildkey(e.getEntityId(), "Q3", fy);
                    if (!allFilingPeriods.containsKey(key)) {
                        ret.put(key, new FYFQInfo(e.getEntityId(), fy, "Q3"));
                    }

                }
            }
        }

        return ret;
    }

    public HashMap<String, FYFQInfo> getValidPeriodsForEntityList(String entityIds) {
        HashMap<String, FYFQInfo> ret = new HashMap<String, FYFQInfo>();
        List<SECFilings> filings = this.findFilingsByEntity(entityIds);

        for (SECFilings f : filings) {

            if (f.FY == 0) {
                continue;
            }
            if (f.FQ == null) {
                continue;
            }

            if (f.FY < this.minFYToProcess) {
                continue;
            }

            if (skipFQProcessing && !f.FQ.equals("FY")) {
                continue;
            }
            String key = buildkey(f.entityId, f.FQ, f.FY);

            if (!ret.containsKey(key)) {
                ret.put(key, new FYFQInfo(f.entityId, f.FY, f.FQ));
            }

        }

        return ret;
    }

    public void saveDerivedZeroData(Collection<TermResultsDerivedZero> col) {

        if (col == null || col.isEmpty()) {
            return;
        }

        List<TermResultsDoc> saveList = new ArrayList<>();
        col.stream().map((item) -> {
            TermResultsDoc doc = new TermResultsDoc();
            doc.setEntityId(item.getEntityId());
            doc.termId = item.getTermId();
            doc.FQ = FQTypeCode.getFYTypeFromCode(item.getFQ());
            doc.FY = item.getFY();
            doc.rank = 99;
            doc.verifiedNotes = item.getVerifiedNotes();
            doc.myDocType = TermResultsDocVO.TermResultsDocType.derivedZeroTermResult;
            doc.resolvedExpression = item.getVerifiedNotes();
            doc.validationMessages = new ArrayList<>();
            doc.validationMessages.add("Is Derived Zero");
            doc.validationStatus = ValidationStatus.ValidNoData;
            if (item.getHasTermResult()) {
                doc.myDocType = TermResultsDocVO.TermResultsDocType.derivedZeroHasTermResult;
            }
            return doc;
        }).forEachOrdered((doc) -> {
            saveList.add(doc);
        });

        this.resultsSolrRepo.saveAll(saveList);
    }

    public void saveDerivedNonZeroData(Collection<TermResultsDerivedNonZero> col) {

        if (col == null || col.isEmpty()) {
            return;
        }

        List<TermResultsDoc> saveList = new ArrayList<>();
        col.stream().map((item) -> {
            TermResultsDoc doc = new TermResultsDoc();
            doc.setEntityId(item.getEntityId());
            doc.termId = item.getTermId();
            doc.FQ = FQTypeCode.getFYTypeFromCode(item.getFQ());
            doc.FY = item.getFY();
            //doc.verifiedNotes = item.getVerifiedNotes();
            doc.rank = 99;
            doc.myDocType = TermResultsDocVO.TermResultsDocType.derivedNonZeroTermResult;
            return doc;
        }).forEachOrdered((doc) -> {
            saveList.add(doc);
        });

        this.resultsSolrRepo.saveAll(saveList);
    }

    public List<TermResultsDoc> findByEntityIdListAndTermIdList(String entityId, String termIds) {

        return this.resultsSolrRepo.findByEntityIdListAndTermIdAndMyDocType(entityId, termIds,
                TermResultsDocVO.TermResultsDocType.primaryTermResult.toString());
    }

    public List<TextBlockInfo> getTextBlocks(String entityIds,
            String[] elementNames) {

//        String query = String.format("sub_cik:%s AND %s %s",
//                entityIds,
//                buildElementNameElasticQuery(elementNames),
//                this.getFiscalConfigurationFactQueryLimits());
//
//        return this.idapAPIService.findTextBlockFacts(query, "cell_elt,sub_cik,fact_fy_i,fact_fp");
        
        String cell_elts = buildElementNameElasticQuery(elementNames);
        List<NormalizedFact> docs = normalizedFactService.findTextBlockFacts(entityIds, cell_elts);
        List<TextBlockInfo> results = new ArrayList<TextBlockInfo>();
        
        if (docs != null) {
        	for(NormalizedFact doc : docs) {
        		TextBlockInfo tbi = new TextBlockInfo();
        		tbi.cik = Integer.parseInt(doc.getCikString());
        		tbi.FY = doc.getFact_fy_i();
        		tbi.FQ = doc.getFact_fp().name();
        		tbi.textBlockName = doc.getCell_elt();
        		results.add(tbi);
        	}
        }
        
        return results;
    }

//    private String buildElementNameSolrQuery(String[] elementNames) {
//        //sub_cik:(51143 63908 21344 34088 310158)
//        StringBuilder bld = new StringBuilder();
//        bld.append("cell_elt:(");
//        for (String name : elementNames) {
//            bld.append(String.format("%s ", name));
//        }
//        bld.append(")");
//        return bld.toString();
//    }
    
    private String buildElementNameElasticQuery(String[] elementNames) {
        //sub_cik:(51143, 63908, 21344, 34088, 310158)
        StringBuilder bld = new StringBuilder();
        for (String name : elementNames) {
            bld.append(String.format("%s,", name));
        }
        String result = bld.toString().trim();
		result = result.substring(0, result.length() - 1); // remove comma at the end
		return result;
    }


    public TermResultsDoc CreateCopy(TermResultsDoc source) {
        TermResultsDoc ret = new TermResultsDoc();
        ret.myDocType = source.myDocType;
        ret.FQ = source.FQ;
        ret.FY = source.FY;
        ret.termId = source.termId;
        ret.setEntityId(source.getEntityId());
        ret.termName = source.termName;
        ret.cik = source.cik;
        ret.stockSymbol = source.stockSymbol;
        ret.companyName = source.companyName;
        ret.periodType = source.periodType;
        ret.rank = source.rank;
        ret.effectiveRank = source.effectiveRank;
        ret.expType = source.expType;
        ret.expression = source.expression;
        ret.resolvedExpression = source.resolvedExpression;
        ret.elementName = source.elementName;
        ret.dimExpression = source.dimExpression;
        ret.dimMemberName = source.dimMemberName;
        ret.hasDimensionalData = source.hasDimensionalData;
        ret.lastModified = source.lastModified;
        ret.isFilingPeriod = source.isFilingPeriod;
        ret.value = source.value;
        ret.filingDate = source.filingDate;
        ret.percentVarianceWithPrevPeriod = source.percentVarianceWithPrevPeriod;
        ret.percentVarianceWithPrevYear = source.percentVarianceWithPrevYear;
        ret.validationStatus = source.validationStatus;
        ret.validationMessages = source.validationMessages;

        return ret;
    }
}
