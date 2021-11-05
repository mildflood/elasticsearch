/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import gov.sec.idap.maxds.calculation.Domain.FYFQInfo;
import gov.sec.idap.maxds.domain.DerviedZeroExpression;
import gov.sec.idap.maxds.domain.Entity;

import gov.sec.idap.maxds.solr.document.TermResultsDoc;
import gov.sec.idap.maxds.domain.TermResultsDerivedNonZero;
import gov.sec.idap.maxds.domain.TermResultsDerivedZero;
import gov.sec.idap.maxds.service.TermResultService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author SRIKANTH
 */
public class DerivedZeroTermExpressionProcessor {

    protected HashMap<String, TermResultsDoc> termResultsMap;
    HashMap<String, Entity> myEntityList;
    String entityIdsQueryString;
    String cikNumericIdsAsString;
    TermRuleInfoProvider myTermRuleProvider;
    TermResultService myTermResultService;
    List<TermResultsDerivedZero> derivedZeroDataList = new ArrayList<>();
    List<TermResultsDerivedNonZero> derivedNonZeroDataList = new ArrayList<>();
    Boolean populateDerivedNonZeroData = false;
    HashMap<String, FYFQInfo> allPeriods = null;
    HashMap<String, FYFQInfo> missingPeriods = null;

    public DerivedZeroTermExpressionProcessor(
            String e,
            String numericCiks,
            TermRuleInfoProvider tr,
            TermResultService trs,
            HashMap<String, TermResultsDoc> results,
            Boolean populateNonZeroData,
            HashMap<String, FYFQInfo> allFilingPeriods,
            HashMap<String, FYFQInfo> missingFilingPeriods,
            HashMap<String, Entity> entityList) {
        termResultsMap = results;
        this.entityIdsQueryString = e;
        this.cikNumericIdsAsString = numericCiks;
        this.myTermRuleProvider = tr;
        myTermResultService = trs;
        this.populateDerivedNonZeroData = populateNonZeroData;
        this.allPeriods = allFilingPeriods;
        this.missingPeriods = missingFilingPeriods;
        this.myEntityList = entityList;
    }

    public void reBuildDerivedZeroResults() {

        List<DerviedZeroExpression> expressions = myTermRuleProvider.myTermRule.getDerivedZeroExpressions();

        if (allPeriods == null) {
            allPeriods = this.myTermResultService.getValidPeriodsForEntityList(entityIdsQueryString);
        }
        HashMap<String, String> dataPoints = this.BuildDataPoints();
        if (expressions != null && !expressions.isEmpty()) {
            
            DerivedZeroTermExpressionHelper helper
                    = new DerivedZeroTermExpressionHelper(entityIdsQueryString, cikNumericIdsAsString,
                            expressions, this.myTermResultService,
                            this.myTermRuleProvider, myEntityList);

            for (String key : allPeriods.keySet()) {
                boolean hasData = dataPoints.containsKey(key);

                FYFQInfo info = allPeriods.get(key);

                TermResultsDerivedZero derivedZero = buildDerivedZero(key, info,
                        expressions, helper);
                if (derivedZero != null) {
                    derivedZero.setHasTermResult(hasData);
                    derivedZeroDataList.add(derivedZero);

                } else {
                    if (populateDerivedNonZeroData && !hasData) {
                        TermResultsDerivedNonZero ret = buildTermResultsDerivedNonZero(
                                this.myTermRuleProvider.myTermRule.getTermId(), info);
                        //we need to add derived Non zero...
                        derivedNonZeroDataList.add(ret);
                    }
                }

            }
        }

        if (this.missingPeriods != null) {
            for (String key : missingPeriods.keySet()) {
                if (dataPoints.containsKey(key)) {
                    continue;
                }

                FYFQInfo info = missingPeriods.get(key);

                //add a derived zero expression for missing filing...
                TermResultsDerivedZero tr = buildTermResultsDerivedZero(
                        this.myTermRuleProvider.myTermRule.getTermId(), info, "Filing for period not found");
                derivedZeroDataList.add(tr);
            }
        }

        saveDerivedZeroData();
        saveDerivedNonZeroData();

    }

    TermResultsDerivedZero buildTermResultsDerivedZero(
            String termId,
            FYFQInfo info,
            String reason) {
        TermResultsDerivedZero ret = new TermResultsDerivedZero();
        ret.setEntityId(info.entityId);
        ret.setFY(info.FY);
        ret.setFQ(info.FQ);
        ret.setTermId(termId);
        ret.setVerifiedNotes(reason);
        return ret;
    }

    TermResultsDerivedNonZero buildTermResultsDerivedNonZero(
            String termId,
            FYFQInfo info) {
        TermResultsDerivedNonZero ret = new TermResultsDerivedNonZero();
        ret.setEntityId(info.entityId);
        ret.setFY(info.FY);
        ret.setFQ(info.FQ);
        ret.setTermId(termId);

        return ret;
    }

    private TermResultsDerivedZero buildDerivedZero(String key, FYFQInfo info,
            List<DerviedZeroExpression> expressions,
            DerivedZeroTermExpressionHelper helper) {
        TermResultsDerivedZero ret = buildTermResultsDerivedZero(
                this.myTermRuleProvider.myTermRule.getTermId(), info, "");
        Boolean canAdd = false;
        String reason = "";
        for (DerviedZeroExpression exp : expressions) {
            switch (exp.type) {
                case NoteBlockExistsCheck:
                    if (helper.noteBlockExistsCheck(key)) {
                        return null;
                    }
                    canAdd = true;
                    reason += exp.expression;
                    break;
                case DependentTermCheck:
                    if (helper.dependentTermExistsCheck(key)) {
                        return null;
                    }
                    canAdd = true;
                    reason += exp.expression;
                    break;
                case ExclusiveTermCheck:
                    if (helper.exclusiveTermExistsCheck(key)) {
                        canAdd = true;
                        reason += exp.expression;
                    }

                    break;
                case IndustryExclusionCheck:
                    if (helper.exclusiveIndustryEntityExistsCheck(info.entityId)) {
                        canAdd = true;
                        reason += exp.expression;
                    }

                    break;
            }
        }
        ret.setVerifiedNotes(reason);

        return canAdd ? ret : null;
    }

    private void saveDerivedZeroData() {
        this.myTermResultService.saveDerivedZeroData(derivedZeroDataList);
    }

    private void saveDerivedNonZeroData() {
        this.myTermResultService.saveDerivedNonZeroData(derivedNonZeroDataList);
    }

    private String buildTermResultkey(TermResultsDoc tr) {
        return buildkey(tr.entityId, tr.FQ.toString(), tr.FY);
    }

    public static String buildkey(String entityId, String FQ, int FY) {
        return String.format("%s %s %d", entityId, FQ, FY);
    }

    private HashMap<String, String> BuildDataPoints() {
        HashMap<String, String> ret = new HashMap<String, String>();
        for (TermResultsDoc tr : this.termResultsMap.values()) {
            String key = this.buildTermResultkey(tr);
            if (!ret.containsKey(key)) {
                ret.put(key, key);
            }
        }

        return ret;
    }

}
