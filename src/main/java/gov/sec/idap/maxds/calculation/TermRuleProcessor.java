/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import gov.sec.idap.maxds.calculation.Domain.EntityGroupedByExpressionMap;
import gov.sec.idap.maxds.calculation.Domain.ResolvedTermExpression;

import gov.sec.idap.maxds.domain.PostRequestResult;
import gov.sec.idap.maxds.domain.ProcessingStatusCode;
import gov.sec.idap.maxds.domain.TermExpression;
import gov.sec.idap.maxds.domain.TermExpressionTypeCode;
import gov.sec.idap.maxds.domain.TermRule;
import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.service.EntityService;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;
import gov.sec.idap.maxds.elasticsearch.service.NormalizedFactService;
import gov.sec.idap.maxds.elasticsearch.service.ProcessingLogService;
import gov.sec.idap.maxds.elasticsearch.service.TermResultService;

public class TermRuleProcessor {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    int entityListSize;

    List<Entity> myEntities;
    TermRule myTermRule;
    int termOrderId = 1;
    String termRuleId;
    EntityService myEntityService;
    TermRuleService myTermRuleService;
    TermResultService myTermResultService;
    NormalizedFactService myNormalizedFactService;
    ProcessingLogService myProcessingLogService;
    String myProcessingId;
    Boolean isAllEntities = false;
    String userName;

    Boolean populateDerivedNonZeroData = false;

    String myDivision, mySector, mySic, myFilerCategory;

    Date startProcessingDate;
    int entityCount = 0;

    String processingInfoHeader = "";

    public TermRuleProcessor(String id, int termOrderId, EntityService es,
            TermRuleService ts, TermResultService tresults,
            NormalizedFactService ns, ProcessingLogService ps,
            String division, String sector, String sic, String filerCategory,
            Boolean populateNonZeroData, int processingGroupSize, String userName,
            String header, String processingId) {
        this.termRuleId = id;
        this.termOrderId = termOrderId;
        this.myEntityService = es;
        this.myTermRuleService = ts;
        this.myTermResultService = tresults;
        this.myNormalizedFactService = ns;
        this.myProcessingLogService = ps;
        myProcessingId = processingId;// UUID.randomUUID().toString();

        this.myDivision = division;
        this.mySector = sector;
        this.mySic = sic;
        this.myFilerCategory = filerCategory;
        this.populateDerivedNonZeroData = populateNonZeroData;

        this.entityListSize = processingGroupSize == 0 ? 100 : processingGroupSize;
        this.userName = userName;
        this.processingInfoHeader = header;
    }

    public PostRequestResult process(String entityId) {

        startProcessingDate = new Date();

        LOG.debug("process");
        PostRequestResult validationResult = validateData(entityId);
        if (validationResult.status == false) {
            return validationResult;
        }
        this.entityCount = entityId == null || entityId.isEmpty() ? this.myEntities.size() : 1;
        try {
            this.myTermRule.setProcessingStatus(ProcessingStatusCode.InProgress);
            this.myTermRuleService.SaveAsIs(myTermRule);
            deleteExistingResults();
            	
            return processEntityList();

        } catch (Exception exp) {

            return PostRequestResult.GetErrorResult("Processing Term Rule failed with Exception " + exp.getMessage());
        } finally {

            //get the term rule one more time .. as the processing could have changed some of the expressions
            //merged expressions etc....
            myTermRule = myTermRuleService.getTermRuleByTermId(myTermRule.getTermId());
            myTermRule.setProcessingStatus(ProcessingStatusCode.Completed);
            this.myTermRuleService.SaveAsIs(myTermRule);

            Date end = new Date();
            long difference = (end.getTime() - startProcessingDate.getTime()) / 1000;

            LOG.debug("process time in Seconds: " + String.format("%d", difference));
            if (entityId == null || entityId.isEmpty()) {
                myProcessingLogService.SaveCompletedStatus(myProcessingId, "",
                        myTermRule.getTermId(), String.format("%s Completed processing of Term rule in %d seconds ",
                        this.processingInfoHeader,
                        difference),
                        difference, entityCount, userName);
            } else {
                myProcessingLogService.SaveCompletedStatus(myProcessingId, entityId,
                        myTermRule.getTermId(), String.format("Completed processing of Term rule for entity %s in %d seconds ",
                        entityId, difference),
                        difference, entityCount, userName);
            }
        }

    }
    
    public PostRequestResult process(String termId, String entityId, boolean goToPeer) {

        startProcessingDate = new Date();

        LOG.debug("process");
        PostRequestResult validationResult = validateData(termId, entityId, goToPeer);
        if (validationResult.status == false) {
            return validationResult;
        }
        this.entityCount = this.myEntities.size();
        try {
            this.myTermRule.setProcessingStatus(ProcessingStatusCode.InProgress);
            this.myTermRuleService.SaveAsIs(myTermRule);
            deleteExistingResults();
            	
            return processEntityList();

        } catch (Exception exp) {

            return PostRequestResult.GetErrorResult("Processing Term Rule failed with Exception " + exp.getMessage());
        } finally {

            //get the term rule one more time .. as the processing could have changed some of the expressions
            //merged expressions etc....
            myTermRule = myTermRuleService.getTermRuleByTermId(myTermRule.getTermId());
            myTermRule.setProcessingStatus(ProcessingStatusCode.Completed);
            this.myTermRuleService.SaveAsIs(myTermRule);

            Date end = new Date();
            long difference = (end.getTime() - startProcessingDate.getTime()) / 1000;

            LOG.debug("process time in Seconds: " + String.format("%d", difference));

            myProcessingLogService.SaveCompletedStatus(myProcessingId, "",
                        myTermRule.getTermId(), String.format("%s Completed processing of Term rule in %d seconds ",
                        this.processingInfoHeader,
                        difference),
                        difference, entityCount, userName);
        }

    }

    private PostRequestResult validateData(String entityId) {
        loadData(entityId);
        if (this.myEntities == null || this.myEntities.isEmpty()) {
            return PostRequestResult.GetErrorResult("Entity collection is empty. Nothing to process.");
        }

        if (this.myTermRule == null) {
            return PostRequestResult.GetErrorResult("Term Rule with the specified ID does not exist. Nothing to process.");

        }
        

        if (this.myTermRule.getProcessingStatus() == ProcessingStatusCode.InProgress) {
            return PostRequestResult.GetErrorResult("Term Rule is already being processed. Please wait for it to complete.");
        }

        return PostRequestResult.GetSuccessResult();
    }
    private PostRequestResult validateData(String termId, String entityId, boolean goToPeer) {
        loadData(termId, entityId, goToPeer);
        if (this.myEntities == null || this.myEntities.isEmpty()) {
            return PostRequestResult.GetErrorResult("Entity collection is empty. Nothing to process.");
        }

        if (this.myTermRule == null) {
            return PostRequestResult.GetErrorResult("Term Rule with the specified ID does not exist. Nothing to process.");

        }
        

        if (this.myTermRule.getProcessingStatus() == ProcessingStatusCode.InProgress) {
            return PostRequestResult.GetErrorResult("Term Rule is already being processed. Please wait for it to complete.");
        }

        return PostRequestResult.GetSuccessResult();
    }

    private void deleteExistingResults() {

        if (isAllEntities) {
            LOG.debug("deleteExistingResults for all entities Ter RuleId : " + termRuleId);
            myTermResultService.deleteByTermRule(termRuleId);
        } else {
            for (Entity et : myEntities) {
                LOG.debug("deleteExistingResults for entity : " + et.getCompanyName());
                myTermResultService.deleteByTermIdAndEntityId(termRuleId, et.getEntityId());

            }

        }

    }

    private void loadData(String entityId) {

        LOG.debug("loadAllEntities");
        if (entityId == null || entityId.isEmpty()) {
            isAllEntities = true;
            myEntities = (List<Entity>) myEntityService.findAllEntities();

            HashMap<String, String> entityMap = null;
            if (myDivision != null && !myDivision.isEmpty() && !myDivision.equals("NULL")) {
                entityMap = myEntityService.findByDivisionSectorAndSicCode(myDivision, mySector, mySic, myFilerCategory);
                List<Entity> reducedList = new ArrayList<Entity>();
                for (Entity et : myEntities) {
                    if (entityMap.containsKey(et.getEntityId())) {
                        reducedList.add(et);
                    }

                }
                myEntities = reducedList;
                isAllEntities = false;

            }

        } else {
            myEntities = new ArrayList<Entity>();
            Entity et = myEntityService.getEntityById(entityId);
            if (et != null) {
                myEntities.add(et);
            }
        }
        myTermRule = myTermRuleService.getTermRuleByTermId(termRuleId);
        if (termOrderId > 0) {
            myTermRule.setOrder(termOrderId);
        }

    }
    
    private void loadData(String termId, String entityId, boolean goToPeer) {
        LOG.debug("load Entities");
        if (entityId == null || entityId.isEmpty()) {
            isAllEntities = true;
            myEntities = (List<Entity>) myEntityService.findAllEntities();

            HashMap<String, String> entityMap = null;
            if (myDivision != null && !myDivision.isEmpty() && !myDivision.equals("NULL")) {
                entityMap = myEntityService.findByDivisionSectorAndSicCode(myDivision, mySector, mySic, myFilerCategory);
                List<Entity> reducedList = new ArrayList<Entity>();
                for (Entity et : myEntities) {
                    if (entityMap.containsKey(et.getEntityId())) {
                        reducedList.add(et);
                    }

                }
                myEntities = reducedList;
                isAllEntities = false;
            }
        } else {
        	
        	List<String> ciks = new ArrayList<String>();
			if (goToPeer) {
				ciks = getPeerGroupEntities(termId, entityId);
			} else {
				ciks.add(entityId);
			}					
            myEntities = new ArrayList<Entity>();
            for (String cik: ciks) {
            	Entity et = myEntityService.getEntityById(cik);
                if (et != null) {
                    myEntities.add(et);
                }
            }
        }
        myTermRule = myTermRuleService.getTermRuleByTermId(termRuleId);
        if (termOrderId > 0) {
            myTermRule.setOrder(termOrderId);
        }

    }

    private List<String> getPeerGroupEntities(String termRuleId, String cik) {
    	List<String> ciks = new ArrayList<String>();
    	Entity et = myEntityService.getEntityById(cik);
    	
    	List<Entity> list = myTermResultService.getEntitiesForTermRuleAndRank(termRuleId,
                0, true, true, 2017, 2021, new Boolean(true), et.getDivision(), 
                et.getSector(), et.getSic(), et.getFilerCategory(), null);
    	
    	for( Entity e: list) {
    		ciks.add(e.getCik());
    	}
    	if (ciks.size() == 0) {
        	ciks.add(cik);
    	}
    	return ciks;
    }
    private PostRequestResult processEntityList() {
        if (myTermRule == null) {
            return PostRequestResult.GetErrorResult("Failed to load term rule by term Id.");
        }
        
        List<TermExpression> exps = this.myTermRule.getExpressions();
        if (exps == null || exps.isEmpty()) {
            //nothing to do for this term we can just move on to the next one...
            
            return PostRequestResult.GetSuccessResultWithProcessingLogGroupId(myProcessingId);

        }
        
        LOG.debug("Building term Rule provider");
        TermRuleInfoProvider termRuleProvider = new TermRuleInfoProvider(
                this.myTermRule, this.myTermRuleService);

        if (myProcessingLogService.IsCanceled(myProcessingId)) {
            return PostRequestResult.GetErrorResult("Term Processing has been canceled.");
        }

        LOG.debug("processEntityList");
        myProcessingLogService.SaveProgressStatus(myProcessingId, "", myTermRule.getTermId(),
                processingInfoHeader + "Starting Processing of Term Rule.", 0, this.entityCount, userName);
        
        termRuleProvider.buildEntityGroupedByExpressionMap(myEntities);
        ExtendedCheckProcessor ecFactsCache
                = myEntities.size() > 200 ? new ExtendedCheckProcessor(myNormalizedFactService) : null;

        int processedCount = 0;
        for (EntityGroupedByExpressionMap eg : termRuleProvider.entityListGroups) {

            if (eg.entityList.isEmpty() || eg.expressionMaps.isEmpty()) {
                continue;
            }

            HashMap<String, Entity> entMap = new HashMap<>();
            for (Entity et : eg.entityList) {

                entMap.put(et.getEntityId(), et);
                if (entMap.size() >= entityListSize) {
                    process(entMap, termRuleProvider, eg.expressionMaps, ecFactsCache);
                    processedCount += entMap.size();
                    entMap.clear();

                    if (processedCount > 25) {
                        Date end = new Date();
                        long difference = (end.getTime() - startProcessingDate.getTime()) / 1000;
                        String description = String.format("%s Completed processing of %d of %d entities in %d seconds",
                                processingInfoHeader, processedCount, this.entityCount, difference);

                        if (myProcessingLogService.IsCanceled(myProcessingId)) {
                            return PostRequestResult.GetErrorResult("Term Processing has been canceled.");
                        }
                        myProcessingLogService.SaveProgressStatus(myProcessingId, "", myTermRule.getTermId(),
                                description, difference, this.entityCount, userName);
                    }
                }

            }
            if (entMap.size() > 0) {
                process(entMap, termRuleProvider, eg.expressionMaps, ecFactsCache);

            }

        }

        return PostRequestResult.GetSuccessResultWithProcessingLogGroupId(myProcessingId);

    }

    private void process(HashMap<String, Entity> entMap,
            TermRuleInfoProvider termRuleProvider,
            EnumMap<TermExpressionTypeCode, List<ResolvedTermExpression>> expressionMapsToUse,
            ExtendedCheckProcessor ecFactsCache) {
        try {
            String processInfo = String.format("Starting processing of Term rule for Entity list size %d. ",
                    entMap.size());
            LOG.debug(processInfo);

            EntityListTermProcessor etp = new EntityListTermProcessor(entMap, termRuleProvider,
                    myEntityService, myTermRuleService, myTermResultService,
                    myNormalizedFactService, myProcessingLogService, ecFactsCache, myProcessingId,
                    populateDerivedNonZeroData, expressionMapsToUse);

            etp.process();
        } catch (Exception exp) {
            myProcessingLogService.SaveErrorStatus(myProcessingId, "", myTermRule.getTermId(),
                    String.format("Exception in processing term rule for entity list : %s", exp.getMessage()),
                    userName);
            throw exp;

        }
    }

}
