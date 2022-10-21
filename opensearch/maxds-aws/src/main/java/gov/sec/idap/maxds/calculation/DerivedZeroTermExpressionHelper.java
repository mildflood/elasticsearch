/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import java.util.HashMap;
import java.util.List;

import gov.sec.idap.maxds.domain.DerviedZeroExpression;
import gov.sec.idap.maxds.domain.TextBlockInfo;
import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;
import gov.sec.idap.maxds.elasticsearch.service.TermResultService;


public class DerivedZeroTermExpressionHelper {

    List<DerviedZeroExpression> expressions;
    String entityIdsQueryString;
    String cikNumericIdsAsString;
    protected HashMap<String, String> noteBlockExistsHash;
    protected HashMap<String, String> dependentTermExistsHash;
    protected HashMap<String, String> exclusiveTermExistsHash;
    protected HashMap<String, String> exclusiveIndustryEntityExistsHash;
    TermResultService myTermResultService;
    TermRuleInfoProvider myTermRuleProvider;
    HashMap<String, Entity> myEntityList;

    public DerivedZeroTermExpressionHelper(String e,
            String numericCiks,
            List<DerviedZeroExpression> exp, TermResultService trs,
            TermRuleInfoProvider tr, HashMap<String, Entity> entityList) {
        entityIdsQueryString = e;
        this.cikNumericIdsAsString = numericCiks;
        expressions = exp;
        noteBlockExistsHash = new HashMap<>();
        dependentTermExistsHash = new HashMap<>();
        exclusiveTermExistsHash = new HashMap<>();
        exclusiveIndustryEntityExistsHash = new HashMap<>();
        myTermResultService = trs;
        this.myTermRuleProvider = tr;
        this.myEntityList = entityList;

        Initialize();
    }

    private void Initialize() {
        
        if (expressions == null || expressions.size() == 0)
            return;
        
        for (DerviedZeroExpression exp : expressions) {
            switch (exp.type) {
                case NoteBlockExistsCheck:
                    buildNoteBlockExistsCheckData(exp);
                    break;
                case DependentTermCheck:
                    buildDependentTermCheckData(exp);
                    break;
                case ExclusiveTermCheck:
                    buildExclusiveTermCheckData(exp);
                    break;
                case IndustryExclusionCheck:
                    buildExclusiveIndustryData(exp);
                    break;

            }
        }
    }

    private void buildNoteBlockExistsCheckData(DerviedZeroExpression exp) {
        String[] textBlocks = ExpressionParsingUtil.getItemsFromExpressionStringSimple(exp.expression);

        List<TextBlockInfo> infos = myTermResultService.getTextBlocks(cikNumericIdsAsString, textBlocks);
        if (infos == null) {
            return;
        }
        for (TextBlockInfo info : infos) {
            String key = DerivedZeroTermExpressionProcessor.buildkey(info.getEntityId(),
                    info.FQ, info.FY);

            if (!noteBlockExistsHash.containsKey(key)) {
                noteBlockExistsHash.put(key, key);
            }
        }
    }

    private String getTermIdQueryString(String[] termNames) {
        StringBuilder bld = new StringBuilder();
        bld.append("(");
        for (String name : termNames) {

            String id = this.myTermRuleProvider.getTermIdFromTermName(name);
            if (id != null) {
                bld.append(String.format("%s ", id));
            }

        }
        bld.append(")");
        return bld.toString();
    }

    private void buildDependentTermCheckData(DerviedZeroExpression exp) {
        String[] termNames = ExpressionParsingUtil.getItemsFromExpressionStringSimple(exp.expression);

        List<TermResultsDoc> infos
                = this.myTermResultService.findByEntityIdListAndTermIdList(entityIdsQueryString,
                        getTermIdQueryString(termNames));

        if (exp.allTermsRequired && termNames.length > 1) {

            HashMap<String, Integer> depTermHash = new HashMap<>();
            for (TermResultsDoc info : infos) {
                String key = DerivedZeroTermExpressionProcessor.buildkey(info.getEntityId(),
                        info.FQ.toString(), info.FY);

                if (!depTermHash.containsKey(key)) {
                    depTermHash.put(key, 1);
                } else {
                    depTermHash.put(key, depTermHash.get(key) + 1);
                }
            }

            for (String key : depTermHash.keySet()) {

                if (depTermHash.get(key) == termNames.length) {
                    dependentTermExistsHash.put(key, key);
                }

            }

        } else {

            for (TermResultsDoc info : infos) {
                String key = DerivedZeroTermExpressionProcessor.buildkey(info.getEntityId(),
                        info.FQ.toString(), info.FY);

                if (!dependentTermExistsHash.containsKey(key)) {
                    dependentTermExistsHash.put(key, key);
                }
            }
        }

    }

    private void buildExclusiveTermCheckData(DerviedZeroExpression exp) {
        String[] termNames = ExpressionParsingUtil.getItemsFromExpressionStringSimple(exp.expression);

        List<TermResultsDoc> infos = this.myTermResultService.findByEntityIdListAndTermIdList(entityIdsQueryString, getTermIdQueryString(termNames));

        for (TermResultsDoc info : infos) {
            String key = DerivedZeroTermExpressionProcessor.buildkey(info.getEntityId(),
                    info.FQ.toString(), info.FY);

            if (!exclusiveTermExistsHash.containsKey(key)) {
                exclusiveTermExistsHash.put(key, key);
            }
        }
    }

    private void buildExclusiveIndustryData(DerviedZeroExpression exp) {

        for (Entity entity : this.myEntityList.values()) {
            if( isMatch(entity, exp))
            {
                this.exclusiveIndustryEntityExistsHash.put(entity.getEntityId(), entity.getEntityId());
            }
        }

        
    }

    private Boolean isMatch(Entity entity, DerviedZeroExpression exp) {
        Boolean ret = false;
        if (exp.sector != null && !exp.sector.equals(entity.getSector())) {
            return false;
        } else {
            ret = true;
        }
        
        if (exp.division != null && !exp.division.equals(entity.getDivision())) {
            return false;
        } else {
            ret = true;
        }

        if (exp.industry != null && !exp.industry.isEmpty() && !exp.industry.equals(entity.getSic())) {
            return false;
        } else {
            ret = true;
        }
        

        return ret;
    }

    public Boolean noteBlockExistsCheck(String key) {
        return noteBlockExistsHash.containsKey(key);
    }

    public Boolean dependentTermExistsCheck(String key) {
        return dependentTermExistsHash != null && dependentTermExistsHash.containsKey(key);
    }

    public Boolean exclusiveTermExistsCheck(String key) {
        return exclusiveTermExistsHash != null && exclusiveTermExistsHash.containsKey(key);
    }
    public Boolean exclusiveIndustryEntityExistsCheck(String key) {
        return this.exclusiveIndustryEntityExistsHash != null && exclusiveIndustryEntityExistsHash.containsKey(key);
    }
}
