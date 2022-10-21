/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation.Domain;


import gov.sec.idap.maxds.domain.TermExpressionTypeCode;
import gov.sec.idap.maxds.elasticsearch.document.Entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author srika
 */
public class EntityGroupedByExpressionMap {
    
    public List<Entity> entityList = new ArrayList<>();
    public EnumMap<TermExpressionTypeCode, List<ResolvedTermExpression>> expressionMaps 
            = new EnumMap<>(TermExpressionTypeCode.class);
    
}
