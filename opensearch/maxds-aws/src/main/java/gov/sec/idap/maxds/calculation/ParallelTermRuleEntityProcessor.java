/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;


import gov.sec.idap.maxds.domain.TermRule;

import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.service.EntityService;
import gov.sec.idap.maxds.elasticsearch.service.NormalizedFactService;
import gov.sec.idap.maxds.elasticsearch.service.ProcessingLogService;
import gov.sec.idap.maxds.elasticsearch.service.TermResultService;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 *
 * @author srira
 */
public class ParallelTermRuleEntityProcessor implements Callable<Boolean> {

    private final TermRule termRule;
    private final Entity entity;
    private final TermRuleService myTermRuleService;
    private final EntityService myEntityService;
    private final TermResultService myTermResultService;
    private final NormalizedFactService myNormalizedFactService;
    private final ProcessingLogService myProcessingLogService;   
    

    
    
    public ParallelTermRuleEntityProcessor(TermRule termRule,
                                        Entity entity,
                                        TermRuleService myTermRuleService,
                                        EntityService myEntityService,
                                        TermResultService myTermResultService,
                                        NormalizedFactService myNormalizedFactService,
                                        ProcessingLogService myProcessingLogService) {
        this.termRule = termRule;
        this.entity = entity;
        this.myTermRuleService = myTermRuleService;
        this.myEntityService = myEntityService;
        this.myTermResultService = myTermResultService;
        this.myNormalizedFactService = myNormalizedFactService;
        this.myProcessingLogService = myProcessingLogService;

    }
    @Override
    public Boolean call() throws Exception {
//        EntityListTermProcessor etp = new 
//            EntityListTermProcessor(entity,
//                                new TermRuleInfoProvider(termRule,myTermRuleService ),
//                                myEntityService,
//                                myTermRuleService,
//                                myTermResultService,
//                                myNormalizedFactService,
//                                myProcessingLogService, 
//                                UUID.randomUUID().toString() , false
//            );        
        
        
        //return etp.process();
        //Thread.sleep(100);
        return true;
    }
    
}
