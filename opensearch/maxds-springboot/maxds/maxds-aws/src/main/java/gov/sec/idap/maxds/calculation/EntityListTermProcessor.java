/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sec.idap.maxds.calculation.Domain.ConceptMatchResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ConceptMatchWithDimResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ExtendedCheckResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ExtendedCheckWithDimensionResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.FYFQInfo;
import gov.sec.idap.maxds.calculation.Domain.FormulaResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.FormulaTermResults;
import gov.sec.idap.maxds.calculation.Domain.ResolvedTermExpression;

import gov.sec.idap.maxds.domain.FQTypeCode;
import gov.sec.idap.maxds.domain.FactDimension;

import gov.sec.idap.maxds.domain.TermExpression;
import gov.sec.idap.maxds.domain.TermExpressionTypeCode;
import gov.sec.idap.maxds.domain.TermResult;
import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.document.NormalizedFact;
import gov.sec.idap.maxds.elasticsearch.service.EntityService;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;

import gov.sec.idap.maxds.elasticsearch.service.NormalizedFactService;
import gov.sec.idap.maxds.elasticsearch.service.ProcessingLogService;
import gov.sec.idap.maxds.elasticsearch.service.TermResultService;
import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;

public class EntityListTermProcessor {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
    HashMap<String, Entity> myEntityList;
    String cikNumericIdsAsString;
    String entityIdsQueryString;

    TermRuleInfoProvider myTermRuleProvider;

    HashMap<String, TermResultsDoc> termResultsMap = new HashMap<>();
    HashMap<String, FYFQInfo> allFilingPeriods = new HashMap<>();
    HashMap<String, FYFQInfo> missingFilingPeriods = new HashMap<>();

    EnumMap<TermExpressionTypeCode, List<ResolvedTermExpression>> expressionMaps
            = new EnumMap<>(TermExpressionTypeCode.class);

    EntityService myEntityService;
    TermRuleService myTermRuleService;
    TermResultService myTermResultService;
    NormalizedFactService myNormalizedFactService;
    ProcessingLogService myProcessingLogService;
    String myProcessingId;
    Boolean populateDerivedNonZeroData = false;
    ExtendedCheckProcessor extendedCheckFactsCache = null;

    public EntityListTermProcessor(HashMap<String, Entity> entMap, TermRuleInfoProvider tr,
            EntityService es,
            TermRuleService ts, TermResultService tresults,
            NormalizedFactService ns, ProcessingLogService ps,
            ExtendedCheckProcessor ecCache,
            String processingId,
            Boolean populateNonZeroData,
            EnumMap<TermExpressionTypeCode, List<ResolvedTermExpression>> eMaps) {

        this.myEntityService = es;
        this.myTermRuleService = ts;
        this.myTermResultService = tresults;
        this.myNormalizedFactService = ns;
        this.myEntityList = entMap;
        this.extendedCheckFactsCache = ecCache;
        this.myTermRuleProvider = tr;
        this.myProcessingLogService = ps;
        this.myProcessingId = processingId;
        this.populateDerivedNonZeroData = populateNonZeroData;
        this.expressionMaps = eMaps;

    }

    
   
            
    
    public Boolean process() {
        LOG.debug("process");

        cikNumericIdsAsString
                = NormalizedFactService.buildEntityElasticQueryStringForFacts(myEntityList);

        entityIdsQueryString
                = NormalizedFactService.buildEntityElasticQueryStringForFilings(myEntityList);

        allFilingPeriods
                = this.myTermResultService.getValidPeriodsForEntityList(entityIdsQueryString);
        
        missingFilingPeriods
                = this.myTermResultService.getMissingFilingPeriodsForEntityList(myEntityList,
                        allFilingPeriods);
        processConceptMatch();
        processConceptMatchMultiple();
        processConceptMatchWithDim();
        processExtendedCheck();
        processExtendedCheckWithDim();

        processQ4Results();
        if (processFormula()) {
            //since we have formulas we might need to recalculate Q4
            processQ4Results();
        }

        processVarianceInfo();
        processValidationInfo();
        saveTermResults();
        saveDerivedZeroData();
        return true;
    }

    private void saveDerivedZeroData() {
        DerivedZeroTermExpressionProcessor dProcessor
                = new DerivedZeroTermExpressionProcessor(
                        this.entityIdsQueryString,
                        this.cikNumericIdsAsString,
                        myTermRuleProvider, myTermResultService, termResultsMap, populateDerivedNonZeroData,
                        allFilingPeriods,missingFilingPeriods, myEntityList);

        dProcessor.reBuildDerivedZeroResults();

    }

    private void processQ4Results() {

        if (this.myTermResultService.canSkipQ4Processing()) {
            return;
        }

        LOG.debug("processQ4Results");
        TermResultQ4ProcessorBase processor = null;
        String type = this.myTermRuleProvider.myTermRule.getType();
        if (type != null && type.equalsIgnoreCase("ratio")) {
            //nothing to do as we don't want to use y-q3-q2-q1 for ratios
            return;

        }
        if (type != null && type.equalsIgnoreCase("weighedshares")) {
            //nothing to do as we don't want to use y-q3-q2-q1 for ratios
            processor = new TermResultQ4ProcessorShare(termResultsMap);

        } else {
            processor = new TermResultQ4ProcessorSimple(termResultsMap);
        }

        termResultsMap = processor.addQ4Results();

    }

    private void processVarianceInfo() {
        LOG.debug("processVarianceInfo");
        VarianceProcessor processor = new VarianceProcessor(termResultsMap);

        termResultsMap = processor.addVarianceInfo();
    }

    private void processValidationInfo() {
        LOG.debug("processValidationInfo");
        ValidationProcessor processor = new ValidationProcessor(this.entityIdsQueryString,
                this.myTermRuleProvider, this.myTermResultService,
                termResultsMap);

        termResultsMap = processor.validate();
    }

    private void saveTermResults() {
        LOG.debug("saveTermResults");
        List<TermResultsDoc> termResults = new ArrayList<>(termResultsMap.values());
        for (TermResultsDoc tr : termResults) {
            String key = String.format("%s %s %d", tr.getEntityId(), tr.FQ.toString(), tr.FY);

            tr.isFilingPeriod = allFilingPeriods.containsKey(key);

        }
        if (termResults.size() > 0) {
            LOG.debug("saveTermResults new records being added to Solr");
            myTermResultService.saveTermResults(termResults);
        }
    }

    private void addTermResult(TermResultsDoc tr) {
        String key = buildTermResultkey(tr);
        TermResultsDoc current = termResultsMap.get(key);
        if (current != null) {

            if (current.rank > tr.rank) {

                //TODO:SOLR
                //swap tr and current...
//                tr.otherTermResults = current.otherTermResults;
//                if( tr.otherTermResults == null)
//                {
//                    tr.otherTermResults = new ArrayList<>();
//                }
//                current.otherTermResults = null;
//                tr.otherTermResults.add(current);
                current = tr;
            } else {

                //for concept match with multiple...
                // we want to take the greater value
                if (current.rank == tr.rank
                        && current.expType == TermExpressionTypeCode.ConceptMatchMultiple
                        && tr.expType == TermExpressionTypeCode.ConceptMatchMultiple) {
                    if (current.value < tr.value) {
                        current = tr;

                    }
                }
            }

        } else {
            current = tr;
        }

        termResultsMap.put(key, current);
    }

    private String buildTermResultkey(TermResultsDoc tr) {
        return String.format("%s %s %d", tr.getEntityId(), tr.FQ, tr.FY);
    }

    private Boolean isProcessedByEarlierRank(String entityId,
            FQTypeCode fq, int fy, int currentRank) {
        return isProcessedByEarlierRank(String.format("%s %s %d", entityId, fq, fy), currentRank);

    }

    private Boolean isProcessedByEarlierRank(String key, int currentRank) {

        if (!termResultsMap.containsKey(key)) {
            return false;
        }

        return termResultsMap.get(key).rank <= currentRank;
    }

    private void processConceptMatchMultiple() {

        if (!expressionMaps.containsKey(TermExpressionTypeCode.ConceptMatchMultiple)) {
            return;
        }
        LOG.debug("processConceptMatchMultiple");

        for (ResolvedTermExpression te : expressionMaps.get(TermExpressionTypeCode.ConceptMatchMultiple)) {

            for (String elementName : te.expression.conceptMatchMultipleList) {
                processConceptExpressionForElementName(te, elementName);
            }

        }

    }

    private void processConceptMatch() {

        if (!expressionMaps.containsKey(TermExpressionTypeCode.ConceptMatch)) {
            return;
        }
        LOG.debug("processConceptMatch");

        for (ResolvedTermExpression te : expressionMaps.get(TermExpressionTypeCode.ConceptMatch)) {
            ConceptMatchResolvedTermExpression cte = (ConceptMatchResolvedTermExpression) te;
            processConceptExpressionForElementName(cte, cte.elementName);

        }

    }

    private List<NormalizedFact> filterFactsForIFRSStandard(List<NormalizedFact> facts, String elementName) {

        if( facts == null || facts.isEmpty()) return facts;
        Boolean isIFRS = elementName.startsWith(("ifrs"));

        List<NormalizedFact> filteredFacts = new ArrayList<>();
        for (NormalizedFact nf : facts) {

            if( nf.getTag_datatype().equals("monetary") && ! nf.getNum_uom().equals("USD")){
                continue;
            }
            
            if (nf.getSub_act_standard() == null || nf.getSub_act_standard().isEmpty()) {
                filteredFacts.add(nf);
            } else {
                if (nf.getSub_act_standard().equals("ifrs")) {
                    if (isIFRS) {
                        filteredFacts.add(nf);
                    }
                } else {
                    if (!isIFRS) {
                        filteredFacts.add(nf);
                    }
                }
            }

        }
        

        return filteredFacts;
    }

    private void processConceptExpressionForElementName(ResolvedTermExpression cte,
            String elementName) {
        List<NormalizedFact> facts
                = myNormalizedFactService.findByEntityIdListAndElementNameIdap(this.cikNumericIdsAsString,
                        elementName, false);
        
        facts = filterFactsForIFRSStandard(facts, elementName);

       
        List<NormalizedFact> factsWithoutDim = new ArrayList<>();
        List<NormalizedFact> factsWithDim = new ArrayList<>();

        for (NormalizedFact nf : facts) {

            if (nf.getFact_fy_i() == 0) {
                continue;
            }
            
          
            
            if (!doesFactQualify(nf, cte)) {
                continue;
            }

            if (nf.getDim_segments() != null && !nf.getDim_segments().isEmpty()) {
                factsWithDim.add(nf);
            } else {
                factsWithoutDim.add(nf);
            }

        }

        HashMap<String, List<NormalizedFact>> factsByFYFQ = new HashMap<>();

        populateFactsInFiscalHashWithoutDim(factsWithoutDim, factsByFYFQ);
        buildTermResultsForConceptMatchFacts(factsByFYFQ, cte, elementName, 0);

        //we can load dimensional facts .. and we can update hsah...
        factsByFYFQ = new HashMap<>();
        populateFactsInFiscalHashWithDim(factsWithDim, factsByFYFQ);
        buildTermResultsForConceptMatchFacts(factsByFYFQ, cte, elementName, 300);
    }

    private Boolean doesFactQualify(NormalizedFact fact,
            ResolvedTermExpression cTE) {

        
        if (cTE.expression.usePositiveValuesOnly && fact.getNum_value() < 0) {
            return false;
        }

        if (cTE.expression.useNegativeValuesOnly && fact.getNum_value() > 0) {
            return false;
        }

        if (cTE.expression.reverseNegativeValues && fact.getNum_value() < 0) {
            fact.setNum_value(fact.getNum_value() * (-1));
            //just reversing ... fact does qualify...
        }

        return true;
    }

    private void populateFactsInFiscalHashWithoutDim(List<NormalizedFact> facts,
            HashMap<String, List<NormalizedFact>> factsByFYFQ) {
        if (facts == null || facts.isEmpty()) {
            return;
        }

        //need to get rid of demoted facts....
        for (NormalizedFact nf : facts) {

            if (nf.getFact_fy_i() == 0) {
                continue;
            }

            String key = nf.getFiscalKeyWithEntity();

            if (!factsByFYFQ.containsKey(key)) {
                factsByFYFQ.put(key, new ArrayList<>());
            }

            factsByFYFQ.get(key).add(nf);

        }
    }

    private void populateFactsInFiscalHashWithDim(List<NormalizedFact> facts,
            HashMap<String, List<NormalizedFact>> factsByFYFQ) {
        if (facts == null || facts.isEmpty()) {
            return;
        }

        HashMap<String, Boolean> processedDims = new HashMap<>();

        //need to get rid of demoted facts....
        for (NormalizedFact nf : facts) {

            if (nf.getFact_fy_i() == 0) {
                continue;
            }

            nf.PopulateDimensionalInfo();
            String key = nf.getFiscalKeyWithEntity();
            if (nf.getDim_segments() != null && !nf.getDim_segments().isEmpty()) {
                String dimKey = String.format("%s:%s", key, nf.getDim_segments());
                if (processedDims.containsKey(dimKey)) {
                    continue; // this particualr dimensional fact has already been loaded...
                }

                processedDims.put(dimKey, true);
            }

            if (!factsByFYFQ.containsKey(key)) {
                factsByFYFQ.put(key, new ArrayList<>());
            }

            factsByFYFQ.get(key).add(nf);

        }
    }

    private void buildTermResultsForConceptMatchFacts(
            HashMap<String, List<NormalizedFact>> factsByFYFQ,
            ResolvedTermExpression cte, String elementName,
            int rankOffSet) {
        for (List<NormalizedFact> factsInPeriod : factsByFYFQ.values()) {
            //since we are sorting the data by filing date..
            //the top fact is guarenteed to be the latest.. fact...
            NormalizedFact topFact = factsInPeriod.get(0);
            if (topFact.getDim_segments() == null || topFact.getDim_segments().isEmpty()) {
                TermResultsDoc tr = createTermResult(topFact, cte.expression, elementName, null);

                if (tr == null) {
                    continue;
                }

                tr.resolvedExpression = String.format("%s = %s", elementName, TermResult.decimalFormat.format(tr.value));
                tr.rank += rankOffSet;
                addTermResult(tr);

            } else {
                List<NormalizedFact> validFacts
                        = reduceDimensionalFactsToUseableFacts(factsInPeriod, cte);

                if (validFacts == null || validFacts.isEmpty()) {
                    //none of the facts qualify...so move on to the next fact....
                    continue;
                }
                //recalcuate tr value based on allowed facts...

                NormalizedFact dfact = validFacts.get(0);

                TermResultsDoc tr = createTermResult(dfact, cte.expression, elementName, null);
                if (tr == null) {
                    continue;
                }

                tr.value = 0;

                //resolved expression "elementName Member@Axis  Member@axis = value + 
                StringBuilder resolvedExpression = new StringBuilder();

                String prefix = "";

                for (NormalizedFact qnf : validFacts) {
                    tr.value += qnf.getNum_value();

                    resolvedExpression.append(String.format("%s %s {%s}",
                            prefix, qnf.getDim_segments(),
                            TermResult.decimalFormat.format(qnf.getNum_value())));

                    prefix = " + ";
                }
                tr.resolvedExpression = String.format("%s = %s(Virtual Parent) Details=%s",
                        elementName,
                        TermResult.decimalFormat.format(tr.value),
                        resolvedExpression.toString());
                tr.rank += rankOffSet;
                addTermResult(tr);

            }

        }
    }

    private List<NormalizedFact> reduceDimensionalFactsToUseableFacts(
            List<NormalizedFact> fullList,
            ResolvedTermExpression cte) {
        //we have only dimensional data...
        //first we need to eliminate.. demoted facts..

        //then we need to eliminate exclusions..
        List<NormalizedFact> factsToConsider = new ArrayList<>();
        if (cte.expression.virtualFactMemberExclusionList != null && cte.expression.virtualFactMemberExclusionList.size() > 0) {
            factsToConsider = getDimensionalFactsWithMembersNotExcluded(
                    cte.expression.virtualFactAxis,
                    cte.expression.virtualFactMemberExclusionList, fullList);
        } else {
            factsToConsider = fullList;
        }

        //now that we have eliminated.. duplicates..
        // we need to group the results by axis info..
        //so that if there are more than one group then... we need to make sure 
        //all groups have the same value..
        HashMap<String, List<NormalizedFact>> groupsByReportedAxis
                = new HashMap<>();

        HashMap<String, Double> groupValuesByAxis
                = new HashMap<>();

        String anyKey = null;
        for (NormalizedFact f : factsToConsider) {
            if (!groupsByReportedAxis.containsKey(f.dimensionAxisKey)) {
                groupsByReportedAxis.put(f.dimensionAxisKey, new ArrayList<>());
                groupValuesByAxis.put(f.dimensionAxisKey, new Double(0));
            }
            anyKey = f.dimensionAxisKey;
            groupsByReportedAxis.get(f.dimensionAxisKey).add(f);
            Double orig = groupValuesByAxis.get(f.dimensionAxisKey);
            groupValuesByAxis.put(f.dimensionAxisKey, orig + f.getNum_value());

        }

        if (groupsByReportedAxis.size() == 1) {
            return factsToConsider;
        }

        //need to verify that the amount is the same accross all groups..
        Double valueToCheck = groupValuesByAxis.get(anyKey);

        Boolean isValid = true;
        for (Double val : groupValuesByAxis.values()) {
            if (!val.equals(valueToCheck)) {
                isValid = false;
                break;
            }
        }

        return isValid ? groupsByReportedAxis.get(anyKey) : null;
    }

    private List<NormalizedFact> getDimensionalFactsWithMembersNotExcluded(
            String axisName,
            List<String> memberExclusionList,
            List<NormalizedFact> dimFacts) {
        if (dimFacts == null) {
            return null;
        }
        List<NormalizedFact> ret = new ArrayList<>();
        for (NormalizedFact dnf : dimFacts) {
            if (axisName != null && axisName.length() > 0) {
                if (dnf.dimensions.size() != 1
                        || !dnf.dimensions.get(0).dimName.equalsIgnoreCase(axisName)) {
                    //skip this fact....
                    continue;
                }
            }
            Boolean add = true;
            for (FactDimension fd : dnf.dimensions) {
                for (String exclude : memberExclusionList) {
                    if (StringUtils.containsIgnoreCase(fd.memberName, exclude)) {
                        add = false;
                        break;
                    }

                }
                if (!add) {
                    break;
                }
            }

            if (add) {
                ret.add(dnf);
            }
        }
        return ret;
    }

    private void processConceptMatchWithDim() {

        if (!expressionMaps.containsKey(TermExpressionTypeCode.ConceptMatchWithDim)) {
            return;
        }
        LOG.debug("processConceptMatchWithDim");

        for (ResolvedTermExpression te : expressionMaps.get(TermExpressionTypeCode.ConceptMatchWithDim)) {
            ConceptMatchWithDimResolvedTermExpression cte = (ConceptMatchWithDimResolvedTermExpression) te;
            List<NormalizedFact> facts
                    = myNormalizedFactService.findByEntityIdListAndElementNameWithDimensions(this.cikNumericIdsAsString,
                            cte.elementName);

            if (facts == null || facts.isEmpty()) {
                continue;
            }
            facts = filterFactsForIFRSStandard(facts, cte.elementName);

            
            int maxDimensionAllowed = 100;
            if (te.expression.useMaxAxisCount) {
                maxDimensionAllowed = te.expression.maxAxisCount;
            }

            HashMap<String, List<NormalizedFact>> factsByFYFQ = new HashMap<>();
            populateFactsInFiscalHashWithDim(facts, factsByFYFQ);

            for (List<NormalizedFact> factsInPeriod : factsByFYFQ.values()) {

                List<NormalizedFact> matchedList = new ArrayList<>();
                for (NormalizedFact dnf : factsInPeriod) {
                    if (dnf.dimensions.size() > maxDimensionAllowed) {
                        continue;
                    }
                    if (!cte.canUseDimensionalFact(dnf)) {
                        continue;
                    }
                    matchedList.add(dnf);
                }

                NormalizedFact matchedDimItem = getBestMatchedDimensionalFact(matchedList);
                if (matchedDimItem != null) {
                    //create termResult..
                    String dimMemberInfo = matchedDimItem.getDimensionDisplayInfo();
                    TermResultsDoc tr = createTermResult(matchedDimItem, te.expression, cte.elementName,
                            dimMemberInfo);
                    if (tr == null) {
                        continue;
                    }
                    tr.resolvedExpression = String.format("%s %s = %s",
                            matchedDimItem.getCell_elt(), dimMemberInfo,
                            TermResult.decimalFormat.format(tr.value));
                    addTermResult(tr);

                }
            }

        }

    }

    private NormalizedFact getBestMatchedDimensionalFact(List<NormalizedFact> matchedList) {
        if (matchedList.isEmpty()) {
            return null;
        }

        if (matchedList.size() == 1) {
            return matchedList.get(0);
        }

        //find the fact with largest value
        NormalizedFact ret = matchedList.get(0);

        for (NormalizedFact nf : matchedList) {

            if (nf.getNum_value() > ret.getNum_value()) {
                ret = nf;
            }
        }

        return ret;
    }

    private void processExtendedCheck() {
        if (!expressionMaps.containsKey(TermExpressionTypeCode.ExtendedCheck)) {
            return;
        }
        LOG.debug("processExtendedCheck");

        Boolean isMonetaryRule = this.isRuleMonetary();

        for (ResolvedTermExpression te : expressionMaps.get(TermExpressionTypeCode.ExtendedCheck)) {
            ExtendedCheckResolvedTermExpression cte = (ExtendedCheckResolvedTermExpression) te;

            List<NormalizedFact> facts = null;
            if (extendedCheckFactsCache == null) {
                facts = myNormalizedFactService.findValueInfoByEntityIdListAndExtendedElements(this.cikNumericIdsAsString, cte);

            } else {
                facts = this.extendedCheckFactsCache.getFactByEntityList(myEntityList, cte);

            }

            if (facts == null || facts.isEmpty()) {
                continue;
            }

            for (NormalizedFact nf : facts) {
                if (isProcessedByEarlierRank(nf.getCikString(), nf.getFact_fp(), nf.getFact_fy_i(), te.expression.rank)) {
                    LOG.debug("skipping processing extended check as a better result already exists");
                    continue;
                }
                if (!this.canUseExtendedFact(nf, isMonetaryRule)) {
                    continue;
                }

                //create termResult..
                TermResultsDoc tr = createTermResult(nf, te.expression, nf.getCell_elt(), null);
                if (tr == null) {
                    continue;
                }
                tr.resolvedExpression = String.format("%s = %s", nf.getCell_elt(), TermResult.decimalFormat.format(tr.value));

                addTermResult(tr);
            }

        }

    }

    private void processExtendedCheckWithDim() {
        if (!expressionMaps.containsKey(TermExpressionTypeCode.ExtendedCheckWithDim)) {
            return;
        }
        LOG.debug("processExtendedCheckWithDim");

        Boolean isMonetaryRule = this.isRuleMonetary();
        for (ResolvedTermExpression te
                : expressionMaps.get(TermExpressionTypeCode.ExtendedCheckWithDim)) {
            ExtendedCheckWithDimensionResolvedTermExpression cte
                    = (ExtendedCheckWithDimensionResolvedTermExpression) te;

            List<NormalizedFact> facts
                    = myNormalizedFactService.findDimensionFactsInfoByEntityIdListAndExtendedElements(this.cikNumericIdsAsString, cte);

            if (facts == null || facts.isEmpty()) {
                continue;
            }

            for (NormalizedFact dnf : facts) {

                if (!this.canUseExtendedFact(dnf, isMonetaryRule)) {
                    continue;
                }
                if (isProcessedByEarlierRank(dnf.getCikString(), dnf.getFact_fp(), dnf.getFact_fy_i(), te.expression.rank)) {
                    LOG.debug("skipping processing extended check as a better result already exists");
                    continue;
                }

                dnf.PopulateDimensionalInfo();

               

                String dimMemberInfo = cte.GetValidDimensionMatchingInfo(dnf, dnf.getCell_elt());

                if (dimMemberInfo == null) {
                    continue;
                }

                TermResultsDoc tr = createTermResult(dnf, te.expression, dnf.getCell_elt(),
                        dimMemberInfo);
                if (tr == null) {
                    continue;
                }
                tr.resolvedExpression = String.format("%s %s = %s", dnf.getCell_elt(), dimMemberInfo, TermResult.decimalFormat.format(tr.value));
                addTermResult(tr);
            }

        }

    }

    private Boolean isRuleMonetary() {
        String type = this.myTermRuleProvider.myTermRule.getType();
        if (type != null) {
            return type.equals("monetary");
        }

        return true;
    }

    private Boolean canUseExtendedFact(NormalizedFact nf, Boolean isMonetaryRule) {

        if( nf.getTag_datatype().equals("monetary") && ! nf.getNum_uom().equals("USD")){
            return false;
        }
        
        return nf.getTag_datatype().equals("monetary") ? isMonetaryRule : !isMonetaryRule;
    }

    private Boolean processFormula() {
        LOG.debug("processFormula");
        if (!expressionMaps.containsKey(TermExpressionTypeCode.Formula)) {
            return false;
        }

        for (ResolvedTermExpression te : expressionMaps.get(TermExpressionTypeCode.Formula)) {
            FormulaResolvedTermExpression formulaExp = (FormulaResolvedTermExpression) te;

            List<TermResultsDoc> termResults = this.myTermRuleProvider.GetTermResultsByEntityListAndTermNames(this.entityIdsQueryString,
                    formulaExp, this.myTermResultService);

            if (termResults == null || termResults.size() == 0) {
                continue;
            }
            FormulaTermResults formulaData = new FormulaTermResults(termResults,
                    this.myTermRuleProvider.myTermRule.getPeriodType());

            BuildFormulaResults(formulaExp, formulaData);
        }

        return true;

    }

    private void BuildFormulaResults(FormulaResolvedTermExpression formulaExp,
            FormulaTermResults formulaData) {

        if (formulaData == null) {
            return;
        }
        for (String key : formulaData.resultsMap.keySet()) {
            if (isProcessedByEarlierRank(key, formulaExp.expression.rank)) {
                LOG.debug("skipping processing formula  as a better result already exists");
                continue;
            }

            FormulaTermResults.FormulaTermResult resultSet = formulaData.resultsMap.get(key);

            TermResultsDoc result = formulaData.createResult(formulaExp, resultSet);
           // LOG.info("Base Formula ::: "+result.resolvedExpression);
            if (result == null) {
                continue;
            }
            Entity entity = this.getEntityById(result.getEntityId());

            if (entity != null) {
                //build the other items in the result..

                result.rank = formulaExp.expression.rank;
                result.termId = this.myTermRuleProvider.myTermRule.getTermId();

                result.companyName = entity.getCompanyName();
                result.setEntityId(entity.getEntityId());
                result.termName = myTermRuleProvider.myTermRule.getName();
                result.expression = formulaExp.expression.expression;
                result.expType = formulaExp.expression.type;
                result.stockSymbol = entity.getTradingSymbol();

                this.addTermResult(result);
            }

        }
    }

    private Entity getEntityById(String entityId) {
        return this.myEntityList.get(entityId);
    }

    private TermResultsDoc createTermResult(NormalizedFact nf,
            TermExpression te, String elementName, String dimMember) {

        Entity entity = getEntityById(nf.getCikString());
        if (entity == null) {
            return null;
        }

        TermResultsDoc ret = new TermResultsDoc();
        ret.FQ = nf.getFact_fp();
        ret.FY = nf.getFact_fy_i();
        ret.rank = te.rank;
        ret.accession = nf.getSub_adsh();
        ret.termId = this.myTermRuleProvider.myTermRule.getTermId();
        ret.value = nf.getNum_value();
        ret.filingDate = nf.buildFilingDateInt();
        ret.companyName = entity.getCompanyName();
        ret.setEntityId(entity.getEntityId());
        ret.termName = myTermRuleProvider.myTermRule.getName();
        ret.expression = te.expression;
        ret.expType = te.type;
        ret.stockSymbol = entity.getTradingSymbol();
        ret.cik = nf.getCikString();
        ret.elementName = elementName;
        ret.periodType = nf.getPeriodType();
        ret.periodEndDate = nf.getNum_ddate();

        ret.dimMemberName = dimMember;
        if (dimMember != null) {
            ret.dimExpression = te.axisExpression + " & " + te.memberExpression;
            ret.hasDimensionalData = true;
        }

        return ret;
    }

}
