/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import gov.sec.idap.maxds.calculation.ExpressionParsingUtil;
import gov.sec.idap.maxds.calculation.ParallelTermRuleEntityProcessor;
import gov.sec.idap.maxds.calculation.TermRuleProcessor;
import gov.sec.idap.maxds.calculation.Domain.ExtendedCheckResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ExtendedCheckWithDimensionResolvedTermExpression;

import gov.sec.idap.maxds.domain.ExtendedCheckResolverInput;
import gov.sec.idap.maxds.domain.ExtendedCheckResolverOutput;
import gov.sec.idap.maxds.domain.PostRequestResult;
import gov.sec.idap.maxds.domain.TermExpressionTypeCode;
import gov.sec.idap.maxds.domain.TermRule;

import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.document.ProcessingLogDoc;
import gov.sec.idap.maxds.elasticsearch.service.EntityService;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;
import gov.sec.idap.maxds.elasticsearch.service.TermResultService;
import gov.sec.idap.maxds.elasticsearch.service.NormalizedFactService;
import gov.sec.idap.maxds.elasticsearch.service.ProcessingLogService;

@Service("processingService")
public class ProcessingService {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Environment env;

    @Autowired
    private EntityService entityService;

    @Autowired
    private TermRuleService termRulesService;

    @Autowired
    private TermResultService termResultService;

    @Autowired
    private NormalizedFactService normalizedFactService;

    @Autowired
    private ProcessingLogService processingLogService;
    
     @Value("${maxds.entitycount.processing.size:100}")
    int entityListSize;

    public PostRequestResult processTermRule(String termId, int orderId, String entityId, String userName) {
        LOG.debug("processTermRule for ID: " + termId);
        TermRuleProcessor trp = new TermRuleProcessor(termId,orderId, entityService, termRulesService,
                termResultService, normalizedFactService, processingLogService,null,null,null,null,
                canPopulateDerivedNonZeroData(), entityListSize, userName,"",UUID.randomUUID().toString() );

        return trp.process(entityId);

    }
    
    //For API call
    @Async
    public void apiProcessTermRule(String termId, int orderId, String entityId, String userName, String processId) {
        LOG.debug("processTermRule for ID: " + termId);
        TermRuleProcessor trp = new TermRuleProcessor(termId,orderId, entityService, termRulesService,
                termResultService, normalizedFactService, processingLogService,null,null,null,null,
                canPopulateDerivedNonZeroData(), entityListSize, userName,"", processId );
        trp.process(entityId);
    }
    
    //For API call
    //@Async
    public void apiProcessRuleWithCriteria(String termId, int orderId, 
            String entityId, String division, String sector, String sic, String filerCategory,
            String userName, String logHeader, String processingId) {
        LOG.debug("processTermRule for ID: " + termId);
        TermRuleProcessor trp = new TermRuleProcessor(termId, orderId,  entityService, termRulesService,
                termResultService, normalizedFactService, processingLogService,
                division, sector, sic, filerCategory,
                canPopulateDerivedNonZeroData(), entityListSize, 
                userName,logHeader, processingId);   
        trp.process(entityId);
    }
    public void apiProcessRuleWithCriteria(String termId, int orderId, 
            String entityId, boolean goToPeer, String division, String sector, String sic, String filerCategory,
            String userName, String logHeader, String processingId) {
        LOG.debug("processTermRule for ID: " + termId);
        TermRuleProcessor trp = new TermRuleProcessor(termId, orderId,  entityService, termRulesService,
                termResultService, normalizedFactService, processingLogService,
                division, sector, sic, filerCategory,
                canPopulateDerivedNonZeroData(), entityListSize, 
                userName,logHeader, processingId);   
        trp.process(termId, entityId, goToPeer);
    }
    
    @Async
    public void apiProcessAccuracyTest(List<TermRule> trs, String userId, int size) {
		if(size > 0) {
			if(!trs.isEmpty()) {
				this.processingLogService.ScheduleTermsForProcessing(trs);
			}
			ProcessingLogDoc nextDoc = processingLogService.getNextTermToProcess();
			
			while (nextDoc != null) {
				LOG.debug("Processing Rule : " + nextDoc.getTermId());
				TermRule tr = termRulesService.getTermRuleByTermId(nextDoc.getTermId());
				apiProcessRuleWithCriteria(tr.getTermId(), tr.getOrder(), null,
						null, null, null, null, userId,
						String.format("Processing Term %d of %d.", nextDoc.getProcessingSequence(), size),
						nextDoc.getProcessingGroupId());
				nextDoc = processingLogService.getNextTermToProcess();
			}
		}
	}
    
    @Async
    public void apiProcessAccuracyTestForEntities(List<TermRule> trs, String entityId, boolean goToPeer, String userId, int size) {
		if(size > 0) {
			if(!trs.isEmpty()) {
				this.processingLogService.ScheduleTermsForProcessing(trs);
			}
			ProcessingLogDoc nextDoc = processingLogService.getNextTermToProcess();
			
			while (nextDoc != null) {
				LOG.debug("Processing Rule : " + nextDoc.getTermId());
				TermRule tr = termRulesService.getTermRuleByTermId(nextDoc.getTermId());			
				
				//get peer group cik list ....
				List<String> ciks = new ArrayList<String>();
				
				apiProcessRuleWithCriteria(tr.getTermId(), tr.getOrder(), entityId, goToPeer,
								null, null, null, null, userId,
								String.format("Processing Term %d of %d.", nextDoc.getProcessingSequence(), size),
								nextDoc.getProcessingGroupId());			
				nextDoc = processingLogService.getNextTermToProcess();
			}
		}
	}
    
    public PostRequestResult ProcessRuleWithCriteria(String termId, int orderId, 
            String entityId, String division, String sector, String sic, String filerCategory,
            String userName, String logHeader, String processingId) {
        LOG.debug("processTermRule for ID: " + termId);
        TermRuleProcessor trp = new TermRuleProcessor(termId, orderId,  entityService, termRulesService,
                termResultService, normalizedFactService, processingLogService,
                division, sector, sic, filerCategory,
                canPopulateDerivedNonZeroData(), entityListSize, 
                userName,logHeader, processingId);   
        return trp.process(entityId);
    }

    public PostRequestResult deleteTermRule(String termId) {

        try {
            PostRequestResult ret = termRulesService.canDeleteTermRule(termId);
            if (!ret.status) {
                return ret;
            }
            termResultService.deleteByTermRule(termId);
            termRulesService.deleteByTermRule(termId);

        } catch (Exception exp) {
            return PostRequestResult.GetErrorResult(
                    String.format("Deleting term Rule failed with exception %s", exp.getMessage()));
        }
        return PostRequestResult.GetSuccessResult();
    }

    public List<ExtendedCheckResolverOutput> getExtendedElementsByResolverInput(
            ExtendedCheckResolverInput input) {

        Boolean hasDimension = input.getExpression().type == TermExpressionTypeCode.ExtendedCheckWithDim;

        ExtendedCheckResolvedTermExpression resolvedExpression
                = hasDimension
                        ? ExpressionParsingUtil.createExtendedCheckWithDimExpression(
                                input.getExpression())
                        : ExpressionParsingUtil.createExtendedCheckExpression(
                                input.getExpression());

        if (resolvedExpression.containsWords.isEmpty()) {
            return new ArrayList<>();
        }
        HashMap<String, String> entityMap
                = this.entityService.findByDivisionSectorAndSicCode(
                        input.getDivision(), input.getSector(),
                        input.getSic(), input.getFilerCategory());

        if (!hasDimension) {

            
            List<ExtendedCheckResolverOutput> output
                    = normalizedFactService.findMatchingExtendedElementNameInfo(
                            entityMap.keySet(), resolvedExpression.buildSolrQueryPartForElementName(),
                            resolvedExpression.periodType,
                            resolvedExpression.balanceType
                    );

            return output;
        } else {
            List<ExtendedCheckResolverOutput> output
                    = normalizedFactService.findMatchingExtendedElementNameInfoWithDimension(
                            entityMap.keySet(), resolvedExpression.buildSolrQueryPartForElementName(),
                            resolvedExpression.periodType,
                            resolvedExpression.balanceType
                    );
            //need to filter for dimensional matching...

            return buildOutput((ExtendedCheckWithDimensionResolvedTermExpression) resolvedExpression,
                    output);

        }

    }

    private List<ExtendedCheckResolverOutput> buildOutput(
            ExtendedCheckWithDimensionResolvedTermExpression resolvedExpression,
            List<ExtendedCheckResolverOutput> inputAllDim) {
        ArrayList<ExtendedCheckResolverOutput> ret = new ArrayList<>();

        for (ExtendedCheckResolverOutput item : inputAllDim) {

            item.PopulateDimensionalInfo();
            if (resolvedExpression.NamedAxisList == null
                    || resolvedExpression.NamedAxisList.size() == 0) {

                ret.add(item);
            } else {
                if (resolvedExpression.GetDimensionMatchingInfo(item.dimensions) != null) {

                    ret.add(item);
                }
            }

        }
        return ret;
    }

    public void processAllRules(int nofThreads) {
        ExecutorService execs = Executors.newFixedThreadPool(nofThreads);
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();

        termRulesService.findAll().forEach((TermRule termRule) -> {
            ((List<Entity>)entityService.findAllEntities()).stream().map((entity) -> {
                ParallelTermRuleEntityProcessor ptrep;
                ptrep = new ParallelTermRuleEntityProcessor(termRule, entity, termRulesService, entityService,
                        termResultService, normalizedFactService, processingLogService);
                return ptrep;
            }).forEachOrdered((ptrep) -> {
                tasks.add(ptrep);
            });
        });

        try {
            List<Future<Boolean>> results = execs.invokeAll(tasks);
            for (Future<Boolean> future : results) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException ex) {

        } finally {
            execs.shutdown();
        }
    }

    private Boolean canPopulateDerivedNonZeroData() {

        String ret = env.getProperty("derivedNonZero.populate", "None");
        return ret.equals("true");
    }
}
