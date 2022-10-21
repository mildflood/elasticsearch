/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import static gov.sec.idap.maxds.calculation.DerivedZeroTermExpressionProcessor.buildkey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.FacetPivotFieldEntry;
import org.springframework.stereotype.Service;

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
import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;
import gov.sec.idap.maxds.elasticsearch.repository.TermResultsDocRepository;
import gov.sec.idap.maxds.elasticsearch.service.EntityService;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;
//import gov.sec.idap.maxds.solr.document.TermResultsDoc;
//import gov.sec.idap.maxds.solr.repository.TermResultsDocRepository;

@Service("termResultServiceSolr")
public class TermResultServiceSolr {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EntityService entityService;

    @Autowired
    private SecApiService idapAPIService;

    @Autowired
    private TermRuleService termRuleService;

    @Autowired
    private TermResultsDocRepository resultsSolrRepo;

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
        resultsSolrRepo.deleteByTermIdAndEntityId(termId, entityId);
    }

    public void saveTermResults(List<TermResultsDoc> termResults) {
        resultsSolrRepo.saveAll(termResults);
    }

    public List<Entity> getEntitiesForTermRuleAndRank(String termId, int rankId, Boolean justRank,
            Boolean mapped,
            int minYearToCheck, int maxYearToCheck, Boolean includeQuarterly,
            String division, String sector, String sic, String filerCategory, String entityInputId) {

        HashMap<String, Entity> entityMap = new HashMap();

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

        HashMap<String, String> entityMap = new HashMap();
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

    //http://184.80.156.127:8983/solr/TermResults/select?facet.field=entityId&facet=on&indent=on&q=myDocType:primaryTermResult%20AND%20entityId:(0000037785%20OR%200000002969%20OR%200000778164)&rows=1&wt=json
    //http://184.80.156.127:8983/solr/TermResults/select?
    //q=myDocType:primaryTermResult AND entityId:(0000037785 OR 0000002969 OR 0000778164) AND FY:[2012 TO 2015] AND FQ:FY
    //&facet.field=entityId&facet=on&facet.mincount=1
    //&rows=0&wt=json&indent=on
    //combined
    //http://184.80.156.127:8983/solr/TermResults/select?q=myDocType:primaryTermResult AND entityId:(0000037785 OR 0000002969 OR 0000778164) AND FY:[2012 TO 2015] AND FQ:FY&facet.field=entityId&facet=on&facet.mincount=1&rows=0&wt=json&indent=on
    //myDocType:derivedZeroTermResult AND entityId:(0000037785 OR 0000002969 OR 0000778164) AND FY:[2012 TO 2015] AND FQ:FY
    //facet.field=entityId&facet=on&facet.mincount=1
    private List<String> getDistinctEntitiesByType(String type,
            String termId, int rank,
            int minYearToCheck, int maxYearToCheck, Boolean includeQuarterly,
            Collection<String> entityMap) {
        FacetPage<TermResultsDoc> facetPage;
        List<String> coveredEntities = new ArrayList<>();

        LOG.info("getDistinctEntitiesByType Started: " + termId + type);
        LOG.info("Entity Count : " + entityMap.size());
        if (includeQuarterly) {
        	//Commented to display negative ranks records as well on coverage details
            /*if (rank > 0 && rank != 99) {*/
        	if (rank > 0 && rank != 99) {
//        		facetPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFyRangeRank(termId, entityMap,
//                        minYearToCheck, maxYearToCheck, type, rank, null);
            } else {
//            	facetPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFyRange(termId, entityMap,
//                        minYearToCheck, maxYearToCheck, type, null);
            }

        } else {

        	//Commented to display negative ranks records as well on coverage details
            /*if (rank > 0 && rank != 99) {*/
        	if (rank > 0 && rank != 99) {
//        		facetPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFyRangeFQRank(termId, entityMap,
//                        minYearToCheck, maxYearToCheck, "FY", type, rank, null);
            } else {
//            	facetPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFyRangeFQ(termId, entityMap,
//                        minYearToCheck, maxYearToCheck, "FY", type, null);
            }

        }

        LOG.info("getDistinctEntitiesByType completed starting to calcualte facet data: " + termId + type);
//        facetPage.getAllFacets().stream().forEach(page -> page.forEach(facet -> {
//            coveredEntities.add(facet.getValue());
//        }));
        LOG.info("getDistinctEntitiesByType completed : " + termId + type);
        return coveredEntities;

    }

    private HashMap<String, Boolean> getDistinctEntitiesWithValidationErrorsByType(String type,
            String termId, int rank,
            int minYearToCheck, int maxYearToCheck, Boolean includeQuarterly,
            Collection<String> entityMap) {
        FacetPage<TermResultsDoc> facetPage;
        HashMap<String, Boolean> coveredEntities = new HashMap<>();

        LOG.info("getDistinctEntitiesByType Started: " + termId + type);
        LOG.info("Entity Count : " + entityMap.size());
        if (includeQuarterly) {

            if (rank > 0 && rank != 99) {
//            	facetPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFyRangeRankWithValError(termId, entityMap,
//                        minYearToCheck, maxYearToCheck, type, rank, null);
            } else {
//            	facetPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFyRangeWithValError(termId, entityMap,
//                        minYearToCheck, maxYearToCheck, type, null);
            }

        } else {

            if (rank > 0 && rank != 99) {
//            	facetPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFyRangeFQRankWithValError(termId, entityMap,
//                        minYearToCheck, maxYearToCheck, "FY", type, rank, null);
            } else {
//            	facetPage = resultsSolrRepo.findEntityFacetByTermIdEntitiesFyRangeFQWithValError(termId, entityMap,
//                        minYearToCheck, maxYearToCheck, "FY", type, null);
            }

        }

        LOG.info("getDistinctEntitiesByType completed starting to calcualte facet data: " + termId + type);
//        facetPage.getAllFacets().stream().forEach(page -> page.forEach(facet -> {
//            coveredEntities.put(facet.getValue(), true);
//        }));
        LOG.info("getDistinctEntitiesByType completed : " + termId + type);
        return coveredEntities;

    }

    public HashMap<Integer, TermRuleStatistics.TermRuleStatisticsByRank> buildStatisticsByRank(
            String termId,
            int minYearToCheck, int maxYearToCheck, Boolean includeQuarterly,
            HashMap<String, String> entityMap) {

        HashMap<Integer, TermRuleStatistics.TermRuleStatisticsByRank> statisticsByRank = new HashMap<>();

        FacetPage<TermResultsDoc> facetPage = null;

        if (includeQuarterly) {
//            facetPage = resultsSolrRepo.findEntityRankPivotFacetByTermIdEntitiesFyRange(termId,
//                    entityMap.keySet(), minYearToCheck, maxYearToCheck,
//                    TermResultsDocVO.TermResultsDocType.primaryTermResult.toString(),
//                    null);

        } else {
//            facetPage = resultsSolrRepo.findEntityRankPivotFacetByTermIdEntitiesFyRangeFQ(termId,
//                    entityMap.keySet(), minYearToCheck, maxYearToCheck,
//                    TermResultsDocVO.TermResultsDocType.primaryTermResult.toString(),
//                    null);
        }

        List<FacetPivotFieldEntry> entries
                = facetPage.getPivot("rank,entityId");

        HashMap<Integer, FacetPivotFieldEntry> entriesByRank = new HashMap<>();
        for (FacetPivotFieldEntry rE : entries) {
            int rank = Integer.parseInt(rE.getValue());
            entriesByRank.put(rank, rE);
        }

        HashMap<String, Boolean> distinctEntities = new HashMap<>();

        for (Map.Entry<Integer, FacetPivotFieldEntry> entry : entriesByRank.entrySet()) {
            int rank = entry.getKey();
            TermRuleStatistics.TermRuleStatisticsByRank stat = new TermRuleStatistics.TermRuleStatisticsByRank();

            stat.mappedEntityCount = 0;
            stat.rankId = rank;

            for (FacetPivotFieldEntry ee : entry.getValue().getPivot()) {

                String entity = ee.getValue();
                stat.nonDistinctMappedEntityCount++;

                if (!distinctEntities.containsKey(entity)) {
                    stat.mappedEntityCount++;
                    distinctEntities.put(entity, Boolean.FALSE);
                }
            }

            statisticsByRank.put(rank, stat);
        }

//        facetPage = resultsSolrRepo.findEntityRankPivotFacetByTermIdEntitiesFyRange(termId,
//                entityMap.keySet(), minYearToCheck, maxYearToCheck,
//                TermResultsDocVO.TermResultsDocType.derivedZeroTermResult.toString(),
//                null);

        for (FacetPivotFieldEntry rE : facetPage.getPivot("rank,entityId")) {
            int rank = 99;
            TermRuleStatistics.TermRuleStatisticsByRank stat = new TermRuleStatistics.TermRuleStatisticsByRank();

            stat.mappedEntityCount = 0;
            stat.rankId = 99;
            for (FacetPivotFieldEntry ee : rE.getPivot()) {

                String entity = ee.getValue();
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

        String docType = useMissingCollection ? TermResultsDocVO.TermResultsDocType.derivedZeroTermResult.toString()
                : TermResultsDocVO.TermResultsDocType.primaryTermResult.toString();

        FacetPage<TermResultsDoc> facetPage = null;

        if (includeQuarterly) {

            if (isForAllEntities) {
//                facetPage = resultsSolrRepo.findAllEntityYearPivotFacetByTermIdEntitiesFyRange(termId,
//                        minYearToCheck, maxYearToCheck,
//                        docType,
//                        null);
            } else {
//                facetPage = resultsSolrRepo.findEntityYearPivotFacetByTermIdEntitiesFyRange(termId,
//                        entitities, minYearToCheck, maxYearToCheck,
//                        docType,
//                        null);
            }

        } else {
            if (isForAllEntities) {
//                facetPage = resultsSolrRepo.findAllEntityYearPivotFacetByTermIdEntitiesFyRangeFQ(termId,
//                        minYearToCheck, maxYearToCheck,
//                        docType,
//                        null);
            } else {
//                facetPage = resultsSolrRepo.findEntityYearPivotFacetByTermIdEntitiesFyRangeFQ(termId,
//                        entitities, minYearToCheck, maxYearToCheck,
//                        docType,
//                        null);
            }
        }

        List<FacetPivotFieldEntry> entries
                = facetPage.getPivot("FY,entityId");

        for (FacetPivotFieldEntry rE : entries) {
            int year = Integer.parseInt(rE.getValue());

            List<String> entityList = new ArrayList<>();
            for (FacetPivotFieldEntry ent : rE.getPivot()) {
                entityList.add(ent.getValue());
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
    	return null;

//        List<TermResultsDoc> dataList = resultsSolrRepo.findByEntityIdListAndTermIdAndMyDocType(entityIds, termId,
//                TermResultsDocVO.TermResultsDocType.primaryTermResult.toString());
//
//        if (dataList == null) {
//            return dataList;
//        }
//
//        List<TermResultsDoc> ret = new ArrayList<>();
//        for (TermResultsDoc trd : dataList) {
//
//            if (trd.FY < this.minFYToProcess) {
//                continue;
//            }
//
//            if (this.skipFQProcessing && trd.FQ != FQTypeCode.FY) {
//                continue;
//            }
//
//            ret.add(trd);
//        }
//
//        return ret;
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

    public List<SECFilings> findFilingsByEntity(String entity) {

        String query = String.format("entityId:%s %s AND -formType:S-1 AND -formType:POS AM", entity,
                getFiscalConfigurationFilingQueryLimits());

        return idapAPIService.findFilingsByQuery(query);

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
    	return null;
//        return this.resultsSolrRepo.findByEntityIdListAndTermIdAndMyDocType(entityId, termIds,
//                TermResultsDocVO.TermResultsDocType.primaryTermResult.toString());
    }

    public List<TextBlockInfo> getTextBlocks(String entityIds,
            String[] elementNames) {

        String query = String.format("sub_cik:%s AND %s %s",
                entityIds,
                buildElementNameSolrQuery(elementNames),
                this.getFiscalConfigurationFactQueryLimits());

        return this.idapAPIService.findTextBlockFacts(query, "cell_elt,sub_cik,fact_fy_i,fact_fp");

    }

    private String buildElementNameSolrQuery(String[] elementNames) {
        //sub_cik:(51143 63908 21344 34088 310158)
        StringBuilder bld = new StringBuilder();
        bld.append("cell_elt:(");
        for (String name : elementNames) {
            bld.append(String.format("%s ", name));
        }
        bld.append(")");
        return bld.toString();
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
