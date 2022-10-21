/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import gov.sec.idap.maxds.calculation.Domain.EntityGroupedByExpressionMap;
import gov.sec.idap.maxds.calculation.Domain.FormulaResolvedTermExpression;
import gov.sec.idap.maxds.calculation.Domain.ResolvedTermExpression;

import gov.sec.idap.maxds.domain.EntityOverride;
import gov.sec.idap.maxds.domain.TermExpression;
import gov.sec.idap.maxds.domain.TermExpressionTypeCode;

import gov.sec.idap.maxds.domain.TermRule;
import gov.sec.idap.maxds.domain.TermRuleOverride;
import gov.sec.idap.maxds.domain.TermValidationExpression;
import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;
import gov.sec.idap.maxds.elasticsearch.service.TermResultService;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class TermRuleInfoProvider {

    public List<EntityGroupedByExpressionMap> entityListGroups = new ArrayList<>();

    private EnumMap<TermExpressionTypeCode, List<ResolvedTermExpression>> defaultExpressionMaps
            = new EnumMap<>(TermExpressionTypeCode.class);

    public List<FormulaResolvedTermExpression> validationExpressions = new ArrayList<>();

    public TermRule myTermRule;

    public TermRuleService myTermRuleService;

    private HashMap<String, TermRule> myTermRuleByName = new HashMap<>();

    public TermRuleInfoProvider(TermRule tr, TermRuleService ts) {
        myTermRule = tr;
        myTermRuleService = ts;

        defaultExpressionMaps = BuildExpressionMaps(this.myTermRule.getExpressions());
    }

    public TermRuleInfoProvider() {
	}

	public void buildEntityGroupedByExpressionMap(List<Entity> allEntities) {
        entityListGroups = new ArrayList<>();
        List<Entity> remainingEntities = new ArrayList<>();
        if (this.myTermRule.getOverrides() != null
                && this.myTermRule.getOverrides().size() > 0) {
            //has overrides.. need to create multiple groups..
            for (TermRuleOverride override : myTermRule.getOverrides()) {

                EntityGroupedByExpressionMap overrideMap = new EntityGroupedByExpressionMap();
                
                if( override.getMergeBaseExpressions()){
                
                    List<TermExpression> fullList = new ArrayList();
                    for( TermExpression te : override.getExpressions()){
                        if( te == null ) continue;
                        te.rank-=1001;   
                        fullList.add(te);
                    }
                    fullList.addAll(myTermRule.getExpressions());
                    overrideMap.expressionMaps = BuildExpressionMaps(fullList);
                    //need to merge the base expressions with the override expressions
                }else{
                    overrideMap.expressionMaps = BuildExpressionMaps(override.getExpressions());
                }
                entityListGroups.add(overrideMap);
            }
            
            for( Entity e : allEntities)
            {
                int index = this.getEntityOverrideIndex(e);
                if( index >= 0)
                {
                    entityListGroups.get(index).entityList.add(e);
                }
                else
                {
                    remainingEntities.add(e);
                }
            }
            
        } else {
            remainingEntities = allEntities;
        }

        EntityGroupedByExpressionMap main = new EntityGroupedByExpressionMap();
        main.entityList = remainingEntities;
        main.expressionMaps = defaultExpressionMaps;

        entityListGroups.add(main);
    }
    
    private int getEntityOverrideIndex(Entity entity)
    {
        int index = -1;
        for (TermRuleOverride override : myTermRule.getOverrides()) {
            
                index++;
                if (override.getEntityOverrides() == null) {
                    continue;
                }
                if (override.getExpressions() == null || override.getExpressions().size() == 0) {
                    continue;
                }
                for (EntityOverride eo : override.getEntityOverrides()) {
                    if (eo.isMatch(entity)) {
                        //found match..
                        //need to use the override...
                        return index;

                    }
                }

            }
        
        return -1;
    }

    public String getTermIdFromTermName(String name) {
        if (!myTermRuleByName.containsKey(name)) {
            TermRule tr = this.myTermRuleService.getTermRuleByExactName(name);

            myTermRuleByName.put(name, tr);
        }

        TermRule rule = myTermRuleByName.get(name);

        return rule != null ? rule.getTermId() : null;
    }

    public TermRule getTermRuleFromTermName(String name) {
        if (!myTermRuleByName.containsKey(name)) {
            TermRule tr = this.myTermRuleService.getTermRuleByTermId(name);

            myTermRuleByName.put(name, tr);
        }

        return myTermRuleByName.get(name);

    }

    public EnumMap<TermExpressionTypeCode, List<ResolvedTermExpression>>
            GetExpressionMaps(Entity entity) {
        if (myTermRule.getOverrides() != null && entity != null) {
            for (TermRuleOverride override : myTermRule.getOverrides()) {
                if (override.getEntityOverrides() == null) {
                    continue;
                }
                if (override.getExpressions() == null || override.getExpressions().size() == 0) {
                    continue;
                }
                for (EntityOverride eo : override.getEntityOverrides()) {
                    if (eo.isMatch(entity)) {
                        //found match..
                        //need to use the override...
                        return BuildExpressionMaps(override.getExpressions());

                    }
                }

            }
        }

        return defaultExpressionMaps;
    }

    private EnumMap<TermExpressionTypeCode, List<ResolvedTermExpression>> BuildExpressionMaps(List<TermExpression> expressions) {
        EnumMap<TermExpressionTypeCode, List<ResolvedTermExpression>> retMaps
                = new EnumMap<>(TermExpressionTypeCode.class);
        for (TermExpression te : expressions) {
            if (!retMaps.containsKey(te.type)) {
                retMaps.put(te.type, new ArrayList<ResolvedTermExpression>());

            }
            ResolvedTermExpression val = ExpressionParsingUtil.BuildResolvedTerm(te);
            if (val != null) {
                retMaps.get(te.type).add(val);
            } else {
                //unsupported type... log message...
            }
        }

        List<TermValidationExpression> valExpressions
                = myTermRule.getValidationExpressions();

        if (valExpressions == null || valExpressions.isEmpty()) {
            return retMaps;
        }

        for (TermValidationExpression tve : valExpressions) {
            TermExpression te = new TermExpression();
            te.expression = tve.expression;

            FormulaResolvedTermExpression resolvedExp = new FormulaResolvedTermExpression(te);

            validationExpressions.add(resolvedExp);
        }

        return retMaps;
    }

    
    public int getDefaultOrderIdForTermRule() {
        ArrayList<String> termNames = myTermRuleService.getTermsUsed(this.myTermRule);
        if (termNames == null || termNames.isEmpty()) {
            return 1; //no dependency...set the order to 1
        }
        int ret = 1;
        for (String termName : termNames) {
            try {
                TermRule tr = this.getTermRuleFromTermName(termName);
                if (tr != null) {

                    if (tr.getOrder() > ret) {
                        ret = tr.getOrder();
                    }
                }
            } catch (Exception exp) {

            }
        }
        return ret + 1;
    }

    public Boolean isTermNameUsedInDefaultFormulas(String name) {
        ArrayList<String> termNames = myTermRuleService.getTermsUsed(this.myTermRule);
        if (termNames == null || termNames.isEmpty()) {
            return false; //no dependency...
        }
        for (String termName : termNames) {
            if (name.equals(termName)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<TermResultsDoc> GetTermResultsByEntityAndTermNames(String entity,
            FormulaResolvedTermExpression expression, TermResultService service) {
        ArrayList<TermResultsDoc> ret = new ArrayList<TermResultsDoc>();

        for (FormulaResolvedTermExpression.TermFormula termFormula : expression.formulas) {
            List<TermResultsDoc> results = service.findbyEntityIdAndTermName(entity, termFormula.termName);
            if (results == null || results.isEmpty()) {
                if (!termFormula.allowNull) {
                    return ret; //why bother with this formula if a required term is not even available,..
                }
                continue;
            }
            ret.addAll(results);
        }

        return ret;
    }

    public ArrayList<TermResultsDoc> GetTermResultsByEntityListAndTermNames(String entityIds,
            FormulaResolvedTermExpression expression, TermResultService service) {
        ArrayList<TermResultsDoc> ret = new ArrayList<TermResultsDoc>();

        for (FormulaResolvedTermExpression.TermFormula termFormula : expression.formulas) {
            String termId = this.getTermIdFromTermName(termFormula.termName);

            List<TermResultsDoc> results = termId == null
                    ? null
                    : service.findbyEntityIdListAndTermId(entityIds, termId);
            if (results == null || results.isEmpty()) {
                if (!termFormula.allowNull) {
                    return ret; //why bother with this formula if a required term is not even available,..
                }
                continue;
            }
            ret.addAll(results);
        }

        return ret;
    }

}
