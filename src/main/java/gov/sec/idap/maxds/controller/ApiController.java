/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.controller;

import static gov.sec.idap.maxds.calculation.ExpressionParsingUtil.BuildResolvedTerm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import gov.sec.idap.maxds.api.vo.AccuracyTestDataVO;
import gov.sec.idap.maxds.api.vo.ApiAccuracyTestResponseVO;
import gov.sec.idap.maxds.api.vo.ApiResponseListVO;
import gov.sec.idap.maxds.api.vo.ApiResponseVO;
import gov.sec.idap.maxds.api.vo.ProcessingStatusVO;
import gov.sec.idap.maxds.api.vo.TermResultsDocListVO;
import gov.sec.idap.maxds.api.vo.TermResultsDocVO;
import gov.sec.idap.maxds.calculation.Domain.ConceptMatchWithDimResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ExtendedCheckResolvedTermExpression;
import gov.sec.idap.maxds.domain.AccuracyTestData;
import gov.sec.idap.maxds.domain.EntityTermProcessingVO;
import gov.sec.idap.maxds.domain.ExportAccuracyTestInput;
import gov.sec.idap.maxds.domain.ExportInput;
import gov.sec.idap.maxds.domain.ExportOutput;
import gov.sec.idap.maxds.domain.ExpressionFormula;
import gov.sec.idap.maxds.domain.ExtendedCheckResolverInput;
import gov.sec.idap.maxds.domain.ExtendedCheckResolverOutput;
import gov.sec.idap.maxds.domain.GraphProperties;
import gov.sec.idap.maxds.domain.GroupTermMapInformation;
import gov.sec.idap.maxds.domain.Login;
import gov.sec.idap.maxds.domain.PostRequestResult;
import gov.sec.idap.maxds.domain.ProcessingStatusCode;
import gov.sec.idap.maxds.domain.ProcessingTerm;
import gov.sec.idap.maxds.domain.TermCoverageExportOutput;
import gov.sec.idap.maxds.domain.TermExpression;
import gov.sec.idap.maxds.domain.TermExpressionTypeCode;
import gov.sec.idap.maxds.domain.TermMapGroup;
import gov.sec.idap.maxds.domain.TermRule;
import gov.sec.idap.maxds.domain.TermRuleHeader;
import gov.sec.idap.maxds.domain.TermRulePriorityGroup;
import gov.sec.idap.maxds.domain.TermRuleProcessing;
import gov.sec.idap.maxds.domain.TermRuleStatistics;
import gov.sec.idap.maxds.domain.TermRuleStatisticsInput;
import gov.sec.idap.maxds.domain.USGAAPTaxonomyElement;
import gov.sec.idap.maxds.model.Doc;
import gov.sec.idap.maxds.model.ReportDataVO;
import gov.sec.idap.maxds.model.Response;
import gov.sec.idap.maxds.model.TermNames;
import gov.sec.idap.maxds.security.CustomAuthenticationProvider;
import gov.sec.idap.maxds.security.MaxdsAuthentication;

import gov.sec.idap.maxds.service.AdminService;
import gov.sec.idap.maxds.service.CoverageService;

import gov.sec.idap.maxds.service.ExportService;

import gov.sec.idap.maxds.service.ProcessingService;
import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.document.ProcessingLogDoc;
import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;
import gov.sec.idap.maxds.elasticsearch.service.AccuracyTestService;
import gov.sec.idap.maxds.elasticsearch.service.EntityService;
import gov.sec.idap.maxds.elasticsearch.service.ProcessingLogService;
import gov.sec.idap.maxds.elasticsearch.service.ReferenceDataService;
import gov.sec.idap.maxds.elasticsearch.service.TermMapService;
import gov.sec.idap.maxds.elasticsearch.service.TermResultService;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;
import gov.sec.idap.maxds.services.PreferencesServices;

import gov.sec.idap.maxds.util.ProfileProcessUtils;
import gov.sec.idap.maxds.util.Util;
import gov.sec.idap.utils.web.security.SecAdUser;
import gov.sec.prototype.edm.domain.Preferences;
@CrossOrigin
@RestController
@RequestMapping("/api")
public class ApiController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Autowired
    Environment env;

    @Autowired
    ServletContext servletContext;

    @Autowired
    private EntityService entityService;

    @Autowired
    private TermRuleService termRuleService;

    @Autowired
    private ProcessingService processingService;

    @Autowired
    private TermResultService termResultService;

    @Autowired
    private ProcessingLogService processingLogService;

    @Autowired
    private ReferenceDataService lookupDataService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private CoverageService coverageService;

    @Autowired
    private TermMapService termMapService;

    @Autowired
    AdminService adminService;

    @Autowired
    private AccuracyTestService accuracyTestService;
    
    @Autowired
	private PreferencesServices preferencesServices;
    
    @Autowired
    private ProfileProcessUtils processUtils;
    
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;
    
    @Autowired
    private MaxdsAuthentication maxdsAuthentication;
    
    @Autowired
	private Util util;
   // private Util util = new Util();

    
    @CrossOrigin
	@RequestMapping(value = { "/TermRulesList" }, method = RequestMethod.GET)
    public 
    List<TermRuleHeader> getAllTermRulesNew() {
        LOG.debug("getAllTermRulesNew: ");

       /* Comments.
        * SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);*/
        List<TermRuleHeader> ret = termRuleService.findLimited();

        return ret;
    }
    

    @CrossOrigin
    @RequestMapping(value = "/termNamesListApi", method = RequestMethod.GET)
    public List<TermNames> getAllTermRuleList() {
        LOG.debug("getAllTermRulesNew: ");
        List<TermNames> ret = new ArrayList<TermNames>();
       /* SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);*/
        for(TermRuleHeader termRuleHeader:termRuleService.findLimited())
        {
        	TermNames names = new TermNames();
        	names.setName(termRuleHeader.getName()+"("+termRuleHeader.getTermId()+")");
        	names.setTermId(termRuleHeader.getTermId());
        	//String term = termRuleHeader.getName()+"("+termRuleHeader.getTermId()+")";
        	ret.add(names);
        }

        return ret;
    }
    
	/*
	 * @RequestMapping(value = "/fetchPreferences", method = RequestMethod.GET)
	 * public  List<Preferences> fetchPreferences() {
	 * LOG.debug("fetchPreferences: ");
	 * 
	 * SecurityContextHolder.getContext().getAuthentication();
	 * ServletRequestAttributes attr = (ServletRequestAttributes)
	 * RequestContextHolder.currentRequestAttributes(); HttpSession session =
	 * attr.getRequest().getSession(true); List<Preferences> ret =
	 * preferencesServices.fetchPreferences(SecurityContextHolder.getContext().
	 * getAuthentication().getName());
	 * 
	 * Iterable<ProcessingLogDoc> logs = getLatestProcessingLogs(500);
	 * for(ProcessingLogDoc log : logs) {
	 * if(log.logStatus.equalsIgnoreCase("InProgress")) { try {
	 * //Thread.sleep(60000); TimeUnit.SECONDS.sleep(1); } catch (Exception e) {
	 * e.printStackTrace(); } } } return ret; }
	 */

    
    
   /* @RequestMapping(value = "/fetchProcessedPreferences", method = RequestMethod.GET)
    public 
    List<ProcessedPreferences> fetchProcessedPreferences() {
        LOG.debug("Fetching Processed Preferences...");

        SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        List<ProcessedPreferences> ret = preferencesServices.fetchProcessedPreferences(SecurityContextHolder.getContext().getAuthentication().getName());

        return ret;
    }*/
    
	/*
	 * @RequestMapping(value = "/updateProcessDetails", method = RequestMethod.POST)
	 * public void updateProcessDetails(@RequestBody List<Preferences> preferences)
	 * { LOG.debug("updateProcessDetails: ");
	 * SecurityContextHolder.getContext().getAuthentication();
	 * ServletRequestAttributes attr = (ServletRequestAttributes)
	 * RequestContextHolder.currentRequestAttributes(); HttpSession session =
	 * attr.getRequest().getSession(true);
	 * preferencesServices.updateProcessDetails(preferences); }
	 */
    
    
    
    
    
    @CrossOrigin
	@RequestMapping("/TermRule")
    public 
    TermRule getTermRule(@RequestParam String id) {
        TermRule rule = termRuleService.getTermRuleByTermId(id);
        LOG.debug("getTermRule -getTermRuleByTermId: " + rule);

//        if( rule.getExpressions()!= null) {
//            for(TermExpression exp : rule.getExpressions()) {
//                LOG.debug(" getTermRule - expression: " + exp.rank + ", "+ exp.expression);
//            }
        for (TermExpression expr : rule.getExpressions()) {
            LOG.debug("Expression format - TYPE:" + expr.type + ",expression:" + expr.expression + ",axisExpression:" + expr.axisExpression + ",memberExpression:" + expr.memberExpression);

            switch (expr.type) {
                case ConceptMatchWithDim: {
                    ConceptMatchWithDimResolvedTermExpression cmd = (ConceptMatchWithDimResolvedTermExpression) BuildResolvedTerm(expr);

                    expr.conceptName = expr.expression;
                    break;
                }
                case ExtendedCheck: {
                    ExtendedCheckResolvedTermExpression ecte = (ExtendedCheckResolvedTermExpression) BuildResolvedTerm(expr);
                    expr.containsWords = ecte.containsWords;
                    expr.doesNotContainsWords = ecte.doesNotContainsWords;
                    expr.balType = "na";
                    if (ecte.balanceType.equals("D")) {
                        expr.balType = "debit";
                    }
                    if (ecte.balanceType.equals("C")) {
                        expr.balType = "credit";
                    }

                    expr.perType = ecte.periodType.equals("D") ? "duration" : "instant";

                    break;
                }
                case ExtendedCheckWithDim: {
                    TermExpressionTypeCode currentType = expr.type;
                    expr.type = TermExpressionTypeCode.ExtendedCheck;
                    ExtendedCheckResolvedTermExpression ecte = (ExtendedCheckResolvedTermExpression) BuildResolvedTerm(expr);
                    expr.containsWords = ecte.containsWords;
                    expr.doesNotContainsWords = ecte.doesNotContainsWords;
                    expr.balType = ecte.balanceType;
                    expr.perType = ecte.periodType;
                    expr.isShareItemType = ecte.isShareItemType;
                    expr.type = TermExpressionTypeCode.ConceptMatchWithDim;
                    // ConceptMatchWithDimResolvedTermExpression cmd = (ConceptMatchWithDimResolvedTermExpression) BuildResolvedTerm(expr);
                    // LOG.debug("axis: " + cmd.NamedAxisList);
                    // expr.NamedAxisList = cmd.NamedAxisList;
                    // expr.NamedMemberList = cmd.NamedMemberList;                           
                    expr.type = currentType;
                    break;
                }
                case Formula: {
                    String parts[] = expr.expression.split("[{]");
                    List<ExpressionFormula> formulaList = new ArrayList();
                    for (String part : parts) {
                        if (part.isEmpty()) {
                            continue;
                        }

                        String fields[] = part.split("[}]");
                        if (fields.length == 0) {
                            continue;
                        }
                        String term = fields[0];
                        term = term.replace("sec:", "");
                        boolean nullable = false;
                        String operation = "";
                        if (term.endsWith("=0")) {
                            nullable = true;
                            term = term.replace("=0", "");
                        }
                        if (fields.length == 2) {
                            operation = fields[1];
                        }
                        ExpressionFormula formula = new ExpressionFormula();
                        formula.setTermName(term.trim());
                        formula.setNullable(nullable);
                        formula.setOperation(operation.trim());
                        formulaList.add(formula);
                    }
                    expr.setFormulaList(formulaList);
                    String formula = "";
                    for(ExpressionFormula expressionFormula:formulaList)
                    {
                    	formula = formula+expressionFormula.getTermName()+" "+expressionFormula.getOperation();
                    }
                    
                    LOG.info("DERIVATION TRAIL: "+formula);
                   
                    break;
                }
                case ConceptMatch: {
                    expr.conceptName = expr.expression;
                    break;
                }
            }
        }

        return rule;
    }

    @CrossOrigin
    @RequestMapping(value = {"/TermRule/save"}, method = RequestMethod.PUT)
    public Object save(@RequestBody TermRule termRule) {
    	LOG.debug("Saving term rule : " + termRule.getName());
       return termRuleService.save(termRule, true);
    	
    }
    
    
   /* @RequestMapping(value = "/saveProcessedPreferences",
            method = RequestMethod.POST,
            produces = {"application/json"}, consumes = {"application/json"})
    public 
    PostRequestResult saveProcessedPreferences(@RequestBody List<ProcessedPreferences> saveInfo) {
        return preferencesServices.saveProcessedPreferences(saveInfo);

    }*/

    
	/*
	 * @RequestMapping(value = "/savePreferences/{quaterly}", method =
	 * RequestMethod.POST) public  PostRequestResult save(@RequestBody
	 * Preferences saveInfo,@PathVariable String quaterly) {
	 * saveInfo.setQuaterly(quaterly); LOG.debug("Saving Preferences : " + saveInfo
	 * +" quaterly :  "+quaterly); return
	 * preferencesServices.savePreferences(saveInfo); }
	 */
    
   /* @RequestMapping(value = "/TermRule/ProcessRule/{termRuleId}",
            method = RequestMethod.POST,
            produces = {"application/json"}, consumes = {"application/json"})
    public 
    PostRequestResult ProcessRule(@PathVariable String termRuleId) {
        LOG.debug("ProcessRule: " + termRuleId);
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return processingService.processTermRule(termRuleId, 0, null, name);
    }*/
    @RequestMapping(value = "/TermRule/ProcessRule", method = RequestMethod.PUT)
    public Object ProcessRule(@RequestBody TermRule termRule) {
        LOG.debug("ProcessRule: " + termRule.getId());
        String name = termRule.getUserid();//servletContext.getAttribute("username").toString();this.username;//SecurityContextHolder.getContext().getAuthentication().getName();
        return processingService.processTermRule(termRule.getId(), 0, null, name);
    }
    
    @CrossOrigin
    @RequestMapping(value = "/TermRule/ProcessRuleWithCriteria", method = RequestMethod.PUT)
    public Object ProcessRuleWithCriteria(@RequestBody ProcessingTerm pt) {
        LOG.debug("ProcessRuleWithCriteria: " + pt.getTermId() + pt.getDivision() + pt.getSector() + pt.getSic() + pt.getFilerCategory());
        String name = pt.getUserid();//servletContext.getAttribute("username").toString();//this.username;//SecurityContextHolder.getContext().getAuthentication().getName();
        return processingService.ProcessRuleWithCriteria(pt.getTermId(), 0, null,
        		pt.getDivision(), pt.getSector(), pt.getSic(), pt.getFilerCategory(), name, "", UUID.randomUUID().toString());
    }

   
    @CrossOrigin
    @RequestMapping(value = "/TermRule/ProcessAllRulesWithCriteria", method = RequestMethod.PUT)
    public Object ProcessAllRulesWithCriteria(@RequestBody ProcessingTerm pt) {
        LOG.debug("ProcessAllRulesWithCriteria: " + pt.getDivision() + pt.getSector() + pt.getSic() + pt.getFilerCategory() + pt.getEntityId());
        if (pt.getEntityId() != null && pt.getEntityId().equals("NULL")) {
        	pt.setEntityId(null);
        }

        ProcessingLogDoc doc = processingLogService.getNextTermToProcess();
        String name = pt.getUserid();//servletContext.getAttribute("username").toString();//this.username;//SecurityContextHolder.getContext().getAuthentication().getName();
        List<TermRule> trs = termRuleService.getOrderedTermRuleList();
        if (pt.getIsNewAction()) {

            if (doc != null) {
                //we have something already scheduled to process...
                String msg = String.format("Currently there are existing term rules that are scheduled for processing. Cannot start another set until it is finished.");
                return msg;
            }

            LOG.debug("ProcessAllRulesWithCriteria Term Rule count being processed" + trs.size());
            for (TermRule tr : trs) {
                if (tr.getProcessingStatus() == ProcessingStatusCode.InProgress) {
                    String msg = String.format("Term Rule: %s is being processed. Please wait for it to complete.", tr.getName());
                    return msg;
                }

            }

            Collections.sort(trs, new TermRule());
            this.processingLogService.ScheduleTermsForProcessing(trs);
        } else {
            if (doc == null) {
                //we have something already scheduled to process...
                String msg = String.format("Currently there is nothing scheduled to be processed.");
                return msg;
            }
            //check if there are any partially completed term that is still being processed...
            ProcessingLogDoc inProgressDoc = processingLogService.getItemCurrentlyBeingProcessed();

            if (inProgressDoc != null) {
                //check if it was active within the last 30 minutes...
                long diffInMillies = Math.abs((new Date()).getTime() - inProgressDoc.getLastModified().getTime());
                if (diffInMillies < 1000000) {
                    //too soon to continue processing....
                    String msg = String.format("It is less than 20 minutes since the last stalled processing, please wait at least 20 minutes before restarting the stalled processing.");
                    return msg;

                }
                while (inProgressDoc != null) {
                    TermRule tr = this.termRuleService.getTermRuleByTermId(inProgressDoc.getTermId());
                    LOG.debug("Processing Rule : " + tr.getTermId());
                    try {
                       PostRequestResult  result =  processingService.ProcessRuleWithCriteria(tr.getTermId(), tr.getOrder(),
                    		   pt.getEntityId(),
                    		   pt.getDivision(),
                    		   pt.getSector(), pt.getSic(),
                    		   pt.getFilerCategory(), name,
                                String.format("Processing Term %d of %d.", inProgressDoc.getProcessingSequence(), trs.size()),
                                inProgressDoc.getProcessingGroupId());
                       
                        if( result.status !=  true){
                            return result;
                        }
                    } catch (Exception exp) {
                        //do nothing...
                    }
                   
                    inProgressDoc = processingLogService.getItemCurrentlyBeingProcessed();
                }
            }

        }

        ProcessingLogDoc nextDoc = processingLogService.getNextTermToProcess();
        if (nextDoc == null) {
            //we have something already scheduled to process...
            String msg = String.format("Currently there is nothing scheduled to be processed.");
            return msg;
        }
        
        while (nextDoc != null) {

            TermRule tr = this.termRuleService.getTermRuleByTermId(nextDoc.getTermId());
            String termRuleId = nextDoc.getTermId();
            LOG.debug("Processing Rule : " + tr.getTermId());
            try {
                PostRequestResult  result = processingService.ProcessRuleWithCriteria(tr.getTermId(), tr.getOrder(),
                		pt.getEntityId(),
                		pt.getDivision(),
                		pt.getSector(), pt.getSic(),
                		pt.getFilerCategory(), name,
                        String.format("Processing Term %d of %d.", nextDoc.getProcessingSequence(), trs.size()),
                        nextDoc.getProcessingGroupId());
                if( result.status !=  true){
                    return result;
                }
            } catch (Exception exp) {
                //do nothing...
            }

            nextDoc = processingLogService.getNextTermToProcess();
            
            if( nextDoc != null && nextDoc.getTermId().equals(tr.getTermId())){
                
                 String msg = String.format("Processing is not updating status of Term rules. Please cancel term processing and then kick off a new process.");
                 return msg;
            }
        }

        return PostRequestResult.GetSuccessResult();
    }

   
    @CrossOrigin
    @RequestMapping(value = "/TermRule/CancelPendingProcessing", method = RequestMethod.PUT)
    public 
    PostRequestResult CancelPendingProcessing(@RequestBody TermRule termRule) {

        processingLogService.cancelPendingProcessingItems(termRule.getUserid());

        return PostRequestResult.GetSuccessResult();
    }

	/*
	 * @RequestMapping(value =
	 * "/TermRule/ProcessRuleForEntity/{termRuleId}/{entityId}", method =
	 * RequestMethod.POST, produces = {"application/json"}, consumes =
	 * {"application/json"}) public PostRequestResult ProcessRuleForEntity(
	 * 
	 * @PathVariable("termRuleId") String termRuleId, @PathVariable("entityId")
	 * String entityId) { LOG.debug("ProcessRuleForEntity: " + termRuleId +
	 * ", entity=" + entityId); String name =
	 * SecurityContextHolder.getContext().getAuthentication().getName(); return
	 * processingService.processTermRule(termRuleId, 0, entityId, name); }
	 */
    
    
	/*
	 * @RequestMapping(value = "/TermRule/ProcessRuleForEntity", method =
	 * RequestMethod.POST, produces = {"application/json"}, consumes =
	 * {"application/json"})
	 */
    @CrossOrigin
    @RequestMapping(value = {"/TermRule/ProcessRuleForEntity"}, method = RequestMethod.PUT)
    public Object ProcessRuleForEntity(@RequestBody TermRuleProcessing trp) {
        LOG.debug("ProcessRuleForEntity: " + trp.getTermId() + ", entity=" + trp.getEntityId());
        String name = trp.getUserid(); //servletContext.getAttribute("username").toString();//this.username;//SecurityContextHolder.getContext().getAuthentication().getName();
        
        PostRequestResult postRequestResult = processingService.processTermRule(trp.getTermId(), 0, trp.getEntityId(), name);
        if(trp.getProcessId() != null && trp.getProcessId() != null) {
        	List<Preferences> inProgressList = processUtils.updateInProgress(trp.getProcessId());
            preferencesServices.updateProcessDetails(inProgressList);
        	List<Preferences> completedList = processUtils.updateComplete(trp.getProcessId(),trp.getEntityId());
            preferencesServices.updateProcessDetails(completedList);
        }
        postRequestResult.resultObject = "completed";
        return postRequestResult;
    	//return trp;
    }
    
    @CrossOrigin
    @RequestMapping(value = {"/TermRule/ProcessRuleForEntity"}, method = RequestMethod.GET, produces = {"application/json"})
    public List<ApiResponseVO> apiProcessRuleForEntity(
    //public ApiResponseListVO apiProcessRuleForEntity(
    		@RequestParam String termIds, 
    		@RequestParam String ciks,
    		@RequestParam String userId) {
    	
    	String[] termIdArray = termIds.split(",");
        List<String> termIdList = apiCleanTermidList(Arrays.asList(termIdArray));
        String[] cikArray = ciks.split(",");
        List<String> cikList = apiCleanCikList(Arrays.asList(cikArray));
        
        List<ApiResponseVO> res = new ArrayList<ApiResponseVO>();
        
        for(String termId: termIdList) {
        	if (termId != null && termId.length() > 0) {
        		termId = termId.toUpperCase();
            	for(String cik: cikList) {
            		LOG.debug("ProcessRuleForEntity from API call: termId=" + termId + ", entity=" + cik + ", userId=" + userId);
            		res.add(apiTermEntityProcess(termId, cik, userId));
            	}
        	}
        }        
    	return res;
    }
    
    private ApiResponseVO apiTermEntityProcess(String termId, String cik, String userId) {
        ApiResponseVO response = new ApiResponseVO();
        response.setTermId(termId);
        response.setCik(Long.parseLong(cik));
        
        String processId = UUID.randomUUID().toString();
        String profileId = getProfileId();
        String url = "https://sp-us-deraodp01.ix.sec.gov:18081/api/";
        if (profileId != null && profileId.equalsIgnoreCase("prod")) {
        	url = "https://md-up-ldridap.ix.sec.gov:18081/api/";
        }
        String statusUrl = url + "ProcessStatus/" + processId;
        String resultUrl = url + "ProcessResults?termRuleId=" + termId + "&cik=" + cik;
        
        processingService.apiProcessTermRule(termId, 0, cik, userId, processId);
        response.setId(processId);
        response.setResultLink(resultUrl);
        response.setStatusLink(statusUrl); 
        return response;
    }
    
    private	List<String> apiRemoveDuplicates(List<String> list) {
    	Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(list);
        return new ArrayList<String>(set);
    }
    
    private List<String> apiCleanTermidList(List<String> list) {
    	ArrayList<String> termidList = new ArrayList<String>();
    	if (list != null ) {
    		for (String termid: list) {
    			if (!StringUtils.isEmpty(termid)) {
    				termidList.add(termid.trim());
    			}
    		}
    	}
    	return apiRemoveDuplicates(termidList);
    }
    
    private List<String> apiCleanCikList(List<String> list) {
    	ArrayList<String> cikList = new ArrayList<String>();
    	if (list != null ) {
    		for (String cik: list) {
    			if (cik != null && cik.trim().length()>0 && cik.trim().length()<=10 ) {
    				cik = cik.trim();
    				cik = ("0000000000" + cik).substring(cik.length());
        			cikList.add(cik);
    			}
    		}
    	}
    	return apiRemoveDuplicates(cikList);
    }
    
    @RequestMapping(value = "/ProcessResults", method = RequestMethod.GET, produces = {"application/json"})
    public List<TermResultsDocVO>  apiGetAllTermResultsByTermRuleAndEntity(@RequestParam String termRuleId, @RequestParam String cik) {
    	List<TermResultsDoc> ret = new ArrayList<TermResultsDoc>();
    	List<TermResultsDocVO> retVO = new ArrayList<TermResultsDocVO>();
    	
    	if (termRuleId != null && cik != null ) {
    		termRuleId = termRuleId.trim().toUpperCase();
    		cik = cik.trim();
    		if (cik.length() > 0 && cik.length() <= 10) {
    			cik = ("0000000000" + cik).substring(cik.length());
        		
        		LOG.debug("apiGetAllTermResultsByTermRuleAndEntity: rule=" + termRuleId + ", entity=" + cik);
                ret = termResultService.findBaseInfoByTermIdAndEntity(termRuleId, cik);
                ret = processUtils.generateExperssionTrails(ret);
                LOG.debug("Got results apiGetAllTermResultsByTermRuleAndEntity: rule=" + termRuleId + ", count=" + ret.size());
                Collections.sort(ret, new TermResultsDoc());
                Collections.reverse(ret);
                LOG.debug("sorted and returning data");
                                
                for (TermResultsDoc termResultsDoc : ret) {
                	retVO.add(termResultsDoc.createVO());
                }

                LOG.debug("sorted and returning data");
    		}	
    	}
    	return retVO;
    }
    
    @RequestMapping(value = "/ProcessStatus/{groupId}", method = RequestMethod.GET, produces = {"application/json"})
    public List<ProcessingStatusVO> apiGetProcessStatusByGroupId(@PathVariable("groupId") String groupId) {
        LOG.info("apiGetProcessStatusByGroupId: " + groupId);
        String result = null;
        List<ProcessingLogDoc> ret = processingLogService.findByProcessingGroupId(groupId);
        List<ProcessingStatusVO> processStatusList = new ArrayList<ProcessingStatusVO>();
        
        if(ret.size()>0) {
	        Gson gson = new Gson();
	        for (ProcessingLogDoc doc : ret) {
	        	//String jsonString = gson.toJson(doc);
	        	//ProcessingStatusVO jsonObj = gson.fromJson(jsonString, ProcessingStatusVO.class);
	        	processStatusList.add(doc.createVO());
	        }
        } else {
        	ProcessingStatusVO vo = new ProcessingStatusVO();
        	vo.description = "Process id " + groupId + " doesn't exist.";
        	vo.logStatus = "Unknown";
        	processStatusList.add(vo);
        }
        //Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    	//result = gson.toJson(processStatusList.get(0));
        return processStatusList;
    }
    
    //This is API reflects function on Accuracy Test page on Maxds UI
    @RequestMapping(value = "/AccuracyTest", method = RequestMethod.GET, produces = {"application/json"})
    public AccuracyTestDataVO apiAccuracyTestForEntityAndYear(@RequestParam String cik, @RequestParam int fiscalYear) {
    	LOG.debug("API performAccuracyTest CIK : " + cik);
        LOG.debug("API performAccuracyTest fiscal year : " + fiscalYear);
        AccuracyTestData atd = new AccuracyTestData(); 
    	if (cik != null ) {
    		cik = cik.trim();
    		if (cik.length() > 0 && cik.length() <= 10) {
    			cik = ("0000000000" + cik).substring(cik.length());    			
    	        try {
    	            atd = accuracyTestService.runAccuracyTest(cik, fiscalYear);
    	        } catch (Exception ex) {
    	            LOG.error("Exception in running accuracy Test. Exception Message :" + ex.getMessage());
    	            atd.status = false;
    	            atd.errorDescription = "Exception in running accuracy Test. Exception Message :" + ex.getMessage();
    	        }
    		}
    	}  
    	AccuracyTestDataVO vo = atd.createVO();
        return vo;
    }
    
    //This is API reflects function on Process Term page on Maxds UI. Clicking on Accuracy Test button.
    @RequestMapping(value = "/ProcessAccuracyTest", method = RequestMethod.GET, produces = {"application/json"})
    public ApiAccuracyTestResponseVO apiProcessAccuracyTest(@RequestParam String userId) {
    	LOG.debug("API performAccuracyTest by user: " + userId);
    	String url = getApiBaseUrl();
        
        ApiAccuracyTestResponseVO vo = new ApiAccuracyTestResponseVO();
        vo.setJobName("Accuracy Test");
        vo.setUserId(userId);
        
    	List<TermRule> trs = this.termRuleService.getTermRulesForAccuracyTesting();
    	Map<String, Integer> ctMap = apiGetRunningProcessCount();
    	int scheduledCt = ctMap.get("Scheduled").intValue();
    	int inProgressCt = ctMap.get("InProgress").intValue();
    	
    	if ( (scheduledCt+inProgressCt) > 0) {
    		vo.setDescription("Currently there are " + inProgressCt + " running term process and " + scheduledCt + " term process scheduled. You can only start Accuracy Test after they are completed or canceled." );
    	} else {
    		Date start = new Date();
            String statusUrl = url + "ProcessAccuracyTestStatus?startTimeStamp=" + start.getTime();
            vo.setStartTime((new Date()).toString());
            vo.setStatusLink(statusUrl);
    		//thread out process ...
    		processingService.apiProcessAccuracyTest(trs, userId, trs.size());
    	}
    
    	return vo;
    }
    
    @RequestMapping(value = "/ProcessEntityAccuracyTest", method = RequestMethod.GET, produces = {"application/json"})
    public ApiAccuracyTestResponseVO apiProcessAccuracyTestForEntities(@RequestParam String cik, @RequestParam boolean goToPeer, @RequestParam String userId) {
    	LOG.debug("API apiProcessAccuracyTestForEntities by user: " + userId + ", for entity: " + cik + ", goToPeer: " + goToPeer );
    	
    	
    	String url = getApiBaseUrl();
        
        ApiAccuracyTestResponseVO vo = new ApiAccuracyTestResponseVO();
        vo.setJobName("Accuracy Test");
        vo.setUserId(userId);
        
        if (cik != null ) {
    		cik = cik.trim();
    		if (cik.length() > 0 && cik.length() <= 10) {
    			cik = ("0000000000" + cik).substring(cik.length());    			
    		} else {
    			vo.setDescription("Invalid CIK: " + cik);
    		}
    	} else {
    		vo.setDescription("Invalid CIK: " + cik);
    	}

        //get all included term id
    	List<TermRule> trs = this.termRuleService.getTermRulesForAccuracyTesting(); 
    	//find out how many current running/scheduled process
    	Map<String, Integer> ctMap = apiGetRunningProcessCount();
    	int scheduledCt = ctMap.get("Scheduled").intValue();
    	int inProgressCt = ctMap.get("InProgress").intValue();
    	
    	if ( (scheduledCt+inProgressCt) > 0) {
    		vo.setDescription("Currently there are " + inProgressCt + " running term process and " + scheduledCt + " term process scheduled. You can only start Accuracy Test after they are completed or canceled." );
    	} else {
    		Date start = new Date();
            String statusUrl = url + "ProcessAccuracyTestStatus?startTimeStamp=" + start.getTime();
            vo.setStartTime((new Date()).toString());
            vo.setStatusLink(statusUrl);
        
    		//thread out process ...
    		processingService.apiProcessAccuracyTestForEntities(trs, cik, goToPeer, userId, trs.size());
    	}
    
    	return vo;
    }
    
    private Map<String, Integer> apiGetRunningProcessCount() {
        LOG.info("apiGetRunningProcessCount ...");
        int limit = 100;
        int scheduledCt = 0;
        int inProgressCt = 0;
        Map<String, Integer> result = new HashMap<String, Integer>();
        
        Iterable<ProcessingLogDoc> re = processingLogService.getLatestProcessingLogs(limit);
        for (ProcessingLogDoc doc: re) {
        	if (doc.getLogStatus() != null && doc.getLogStatus().equalsIgnoreCase("Scheduled")) {
        		scheduledCt++;
        	} else if (doc.getLogStatus().equalsIgnoreCase("InProgress")) {
        		inProgressCt++;
        	}
        }
        result.put("Scheduled", new Integer(scheduledCt));
        result.put("InProgress", new Integer(inProgressCt));
       return result;
    }
    
    @RequestMapping(value = "/ProcessAccuracyTestStatus",
            method = RequestMethod.GET, produces = {"application/json"})
    public List<ProcessingLogDoc> apiGetLatestProcessingLogs(@RequestParam long startTimeStamp) {
        LOG.info("apiGetLatestProcessingLogs with start timestamp: " + startTimeStamp);
        int limit = 1000;
        Date start = new Date(startTimeStamp);
        
        Iterable<ProcessingLogDoc> re = processingLogService.getLatestProcessingLogs(limit);
        List<ProcessingLogDoc> result = new ArrayList<ProcessingLogDoc>();
        for (ProcessingLogDoc doc: re) {
        	if (doc.getLastModified().after(start)) {
        		result.add(doc);
        	}
        }
       return result;
    }
    
    private String getApiBaseUrl() {
    	String profileId = getProfileId();
        String url = "https://sp-us-deraodp01.ix.sec.gov:18081/api/";
        if (profileId != null && profileId.equalsIgnoreCase("prod")) {
        	url = "https://md-up-ldridap.ix.sec.gov:18081/api/";
        }
        return url;
    }
    
    private String getProfileId() {
		String profileId = null;
		try {
			util.getJdbcTemplate();
			profileId = util.getMaxdsProfileId();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return profileId;
    }

   /* @RequestMapping(value = "/TermRule/DeleteRule/{termRuleId}",
            method = RequestMethod.POST,
            produces = {"application/json"}, consumes = {"application/json"})
    public 
    PostRequestResult DeleteRule(@PathVariable String termRuleId) {
        LOG.debug("Deleting TermRule: " + termRuleId);
        return processingService.deleteTermRule(termRuleId);
    }
*/
    @CrossOrigin
    @RequestMapping(value = "/TermRule/DeleteRule", method = RequestMethod.PUT)
    public Object DeleteRule(@RequestBody TermRule termRule) {
        LOG.debug("Deleting TermRule: " + termRule.getId());
        //return termRule;
       return processingService.deleteTermRule(termRule.getId());
    }
    
    
    @RequestMapping(value = "/TermRulesStatistics/{termRuleId}/{minYear}/{maxYear}/{includeQuarterly}",
            method = RequestMethod.GET)
    public 
    TermRuleStatistics getTermRuleStatistics(@PathVariable("termRuleId") String termRuleId,
            @PathVariable("minYear") int minYear,
            @PathVariable("maxYear") int maxYear,
            @PathVariable("includeQuarterly") Boolean includeQuarterly) {
        LOG.debug("getTermRuleStatistics: " + termRuleId);

        return termResultService.getTermRuleStatisticsByRank(termRuleId,
                minYear, maxYear, includeQuarterly, null, null, null, null, null);

    }
    
    

     @CrossOrigin
     @RequestMapping(value = {"/TermRulesStatisticsForCriteria"}, method = RequestMethod.PUT)
    public 
    TermRuleStatistics postTermRuleStatisticsforCriteria(@RequestBody TermRuleStatisticsInput input) {
        LOG.error("getMappedEntitiesForTermRuleForRank: rule=" + input.termRuleId + ", EntityId=" + input.entityId);
        LOG.error("getMappedEntitiesForTermRuleForRank: division=" + input.division + ", filerCategory=" + input.filerCategory);
        LOG.error("getMappedEntitiesForTermRuleForRank: sector=" + input.sector + ", sic=" + input.sic);
        return termResultService.getTermRuleStatisticsByRank(input.termRuleId,
                input.minYear, input.maxYear, input.includeQuarterly, input.division, 
                input.sector, input.sic, input.filerCategory, input.entityId);

    }
    
     @RequestMapping(value = "/getReportData",method = RequestMethod.GET)
     public List<ReportDataVO> getAdminData()
     {
    	 
    	 List<ReportDataVO> retVal = new ArrayList<ReportDataVO>();
		 ReportDataVO rVo = new ReportDataVO();
    	 String solrHost = env.getProperty("spring.data.solr.host","https://sp-us-deraodp01.ix.sec.gov:8983/solr");
    	 String profileID =env.getProperty("maxds.profileId") ;//"ci";
    	 System.out.println("***********"+profileID);
    	 rVo.setProfileID(profileID);
    	 rVo.setCollectionName("MaxDS_AccuracyTestNotes");
    	 rVo.setCollectionType("Input Collection");
    	 rVo.setCollectionURL(solrHost+"/MaxDS_AccuracyTestNotes/select?q=*%3A*");
    	 retVal.add(rVo);
	 
    	 ReportDataVO rVo1 = new ReportDataVO();
    	 rVo1.setProfileID(profileID);
    	 rVo1.setCollectionName("MaxDS_LookupReferences");
    	 rVo1.setCollectionType("Input Collection");
    	 rVo1.setCollectionURL(solrHost+"/MaxDS_LookupReferences/select?q=*%3A*");
    	 retVal.add(rVo1);
    	 
    	 ReportDataVO rVo2 = new ReportDataVO();
    	 rVo2.setProfileID(profileID);
    	 rVo2.setCollectionName("MaxDS_ProcessingLogs");
    	 rVo2.setCollectionType("Output Collection");
    	 rVo2.setCollectionURL(solrHost+"/MaxDS_ProcessingLogs/select?q=*%3A*");
    	 retVal.add(rVo2);
    	 
    	 ReportDataVO rVo3 = new ReportDataVO();
    	 rVo3.setProfileID(profileID);
    	 rVo3.setCollectionName("MaxDS_TermMapInformation");
    	 rVo3.setCollectionType("Input Collection");
    	 rVo3.setCollectionURL(solrHost+"/MaxDS_TermMapInformation/select?q=*%3A*");
    	 retVal.add(rVo3);
    	 
    	 ReportDataVO rVo4 = new ReportDataVO();
    	 rVo4.setProfileID(profileID);
    	 rVo4.setCollectionName("MaxDS_TermResults");
    	 rVo4.setCollectionType("Output Collection");
    	 rVo4.setCollectionURL(solrHost+"/MaxDS_TermResults/select?q=*%3A*");
    	 retVal.add(rVo4);
    	 
    	 ReportDataVO rVo5 = new ReportDataVO();
    	 rVo5.setProfileID(profileID);
    	 rVo5.setCollectionName("MaxDS_TermRules");
    	 rVo5.setCollectionType("Input Collection");
    	 rVo5.setCollectionURL(solrHost+"/MaxDS_TermRules/select?q=*%3A*");
    	 retVal.add(rVo5);
    	 
    	 return retVal; 
    	 
     }
     @RequestMapping(value = "/getFilingUrl/{cik}/{FY}",method = RequestMethod.GET)
     public String getFilingUrl(@PathVariable("cik") String cik,@PathVariable("FY") String FY)
     {
    	 String solrHost = env.getProperty("spring.data.solr.host","https://sp-us-deraodp01.ix.sec.gov:8983/solr"); 
    	 StringBuilder sb = new StringBuilder();
    	 String line ="";
    	 String retVal = "https://www.sec.gov/Archives/edgar/data/";//0000320193/0000320193-17-000070-index.htm
    	 String urlString = solrHost+"/MaxDS_TermResults/select?fl=termId,termName,entityId,FY,FQ,termId,value,accession_s&q=entityId:"+cik+
    			 "%20AND%20FY:"+FY+"%20AND%20FQ:\"FY\"";
    	 LOG.info(urlString);
    	 System.out.println(urlString);
    	 try 
    	 {
			URL url = new URL(urlString);
			InputStream is = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while((line = br.readLine())!=null)
			{
				sb.append(line);
			}
			Gson gson = new Gson();
			Response response = gson.fromJson(sb.toString(), Response.class);
			if(response.getResponse().getDocs().size()>0)
			{
				Doc doc = response.getResponse().getDocs().get(1);
				retVal = retVal +doc.getEntityId()+File.separator+doc.getAccessionS()+"-index.htm";
				System.out.println(retVal);
			}
			
    	 } 
    	 catch (IOException e) 
    	 {
			e.printStackTrace();
		}
    	 return retVal;
     }
     @RequestMapping(value = "/getFilingUrls/{cik}/{FY}/{term}",method = RequestMethod.GET)
     public String getFilingUrls(@PathVariable("cik") String cik,@PathVariable("FY") String FY,@PathVariable("term") String term)
     {
    	 String solrHost = env.getProperty("spring.data.solr.host","https://sp-us-deraodp01.ix.sec.gov:8983/solr"); 
    	 StringBuilder sb = new StringBuilder();
    	 String urlString = solrHost+"/MaxDS_TermResults/select?q=entityId%3A"+cik+"%20AND%20FY%3A"+FY+"%20%20AND%20termId%3A"+term+"%20AND%20FQ%3AFY";
    	 LOG.info(urlString);
    	 
    	 return urlString;
     }
    @RequestMapping(value = "/isValidUser",method = RequestMethod.POST)
    public PostRequestResult isValidUser(@RequestBody Login login)
    {
    	
    		servletContext.setAttribute("username", login.getUsername());
    		boolean isValid = customAuthenticationProvider.isValidInLDAP(login.getUsername(), login.getPassword());
    		if(isValid) {
    			SecAdUser user = maxdsAuthentication.authenticateUser(login.getUsername(), login.getPassword());
    			return PostRequestResult.GetSuccessResultWithReturnObject(user);
    		}else {
    			return PostRequestResult.GetErrorResult("Invalid Login Details");
    		}
    	
    }
    
    @RequestMapping(value = "/autoLogin",method = RequestMethod.POST)
    public PostRequestResult autoLogin(HttpServletResponse response,
            HttpServletRequest request, @RequestBody Login login) throws UnknownHostException {
    	
    
    	HttpSession session =  request.getSession(true);
    	String ipAddress = request.getHeader("X-FORWARDED-FOR");  
		if (ipAddress == null) {  
		    ipAddress = request.getRemoteAddr();
		}
		 String clintHost=ipAddress;
		try {
			clintHost = InetAddress.getByName(ipAddress).getHostName();
		} catch (UnknownHostException e) {

		}
		ipAddress = ipAddress+"("+clintHost+")";
		 if(clintHost.contains(".")) {
		    	clintHost =  clintHost.substring(0,clintHost.indexOf('.'));
		    }
		String idapUser = env.getProperty("idap.ad.user", "None");  // "IDAPDEV.service";
	    String idapPwd = env.getProperty("idap.ad.pwd", "None"); 

	    SecAdUser sc = maxdsAuthentication.isExistUserIdByComputerName(idapUser, idapPwd, clintHost.toUpperCase());
	    return PostRequestResult.GetSuccessResultWithReturnObject(sc);
    }
    
    
    @RequestMapping(value = "/getFullUserName/{userName}/{password}",method = RequestMethod.GET)
    public SecAdUser getFullUserName(@PathVariable("userName") String userName,@PathVariable("password") String password)
    {
    	return maxdsAuthentication.authenticateUser(userName, password);
	}
    

    @RequestMapping(value = "/UserRolesList/{userid}", method = RequestMethod.GET)
    public 
    List<String> getCurrentUserRoles(final HttpServletRequest request,@PathVariable String userid) {

        ArrayList<String> ret = new ArrayList();

        ret.add("user");
        
        String name = userid;//servletContext.getAttribute("username").toString();//this.username;//SecurityContextHolder.getContext().getAuthentication().getName();
        String userAuth = env.getProperty("user.auth", "None");

        if (userAuth.contains("None")) {
            String adminUsers = env.getProperty("admin.list", "admin");
            List<String> adminList = Arrays.asList(adminUsers.split(","));

            if (adminList.contains(name)) {

                ret.add("admin");
            }
        } else {

            List<String> adminList = new ArrayList<String>();
            List<String> userList = new ArrayList<String>();
            try {
                adminList = util.getResultSet(true);
                userList = util.getResultSet(false);
            } catch (IOException e) {
                LOG.warn("Database Connection Failed: " + e.getMessage());
                e.printStackTrace();
            }

            if (adminList != null && !adminList.isEmpty() && adminList.contains(name)) {

                ret.add("admin");
            } if(userList != null && !userList.isEmpty() && userList.contains(name)) {
            	ret.add("user");
            }

        }

        return ret;

//        String adminUsers = env.getProperty("admin.list", "admin");
//        List<String> adminList = Arrays.asList(adminUsers.split(","));
//        String name = SecurityContextHolder.getContext().getAuthentication().getName();
//        if (adminList.contains(name)) {
//
//            ret.add("admin");
//        }
//        return ret;
    }

    @RequestMapping(value = "/UserDisplayName", produces = "application/json", method = RequestMethod.GET)
    public 
    String getCurrentUserDisplayName() {

        LOG.debug("API getCurrentUserDisplayName....");
        String name = servletContext.getAttribute("username").toString();//this.username;//SecurityContextHolder.getContext().getAuthentication().getName();
        LOG.debug("API getCurrentUserDisplayName Name  : " + name);
        return "{\"name\":\"" + name + "\"}";

    }
    
    @CrossOrigin
    @RequestMapping(value = "/EntityList", method = RequestMethod.GET)
    public 
    List<Entity> getEntities() {
        LOG.debug("getEntities: ");

        LOG.debug("Loading entities from Database....");
        List<Entity> entityList = entityService.findLimitedSlow();

        return entityList;

    }

  
    
    @CrossOrigin
    @RequestMapping(value = {"/MappedEntitiesForCriteria"}, method = RequestMethod.PUT)
    public 
    List<Entity> postMappedEntitiesForTermRuleForCriteria(@RequestBody TermRuleStatisticsInput input) {
    	
    	System.out.println("getMappedEntitiesForTermRuleForRank: rule=" + input.termRuleId + ", EntityId=" + input.entityId);
    	System.out.println("getMappedEntitiesForTermRuleForRank: division=" + input.division + ", filerCategory=" + input.filerCategory);
    	System.out.println("getMappedEntitiesForTermRuleForRank: sector=" + input.sector + ", sic=" + input.sic);
    	
    	/**termRuleId: termName[INTAN,SALE etc...]
    	 * rankId: 
    	 * minYear:
    	 * maxYear:
    	 * includeQuarterly: should be a checkbox , if checked true , else false
    	 * division: division before arrow
    	 * sector: sector after arrow
    	 * sic: numbercode before -
    	 * filerCategory: filercategory
    	 * entity id : NULL*/
        LOG.error("getMappedEntitiesForTermRuleForRank: rule=" + input.termRuleId + ", EntityId=" + input.entityId);
        LOG.error("getMappedEntitiesForTermRuleForRank: division=" + input.division + ", filerCategory=" + input.filerCategory);
        LOG.error("getMappedEntitiesForTermRuleForRank: sector=" + input.sector + ", sic=" + input.sic);
        List<Entity> list = termResultService.getEntitiesForTermRuleAndRank(input.termRuleId,
                input.rankId, true, true, input.minYear, input.maxYear, input.includeQuarterly, input.division, 
                input.sector, input.sic, input.filerCategory, input.entityId);
        System.out.println("Size >>"+list.size());
        return list;
        
    }

   
    
    @CrossOrigin
    @RequestMapping(value = {"/UnMappedEntitiesForCriteria"}, method = RequestMethod.PUT)
    public 
    List<Entity> postUnMappedEntitiesForTermRuleForCriteria(@RequestBody TermRuleStatisticsInput input) {
        LOG.debug("getMappedEntitiesForTermRuleForRank: rule=" + input.termRuleId + ", EntityId=" + input.entityId);
        return termResultService.getEntitiesForTermRuleAndRank(input.termRuleId,
                input.rankId, true, false, input.minYear, input.maxYear, input.includeQuarterly, input.division, 
                input.sector, input.sic, input.filerCategory, input.entityId);

    }
    
    

    @RequestMapping(value = "/TermResultsFor/{termRuleId}/{entityId}",
            method = RequestMethod.GET)
    public 
    List<TermResultsDoc> getAllTermResultsByTermRuleAndEntity(@PathVariable("termRuleId") String termRuleId, @PathVariable("entityId") String entityId) {
        LOG.debug("getAllTermResultsByTermRuleAndEntity: rule=" + termRuleId + ", entity=" + entityId);
        List<TermResultsDoc> ret = termResultService.findBaseInfoByTermIdAndEntity(termRuleId, entityId);
        ret = processUtils.generateExperssionTrails(ret);
        LOG.debug("Got results getAllTermResultsByTermRuleAndEntity: rule=" + termRuleId + ", count=" + ret.size());
        Collections.sort(ret, new TermResultsDoc());
        LOG.debug("sorted and returning data");
        return ret;

    }

    @RequestMapping(value = "/TermResultsAll/{termRuleId}",
            method = RequestMethod.GET)
    public 
    List<TermResultsDoc> getAllTermResultsByTermRule(@PathVariable("termRuleId") String termRuleId) {

        LOG.debug("getAllTermResultsByTermRule: " + termRuleId);
        List<TermResultsDoc> ret = termResultService.findByTermId(termRuleId);
        Collections.sort(ret, new TermResultsDoc());
        return ret;
    }

    @RequestMapping(value = "/TermResultsByEntity/{entityId}",
            method = RequestMethod.GET)
    public 
    List<TermResultsDoc> getTermResultsByEntity(@PathVariable("entityId") String entityId) {

        LOG.debug("getTermResultsByEntity: " + entityId);
        List<TermResultsDoc> ret = termResultService.findByEntity(entityId);
        Collections.sort(ret, new TermResultsDoc());
        return ret;
    }

    @RequestMapping(value = "/ProcessingLog/{groupId}",
            method = RequestMethod.GET)
    public 
    List<ProcessingLogDoc> getLogsForGroup(@PathVariable("groupId") String groupId) {
        LOG.info("getLogsForGroup: " + groupId);
        return processingLogService.findByProcessingGroupId(groupId);

    }

    @RequestMapping(value = "/ProcessingLogList/{limit}",
            method = RequestMethod.GET)
    public 
    Iterable<ProcessingLogDoc> getLatestProcessingLogs(@PathVariable("limit") int limit) {
        LOG.info("getLogsForGroup: " + limit);
        return processingLogService.getLatestProcessingLogs(limit);

    }

    //autocomplete/type ahead for concept lookup
    @RequestMapping(value = "/TaxonomyElements")

    public 
    List<USGAAPTaxonomyElement> getTaxonomyElements(@RequestParam("q") String q,
            @RequestParam("isTextBlock") Boolean isTextBlock) {
        LOG.debug("Querying gaap taxonomy: " + q);
        List<USGAAPTaxonomyElement> list = Collections.EMPTY_LIST;
//        if (q != null && q.length() >= 3) {
            LOG.debug("force 3 chars minimum to get list of taxonomies");
            list = lookupDataService.findByTaxonomyElementIdLike(q, isTextBlock);
//        }
        return list;
    }
    @CrossOrigin
	@RequestMapping(value = { "/TermRulesCategoryList" }, method = RequestMethod.GET)
    public 
    Object getRuleCategories() {
        LOG.debug("Querying term Rule categories: ");

        return lookupDataService.findAllRuleCategories();
    }
    @CrossOrigin
    @RequestMapping(value = "/TermRulesPriorityGroupList", method = RequestMethod.GET)
    public 
    List<TermRulePriorityGroup> getRulePriorityGroups() {
        LOG.debug("Querying term Rule priority groups: ");

        return lookupDataService.findAllRulePriorityGroups();
    }
    @CrossOrigin
    @RequestMapping(value = "/TaxonomyElement", method = RequestMethod.GET)
    public 
    USGAAPTaxonomyElement getTaxonomyElementById(@RequestParam("id") String id) {
        LOG.debug("Querying gaap taxonomy by id: " + id);
        USGAAPTaxonomyElement ele = lookupDataService.findOneTaxonomyElement(id);
        if (ele != null) {
            LOG.debug("Taxonomy Label: " + ele.elementDefaultLabel + "\nDefinition:" + ele.elementDefinitionUS);
        }
        return ele;
    }
    
    @RequestMapping(value = "/TermResultsByRuleEntity", method = RequestMethod.PUT)
    public 
    List<TermResultsDoc> getAllTermResultsByTermRuleAndEntity(@RequestBody TermRuleStatisticsInput input) {
        LOG.debug("getAllTermResultsByTermRuleAndEntity: rule=" + input.termRuleId + ", entity=" + input.entityId);
        List<TermResultsDoc> ret = termResultService.findBaseInfoByTermIdAndEntity(input.termRuleId, input.entityId);
//        ret = processUtils.generateExperssionTrails(ret);
        LOG.debug("Got results getAllTermResultsByTermRuleAndEntity: rule=" + input.termRuleId + ", count=" + ret.size());
        Collections.sort(ret, new TermResultsDoc());
        LOG.debug("sorted and returning data");
        return ret;

    }

    @RequestMapping(value = "/ResolveExtendedExpression",
            method = RequestMethod.POST,
            produces = {"application/json"}, consumes = {"application/json"})
    public 
    List<ExtendedCheckResolverOutput> resolveExtendedExpression(@RequestBody ExtendedCheckResolverInput input) {
        LOG.debug("resolveExtendedExpression: " + input);
        return processingService.getExtendedElementsByResolverInput(input);

    }

    
    @CrossOrigin
    @RequestMapping(value = {"/ExportTermResults"}, method = RequestMethod.PUT)
    public 
    ExportOutput getExportTermResults(@RequestBody ExportInput input) {
        LOG.debug("getExportTermResults: " + input);
        return exportService.getExportTermResults(input);

    }

    @CrossOrigin
    @RequestMapping(value = {"/ExportTermCoverage"}, method = RequestMethod.PUT)
    public 
    TermCoverageExportOutput getExportTermCoverage(@RequestBody ExportInput input) {
        LOG.debug("getExportTermResults: " + input);
        return coverageService.getCoverageResults(input);

    }

//    @RequestMapping(value = "/TermRule/rebuildTextBlockInfosForDerivedZeros",
//            method = RequestMethod.POST,
//            produces={"application/json"},consumes={"application/json"})
//    public  PostRequestResult rebuildTextBlockInfosForDerivedZeros() {
//        LOG.debug("rebuildTextBlockInfosForDerivedZeros: ");
//         
//        String isSECAPIAvailable = env.getProperty("sec.textblockAPI", "true");
//        
//        if(! isSECAPIAvailable.equals("true"))
//        {
//            return PostRequestResult.GetErrorResult("SEC API to Build Text Blocks in not available in this environment");
//        }
//        coverageService.rebuildTextBlockInfosForDerivedZeros();
//        return PostRequestResult.GetSuccessResult();
//    }  
//    
//    @RequestMapping(value = "/TermRule/rebuildEntityYearCountInformation",
//            method = RequestMethod.POST,
//            produces={"application/json"},consumes={"application/json"})
//    public  PostRequestResult rebuildEntityYearCountInformation() {
//        LOG.debug("rebuildEntityYearCountInformation: ");
//         
//        if( coverageService == null)
//        {
//            LOG.warn("RebuildEntityYearCountInformation  coverageService is null");
//             return PostRequestResult.GetErrorResult("RebuildEntityYearCountInformation  coverageService is null");
//        }
//        try
//        {
//            coverageService.rebuildEntityYearCountInformation();
//        }
//        catch(Exception exp)
//        {
//            LOG.warn("RebuildEntityYearCountInformation  failed with Exception " + exp.getMessage());
//             return PostRequestResult.GetErrorResult("RebuildEntityYearCountInformation  failed with Exception " + exp.getMessage());
//        }
//        return PostRequestResult.GetSuccessResult();
//    }  
//    
    @RequestMapping(value = "download/ExportData",
            method = RequestMethod.POST)
    public void downloadExportData(HttpServletResponse response, HttpServletRequest request,
            @ModelAttribute("downloadExportForm") ExportInput input) {

        String fileName = request.getParameter("fileName");
        if (!StringUtils.hasText(fileName)) {
            fileName = "ExportResults.csv";
            if (input.getExportType().equals("coverageStats")) {
                fileName = "ExportCoverageStats.csv";
            }
        }
        Map<String, String[]> rps = request.getParameterMap();

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<String> obj = mapper.readValue(rps.get("entityList")[0], List.class);
            input.setEntityList(obj);
            obj = mapper.readValue(rps.get("termIdList")[0], List.class);
            input.setTermIdList(obj);
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }

        try {
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            LOG.debug("downloadExportData: " + input);

            if (input.getExportType().equals("coverageStats")) {
                coverageService.exportCsvToOutputStream(response.getOutputStream(), input);
            } else {
                exportService.exportCsvToOutputStream(response.getOutputStream(), input);
            }

        } catch (Exception exp) {
            LOG.debug("downloadExportTermResults: Exception  " + exp.getMessage());
        }

    }

    @RequestMapping(value = "download/ExportTermResultsForTermAndEntity",
            method = RequestMethod.POST)
    public void downloadExportTermResultsForSingleTermAndEntity(HttpServletResponse response,
            HttpServletRequest request, @ModelAttribute("downloadExportForm") ExportInput input) {

        String fileName = request.getParameter("fileName");
        if (!StringUtils.hasText(fileName)) {
            fileName = "ExportResults.csv";
        }

        try {
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            LOG.debug("downloadExportTermResultsForSingleTermAndEntity: " + input);
            exportService.exportSingleTermEntityCsvToOutputStream(response.getOutputStream(), input);
        } catch (Exception exp) {
            LOG.debug("downloadExportTermResults: Exception  " + exp.getMessage());
        }

        //need to do something here.. to make sure the user knows this call failed...
    }

    @RequestMapping(value = "/TermMap/GroupNames", method = RequestMethod.GET)
    public 
    List<TermMapGroup> getTermMapGroups() {

        return this.lookupDataService.getAllMappingGroups();
    }
    
    @CrossOrigin
    @RequestMapping(value = "/TermMap/AddGroup")
    public 
    PostRequestResult addMappingGroup(@RequestParam String groupName) {
        LOG.debug("addMappingGroup: " + groupName);
        return lookupDataService.addGroup(groupName);
    }

    @CrossOrigin
    @RequestMapping(value = "/TermMap/RemoveGroup")
    public 
    PostRequestResult removeMappingGroup(@RequestParam String groupName, @RequestParam String userId) {
        LOG.debug("removeMappingGroup: " + groupName);
        List<String> roles = getCurrentUserRoles(null,userId);
        for (String str : roles) {
            if (str.equals("admin")) {
                return termMapService.deleteGroup(groupName);
            }
        }

        PostRequestResult ret = new PostRequestResult();
        ret.status = false;
        ret.errorMessage = "User needs admin rights to delete groups";
        return ret;
    }

    //List<TermMapInformationDisplay>
    @CrossOrigin
    @RequestMapping(value = "/TermMap/GetAllMapInformation", method = RequestMethod.GET)
    public 
    List<GroupTermMapInformation> getAllTermMapInformation() {

        return this.termMapService.getAllTermMapInformations();
    }

//    @RequestMapping(value = "/TermMap/MapInformation/Save",
//            method = RequestMethod.POST,
//            produces = {"application/json"}, consumes = {"application/json"})
//    public 
//    GroupTermMapInformation saveTermMappingInfo(@RequestBody GroupTermMapInformation info) {
//
//        return termMapService.saveTermMapInformation(info);
//
//    }
    
    @CrossOrigin
    @RequestMapping(value = {"/TermMap/MapInformation/Save"}, method = RequestMethod.PUT)
    public GroupTermMapInformation saveTermMappingInfo(@RequestBody GroupTermMapInformation info) {

        return termMapService.saveTermMapInformation(info);

    }

//    @RequestMapping(value = "/TermMap/MapInformation/RemoveItem",
//            method = RequestMethod.POST,
//            produces = {"application/json"}, consumes = {"application/json"})
//    public 
//    PostRequestResult removeTermMappingInfo(@RequestBody GroupTermMapInformation info) {
//        LOG.error("Removing Term MapInformation : " + info.termId);
//        return termMapService.deleteGroupItem(info);
//
//    }
    
    @CrossOrigin
    @RequestMapping(value = {"/TermMap/MapInformation/RemoveItem"}, method = RequestMethod.PUT)
    public Object removeTermMappingInfo(@RequestBody GroupTermMapInformation info) {
      LOG.error("Removing Term MapInformation : " + info.termId);
      return termMapService.deleteGroupItem(info);
  }

    @RequestMapping(value = "download/TermMapInformation", method = RequestMethod.POST)
    public void downloadTermMapInformation(HttpServletResponse response,
            HttpServletRequest request) {

        String fileName = request.getParameter("fileName");
        if (!StringUtils.hasText(fileName)) {
            fileName = "ExportTermMapInformation.csv";
        }

        try {
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            termMapService.exportTermMapInformation(response.getOutputStream());
        } catch (Exception exp) {
            LOG.error("downloadTermMapInformation: Exception  " + exp.getMessage());
        }

        //need to do something here.. to make sure the user knows this call failed...
    }

//    @RequestMapping(value = "/file/upload", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA})
    @CrossOrigin
    @RequestMapping(value = {"/file/upload"}, method = RequestMethod.PUT)
    public PostRequestResult fileUpload(@RequestParam("file") MultipartFile file) {
        try {
            LOG.debug("Uploading file : " + file.getOriginalFilename() + ", " + file.getContentType());
            if (file.isEmpty()) {

                return PostRequestResult.GetErrorResult("file is empty");
            }
            adminService.parseLookupReferenceCSV(file.getInputStream());
            return PostRequestResult.GetSuccessResult();
        } catch (Exception ex) {
            LOG.error(file.getOriginalFilename() + " file process failed: Exception  " + ex.getMessage());
            return PostRequestResult.GetErrorResult("file upload failed " + file.getOriginalFilename() + " , " + ex.getMessage());
        }

    }

    @RequestMapping(value = "/AccuracyTest/{cik}/{fiscalYear}", method = RequestMethod.GET)
    public AccuracyTestData getAccuracyTestResults(@PathVariable("cik") String cik,
            @PathVariable("fiscalYear") int fiscalYear) {

        LOG.debug("performAccuracyTest CIK : " + cik);
        LOG.debug("performAccuracyTest fiscal year : " + fiscalYear);
        try {
            return accuracyTestService.runAccuracyTest(cik, fiscalYear);
        } catch (Exception ex) {
            LOG.error("Exception in running accuracy Test. Exception Message :" + ex.getMessage());
            AccuracyTestData error = new AccuracyTestData();
            error.status = false;
            error.errorDescription = "Exception in running accuracy Test. Exception Message :" + ex.getMessage();
            return error;
        }
    }

    @RequestMapping(value = "/AccuracyTest/save",
            method = RequestMethod.POST,
            produces = {"application/json"}, consumes = {"application/json"})
    public 
    PostRequestResult saveAccuracyTestData(@RequestBody AccuracyTestData data) {

        return accuracyTestService.save(data);

    }

    @RequestMapping(value = "download/accuracyTestResults/{companyName}/{cik}/{fiscalYear}", method = RequestMethod.POST)
    public ResponseEntity<byte[]> exportAccuracyTestResults(@PathVariable("companyName") String companyName,@PathVariable("cik") String cik,@PathVariable("fiscalYear") String fiscalYear) {
    	 String fileName = "ExportAccuracyTestResults.csv";
    	 ExportAccuracyTestInput input = new ExportAccuracyTestInput();
         input.cik = cik;
         input.companyName = companyName;
         input.fiscalYear = Integer.parseInt(fiscalYear);
         input.fiscalPeriod = "FY";
    	 HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
    	  try {
              response.setContentType("text/plain");
              response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
              accuracyTestService.exportAccuracyTestResults(response.getOutputStream(), input);
          } catch (Exception exp) {
              LOG.error("downloadTermMapInformation: Exception  " + exp.getMessage());
          }
    	  
    	  	HttpHeaders headers = new HttpHeaders();
    	    headers.setContentLength(1024);
    	    StringBuilder filename = new StringBuilder("MyPdfName").append(".pdf");
    	    byte[] bytes = new byte[1024];
    	    headers.add("content-disposition", "inline;filename=" + filename.toString());
    	    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
    	    ResponseEntity<byte[]> response1 = new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
    	    return response1;
    	   //return null;
    }
    
    @RequestMapping(value = "download/accuracyTestResults", method = RequestMethod.POST)
    public void downloadAccuracyTestResults(HttpServletResponse response,
            HttpServletRequest request) {

        String fileName = request.getParameter("fileName");
        if (!StringUtils.hasText(fileName)) {
            fileName = "ExportAccuracyTestResults.csv";
        }
        ExportAccuracyTestInput input = new ExportAccuracyTestInput();
        input.cik = request.getParameter("cik");
        input.companyName = request.getParameter("companyName");
        input.fiscalYear = Integer.parseInt(request.getParameter("fiscalYear"));
        input.fiscalPeriod = "FY";

        try {
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            accuracyTestService.exportAccuracyTestResults(response.getOutputStream(), input);
        } catch (Exception exp) {
            LOG.error("downloadTermMapInformation: Exception  " + exp.getMessage());
        }

        //need to do something here.. to make sure the user knows this call failed...
    }

    @RequestMapping(value = "/AccuracyTestGetHtml", method = RequestMethod.POST)

    public 
    PostRequestResult getAccuracyTestGetHtml(@RequestBody String url) {

        LOG.debug("performAccuracyTest CIK : " + url);

        try {

            String ret = getURLSource(url);
            return PostRequestResult.GetSuccessResultWithReturnObject(ret);
        } catch (Exception ex) {
            LOG.error("Exception in running accuracy Test. Exception Message :" + ex.getMessage());

            return PostRequestResult.GetErrorResult("Exception in reading html");
        }
    }

    public static String getURLSource(String url) throws IOException {
        URL urlObject = new URL(url);
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        return toString(urlConnection.getInputStream());
    }

    private static String toString(InputStream inputStream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        }
    }
    
    @RequestMapping(value = "/getRuleGraph/{id}/{parent}", method = RequestMethod.GET)
    public 
			List<GraphProperties> getRuleGraph(@PathVariable String id, @PathVariable String parent) {
		LOG.info("getRuleGraph -getRuleGraph: " + id);
		List<GraphProperties> termList = new ArrayList<GraphProperties>();
		TermRule rule = termRuleService.getTermRuleByTermId(id);
		int i = 1;
		int j = 200;
		int k = 400;
		int l = 600;
		int m = 800;
		if (parent.equals("null")) {
			GraphProperties gp = new GraphProperties();
			gp.setName(id);
			gp.setId(Integer.toString(i));
			gp.setParent(null);
			termList.add(gp);
		}
		if (rule != null && !rule.getExpressions().isEmpty()) {
			for (TermExpression expr : rule.getExpressions()) {
				if (expr.type == TermExpressionTypeCode.Formula) {
					for (ExpressionFormula ef : expr.getFormulaList()) {
						GraphProperties gp = new GraphProperties();
						j++;
						TermRule termRule = termRuleService.getTermRuleByTermName(ef.getTermName());
						String operation = ef.getOperation();
						if (operation.isEmpty())
							operation = ef.getOperation();
						else
							operation = "( " + ef.getOperation() + " )";
						gp.setName(termRule.getName() + "(" + termRule.getId() + ") " + operation);
						gp.setParent(Integer.toString(i));
						gp.setId(Integer.toString(j));
						gp.setChildId(termRule.getId());
						termList.add(gp);
						for (TermExpression expr1 : termRule.getExpressions()) {
							if (expr1.type == TermExpressionTypeCode.Formula) {
								for (ExpressionFormula ef1 : expr1.getFormulaList()) {
									GraphProperties gp1 = new GraphProperties();
									k++;
									TermRule termRule1 = termRuleService.getTermRuleByTermName(ef1.getTermName());
									if (termRule1 != null) {
										String operation1 = ef1.getOperation();
										if (operation1.isEmpty())
											operation1 = ef1.getOperation();
										else
											operation1 = "( " + ef1.getOperation() + " )";
										gp1.setName(termRule1.getName() + "(" + termRule1.getId() + ") " + operation1);
										gp1.setParent(Integer.toString(j));
										gp1.setId(Integer.toString(k));
										gp1.setChildId(termRule1.getId());
										termList.add(gp1);

										for (TermExpression expr2 : termRule1.getExpressions()) {
											if (expr2.type == TermExpressionTypeCode.Formula) {
												for (ExpressionFormula ef2 : expr2.getFormulaList()) {
													GraphProperties gp2 = new GraphProperties();
													l++;
													TermRule termRule2 = termRuleService
															.getTermRuleByTermName(ef2.getTermName());
													if (termRule2 != null) {
														String operation2 = ef2.getOperation();
														if (operation2.isEmpty())
															operation2 = ef2.getOperation();
														else
															operation2 = "( " + ef2.getOperation() + " )";
														gp2.setName(termRule2.getName() + "(" + termRule2.getId() + ") "
																+ operation2);
														gp2.setParent(Integer.toString(k));
														gp2.setId(Integer.toString(l));
														gp2.setChildId(termRule2.getId());
														termList.add(gp2);

														for (TermExpression expr3 : termRule2.getExpressions()) {
															if (expr3.type == TermExpressionTypeCode.Formula) {
																for (ExpressionFormula ef3 : expr3.getFormulaList()) {
																	GraphProperties gp3 = new GraphProperties();
																	m++;
																	TermRule termRule3 = termRuleService
																			.getTermRuleByTermName(ef3.getTermName());
																	if (termRule3 != null) {
																		String operation3 = ef3.getOperation();
																		if (operation3.isEmpty())
																			operation3 = ef3.getOperation();
																		else
																			operation3 = "( " + ef3.getOperation()
																					+ " )";
																		gp3.setName(termRule3.getName() + "("
																				+ termRule3.getId() + ") "
																				+ operation3);
																		gp3.setParent(Integer.toString(l));
																		gp3.setId(Integer.toString(m));
																		gp3.setChildId(termRule3.getId());
																		termList.add(gp3);

																	}
																}
															}
														}
													}
												}

											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return termList;
	}
    
    @CrossOrigin
    @RequestMapping(value = "/TermRule/SelectedProcessRule", method = RequestMethod.PUT)
    public Object selectedProcessRule(@RequestBody TermRule termRule)
    {
        LOG.debug("selectedProcessRule: " + termRule.getTermIds());
        List <TermRule> rules = this.termRuleService.getTermRulesByTermId(termRule.getTermIds());
        for (TermRule tr : rules) 
        {
            if (tr.getProcessingStatus() == ProcessingStatusCode.InProgress)
            {
                String msg = String.format("Term Rule: %s is being processed. Please wait for it to complete.", tr.getName());
                return msg;
            }
        }

        processSelected(rules, termRule.getUserid(),rules.size());
       
        return "SuccessFully processed for selected Terms " + termRule.getTermIds() ;
    }
    
    @CrossOrigin
    @PostMapping(value = "/TermRule/processTermsandEntities")
    public Object selectedProcessRule(@RequestBody EntityTermProcessingVO entityTermProcessingVO)
    {
        LOG.debug("Processing Selected: " + entityTermProcessingVO.toString());
        for(String entityId: entityTermProcessingVO.getEntities())
        {
	        for (String termId : entityTermProcessingVO.getTerms()) 
	        {
	            processingService.processTermRule(termId, 0, entityId, entityTermProcessingVO.getUserid());
	        }
	        
        //processSelected(rules, termRule.getUserid(),rules.size());
        }
        return "SuccessFully processed for selected Terms " + entityTermProcessingVO.getTerms().toString()+
        		" and entities "+ entityTermProcessingVO.getEntities().toString() ;
    }
    
    @CrossOrigin
    @RequestMapping(value = "/processAccuracyTestTermsAndEntities")
    public String processAccuracyTestTermsAndEntities(@RequestBody EntityTermProcessingVO entityTermProcessingVO) 
    {
    	LOG.debug("Accuracy Test TermRules Processing for selected terms and entities on Term Process page ...");
    	List<String> entities = entityTermProcessingVO.getEntities();
    	
    	//  Only process selected terms instead of all terms for Accuracy Test
//    	List<String> termIds = entityTermProcessingVO.getTerms();
//    	List<TermRule> trs = new ArrayList<TermRule>();
//    	if (termIds != null && termIds.size() > 0) {
//    		for (String termId: termIds) {
//    			trs.add(termRuleService.getTermRuleByTermId(termId));
//    		}
//    	}

    	//Process all term rules available for Accuracy Test
    	List<TermRule> trs = this.termRuleService.getTermRulesForAccuracyTesting();
    	processSelected(trs, entities, entityTermProcessingVO.getUserid(), trs.size());
    	return "SuccessFully processed Term Rule Based on Accuracy test for entities " + entityTermProcessingVO.getEntities().toString() ;
    	
    }
   
    
    @CrossOrigin
    @RequestMapping(value = "/processAccuracyTestTerms")
    public String processAccuracyTestTerms(@RequestParam("process") Boolean process,
            @RequestParam("userId") String userId) {
    	LOG.debug("Accuracy Test TermRules Processing for all included terms ...");
    	if(process) {
    		List<TermRule> trs = this.termRuleService.getTermRulesForAccuracyTesting();
    		processSelected(trs, userId, trs.size());
    		 return "SuccessFully processed Term Rule Based on Accuracy test";
    	}
    	else 
    		return "Process Failed";
    	
    }
    
    
    @CrossOrigin
    @RequestMapping(value = "/TermRule/resetProcessing", method = RequestMethod.PUT)
    public String resetProcessing(@RequestBody TermRule termRule) {
    	LOG.debug("Reset processing being clicked by user " + termRule.getUserid());
    	List<TermRule> scheduleProcessing = new ArrayList<TermRule>();
    	//this.processingLogService.cancelPendingProcessingItems(termRule.getUserid());
    	List<TermRule> trs = termRuleService.getOrderedTermRuleList();
    	
    	 for (TermRule tr : trs) {
    		 if(tr.getProcessingStatus() == ProcessingStatusCode.InProgress) {
    			 tr.setProcessingStatus(ProcessingStatusCode.NotProcessed);
    			 termRuleService.save(tr,true);
    			 //scheduleProcessing.add(tr);
    		 }
    	 }
    	 ProcessingLogDoc currentDoc = processingLogService.getItemCurrentlyBeingProcessed();
    	 int size = processingLogService.getScheduledCount(ProcessingStatusCode.Scheduled.toString());
    	 while (currentDoc != null) {
    		 int i = 1;
    		 TermRule tr =  this.termRuleService.getTermRuleByTermId(currentDoc.getTermId());
    		 PostRequestResult result = processingService.ProcessRuleWithCriteria(tr.getTermId(), tr.getOrder(), null,
						null, null, null, null, termRule.getUserid(),
						String.format("System Processing Term %d.", i),
						currentDoc.getProcessingGroupId());
    		 i++;
    		 currentDoc = processingLogService.getItemCurrentlyBeingProcessed();
    	 }
    	 processSelected(scheduleProcessing, termRule.getUserid(), size);
    	
    	return "All Term Processing has been reset. Pending and Inprogress TermRules have been restarted again.";
    	
    }
    
   // @Scheduled(cron = "0 0 */1 * * *")
    private void scheduleProcessing() {
    	LOG.debug("Every hour checking for abondened Term Rules which are in Inprogress state and not processing ");
    	List<TermRule> trs = termRuleService.getOrderedTermRuleList();
    	List<TermRule> scheduleProcessing = new ArrayList<TermRule>();
    	int size = processingLogService.getScheduledCount(ProcessingStatusCode.Scheduled.toString());
   	 for (TermRule tr : trs) {
   		long diffInMillies = Math.abs((new Date()).getTime() - tr.getLastModified().getTime());
   		 if(tr.getProcessingStatus() == ProcessingStatusCode.InProgress && diffInMillies > 3600000) {
   			LOG.debug("Every hour checking for abondened Term Rules which are in Inprogress state and not processing and found " + tr.getTermId() + " status is InProgress for more than one hour");
   			 tr.setProcessingStatus(ProcessingStatusCode.NotProcessed);
   			 termRuleService.save(tr,true);
   			 //scheduleProcessing.add(tr);
   		 }
   	 }
   	ProcessingLogDoc currentDoc = processingLogService.getItemCurrentlyBeingProcessed();
	 while (currentDoc != null) {
		 int i = 1;
		 TermRule tr =  this.termRuleService.getTermRuleByTermId(currentDoc.getTermId());
		 PostRequestResult result = processingService.ProcessRuleWithCriteria(tr.getTermId(), tr.getOrder(), null,
					null, null, null, null, "System",
					String.format("Processing Term %d of %d.", i, size),
					currentDoc.getProcessingGroupId());
		 i++;
		 currentDoc = processingLogService.getItemCurrentlyBeingProcessed();
	 }
   	 processSelected(scheduleProcessing, "System", size);
    }
    
	private void processSelected(List<TermRule> trs, String userId, int size) {
		if(size > 0) {
			if(!trs.isEmpty()) {
				this.processingLogService.ScheduleTermsForProcessing(trs);
			}
			ProcessingLogDoc nextDoc = processingLogService.getNextTermToProcess();
			
			while (nextDoc != null) {
				LOG.debug("Processing Rule : " + nextDoc.getTermId());
				TermRule tr = termRuleService.getTermRuleByTermId(nextDoc.getTermId());
				PostRequestResult result = processingService.ProcessRuleWithCriteria(tr.getTermId(), tr.getOrder(), null,
						null, null, null, null, userId,
						String.format("Processing Term %d of %d.", nextDoc.getProcessingSequence(), size),
						nextDoc.getProcessingGroupId());
				nextDoc = processingLogService.getNextTermToProcess();
			}
		}
	}
	
	private void processSelected(List<TermRule> trs, List<String> entities, String userId, int size) {
		for (String cik: entities) {
			if(size > 0) {
				if(!trs.isEmpty()) {
					this.processingLogService.ScheduleTermsForProcessing(trs);
				}
				ProcessingLogDoc nextDoc = processingLogService.getNextTermToProcess();
				
				while (nextDoc != null) {
					LOG.debug("Processing Rule : " + nextDoc.getTermId());
					TermRule tr = termRuleService.getTermRuleByTermId(nextDoc.getTermId());
					PostRequestResult result = processingService.ProcessRuleWithCriteria(tr.getTermId(), tr.getOrder(), cik,
								null, null, null, null, userId,
								String.format("Processing Term %d of %d.", nextDoc.getProcessingSequence(), size),
								nextDoc.getProcessingGroupId());
					nextDoc = processingLogService.getNextTermToProcess();
				}
			}
		}

	}
}
