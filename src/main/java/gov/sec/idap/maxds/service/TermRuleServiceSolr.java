/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.sec.idap.maxds.calculation.Domain.FormulaResolvedTermExpression;
import gov.sec.idap.maxds.calculation.ExpressionParsingUtil;

import gov.sec.idap.maxds.calculation.TermRuleInfoProvider;
import gov.sec.idap.maxds.domain.DerviedZeroExpression;
import gov.sec.idap.maxds.domain.DerviedZeroTypeCode;
import gov.sec.idap.maxds.domain.PostRequestResult;
import gov.sec.idap.maxds.domain.ProcessingStatusCode;
import gov.sec.idap.maxds.domain.TermExpression;
import gov.sec.idap.maxds.domain.TermExpressionTypeCode;
import gov.sec.idap.maxds.domain.TermRule;
import gov.sec.idap.maxds.domain.TermRuleHeader;
import gov.sec.idap.maxds.domain.TermRuleOverride;
import gov.sec.idap.maxds.domain.TermValidationExpression;
import gov.sec.idap.maxds.elasticsearch.document.TermRuleDoc;
import gov.sec.idap.maxds.elasticsearch.repository.TermRuleRepository;

//import gov.sec.idap.maxds.solr.document.TermRuleDoc;
//import gov.sec.idap.maxds.solr.repository.TermRuleDocRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

@Service("termRuleServiceSolr")
public class TermRuleServiceSolr {

    @Autowired
    //private TermRuleDocRepository repository;
    private TermRuleRepository repository;
    
    private TermRule getRule(List<TermRuleDoc> ruleDocs) {
        if (ruleDocs.size() >= 1) {
            TermRuleDoc doc = (TermRuleDoc) ruleDocs.get(0);
            return doc.BuildRuleFromRuleText();
        }
        return null;
    }

    private List<TermRule> getRuleList(Iterable<TermRuleDoc> ruleDocs) {
        List<TermRule> trFullList = new ArrayList<>();
        for (TermRuleDoc tr : ruleDocs) {

            trFullList.add(tr.BuildRuleFromRuleText());
        }

        return trFullList;
    }

    private List<TermRuleHeader> getRuleHeaderList(Iterable<TermRuleDoc> ruleDocs) {
        List<TermRuleHeader> trList = new ArrayList<>();
        for (TermRuleDoc tr : ruleDocs) {

            trList.add(tr.buildTermRuleHeader());
        }

        return trList;
    }

    private TermRuleDoc buildTermRuleDoc(TermRule tr) {
        TermRuleDoc doc = new TermRuleDoc();
        tr.setId(tr.getTermId());
        BeanUtils.copyProperties(tr, doc);

        List validations = tr.getValidationExpressions();
        doc.setHasValidations(validations != null && validations.size() > 0);

        ObjectMapper mapper = new ObjectMapper();
        try {
            doc.setRuleText(mapper.writeValueAsString(tr));
        } catch (JsonProcessingException exp) {

        }

        return doc;
    }

    public TermRule getTermRuleByTermId(String id) {

        return getRule(this.repository.findByTermId(id));

    }
    
    public List<TermRule> getTermRulesForAccuracyTesting(){
       // return getRuleList(repository.findByIncludeInAccuracyTests());
    	return null;
    }

    public TermRule getTermRuleByTermName(String termName) {

        return getRule(this.repository.findByTermName(termName));

    }

    public List<TermRule> findAll() {

        return getRuleList(repository.findAll());
    }

    public List<TermRule> getOrderedTermRuleList() {
       
        List<TermRule> trFullList = getRuleList(repository.findAll());
        this.UpdateOrderIdInTerms(trFullList);
       

        return trFullList;
    }

    public List<TermRuleHeader> findLimited() {

        return this.getRuleHeaderList(repository.findLimited());
    }

    public void UpdateOrderIdInTerms(List<TermRule> rules) {
        HashMap<String, Integer> termOrderIdMap = new HashMap<>();
        HashMap<String, TermRule> termNameMap = new HashMap<>();
        for (TermRule tr : rules) {
            termNameMap.put(tr.getName(), tr);
            tr.setOrder(1);
        }

        //set the order on all independent terms....
        for (TermRule tr : rules) {
            if (termOrderIdMap.containsKey(tr.getName())) {
                continue;
            }
            getTermRuleDefaultOrder(tr, termNameMap, termOrderIdMap);

        }

    }

    private int getTermRuleDefaultOrder(TermRule tr,
            HashMap<String, TermRule> termNameMap,
            HashMap<String, Integer> termOrderIdMap) {
        if (termOrderIdMap.containsKey(tr.getName())) {
            return termOrderIdMap.get(tr.getName());
        }

        ArrayList<String> depTermNames = getTermsUsed(tr);
        if (depTermNames == null || depTermNames.isEmpty()) {
            tr.setOrder(1);
            termOrderIdMap.put(tr.getName(), tr.getOrder());

            return 1;
        }

        //to avoid recursion..
        //lets set the order id for this tern to be 10000;
        termOrderIdMap.put(tr.getName(), 10000);
        int maxOrder = 1;
        for (String termName : depTermNames) {
            if (!termNameMap.containsKey(termName)) {
                continue;
            }
            int order = getTermRuleDefaultOrder(termNameMap.get(termName),
                    termNameMap, termOrderIdMap);

            if (maxOrder < order) {
                maxOrder = order;
            }
        }

        tr.setOrder(maxOrder + 1);
        termOrderIdMap.remove(tr.getName());
        termOrderIdMap.put(tr.getName(), tr.getOrder());
        return tr.getOrder();
    }

    public ArrayList<String> getTermsUsed(TermRule tr) {

        ArrayList<String> ret = new ArrayList<>();
        if (tr.getExpressions() != null) {
            for (TermExpression te : tr.getExpressions()) {
                if (te.type == TermExpressionTypeCode.Formula) {
                    ret.addAll(getTermsUsedInFormulaExpression(te));
                }

            }
        }

        if (tr.getOverrides() != null) {
            for (TermRuleOverride tro : tr.getOverrides()) {

                if (tro.getExpressions() != null) {
                    tro.getExpressions()
                            .stream()
                            .filter((te) -> (te.type == TermExpressionTypeCode.Formula))
                            .forEachOrdered((te) -> {
                                ret.addAll(getTermsUsedInFormulaExpression(te));
                            });
                }
            }

        }

        if (tr.getDerivedZeroExpressions() != null) {
            for (DerviedZeroExpression dze : tr.getDerivedZeroExpressions()) {
                if (dze.type == DerviedZeroTypeCode.DependentTermCheck
                        || dze.type == DerviedZeroTypeCode.ExclusiveTermCheck) {
                    String[] terms = ExpressionParsingUtil.getItemsFromExpressionStringSimple(dze.expression);
                    if (terms != null && terms.length > 0) {
                        ret.addAll(Arrays.asList(terms));
                    }

                }
            }
        }

        if (tr.getValidationExpressions() != null) {

            for (TermValidationExpression tve : tr.getValidationExpressions()) {

                TermExpression te = new TermExpression();
                te.expression = tve.expression;
                ret.addAll(getTermsUsedInFormulaExpression(te));

            }
        }
        return ret;

    }

    private ArrayList<String> getTermsUsedInFormulaExpression(TermExpression te) {

        ArrayList<String> ret = new ArrayList<>();
        FormulaResolvedTermExpression fte = new FormulaResolvedTermExpression(te);
        return fte.GeTermNamesUsed();

    }

    public void insert(TermRule termRule) {
        termRule.setLastModified(new Date());

        repository.save(buildTermRuleDoc(termRule));
    }

    public PostRequestResult save(TermRule termRule, Boolean calculateOrder) {

        TermRule origTermRule
                = getRule(repository.findByTermId(termRule.getTermId()));

        if (termRule.getId() == null && origTermRule != null) {
            //we are trying to insert a new term rule with the same term Id of another term rule..
            return PostRequestResult.GetErrorResult("Term Rule with same Id exists. Cannot create multiple term rules with the same Term Id.");

        }

        if (origTermRule != null) {
//            if (origTermRule.getProcessingStatus() == ProcessingStatusCode.Processing) {
//                //we are in the middle of processing..
//                //we don;t want to save anything to the user...
//
//                return PostRequestResult.GetErrorResult("Term Rule is being processed. Updates are allowed only after the processing has been completed.");
//            }

            termRule.setId(origTermRule.getId());

        }
        if (calculateOrder) {
            //TermRuleInfoProvider provider = new TermRuleInfoProvider(termRule, this);
            TermRuleInfoProvider provider = new TermRuleInfoProvider();
            termRule.setOrder(provider.getDefaultOrderIdForTermRule());
        }
        //determine the order id for the term rule

        //evrytime we save termrule we can set the status to not processed
        //as any previoud state is not valid...
        termRule.setProcessingStatus(ProcessingStatusCode.NotProcessed);
        if (termRule.getOverrides() != null) {
            List<TermRuleOverride> updated = new ArrayList<>();
            for (TermRuleOverride o : termRule.getOverrides()) {
                if (o.getExpressions() != null && o.getExpressions().size() > 0) {
                    updated.add(o);
                }
            }
            termRule.setOverrides(updated);
            termRule.setOverrides(termRule.getSortedOverrides());
        }
        SaveAsIs(termRule);

        return PostRequestResult.GetSuccessResultWithReturnObject(termRule);
    }

    public void SaveAsIs(TermRule termRule) {

        termRule.setLastModified(new Date());
        TermRuleDoc itemToSave = buildTermRuleDoc(termRule);

        repository.save(itemToSave);

    }

    public void deleteByTermRule(String termId) {

        repository.deleteByTermId(termId);
    }

    public PostRequestResult canDeleteTermRule(String termId) {

        TermRule rule = this.getTermRuleByTermId(termId);
        if (rule == null) {
            return PostRequestResult.GetSuccessResult();
        }

        for (TermRule tr : this.findAll()) {
           // TermRuleInfoProvider prov = new TermRuleInfoProvider(tr, this);
            TermRuleInfoProvider prov = new TermRuleInfoProvider();
            if (prov.isTermNameUsedInDefaultFormulas(rule.getName())) {
                return PostRequestResult.GetErrorResult(String.format("Cannot delete Term Rule as it is used in a formula in the following term: %s",
                        tr.getName()));
            }
        }

        return PostRequestResult.GetSuccessResult();
    }

    public Collection<String> getAllDerivedTextBlockElementNames() {

        HashMap<String, String> noteBlocksHash = new HashMap<>();
        for (TermRule tr : this.findAll()) {
            if (tr.getDerivedZeroExpressions() != null) {
                for (DerviedZeroExpression dExp : tr.getDerivedZeroExpressions()) {
                    if (dExp.type == DerviedZeroTypeCode.NoteBlockExistsCheck) {
                        for (String tb : ExpressionParsingUtil.getItemsFromExpressionStringSimple(dExp.expression)) {
                            if (!noteBlocksHash.containsKey(tb)) {
                                noteBlocksHash.put(tb, tb);
                            }
                        }
                    }
                }

            }
        }

        return noteBlocksHash.values();
    }
    
    public List<TermRule> getTermRulesByTermId(List<String> termIds) {
    	List<TermRule> trFullList = getRuleList(repository.findByTermIds(termIds));
        this.UpdateOrderIdInTerms(trFullList);
        return trFullList;
    }
}
