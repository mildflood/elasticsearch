/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation;

import gov.sec.idap.maxds.calculation.Domain.ExtendedCheckResolvedTermExpression;
import gov.sec.idap.maxds.domain.Entity;
import gov.sec.idap.maxds.domain.NormalizedFact;
import gov.sec.idap.maxds.service.NormalizedFactService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author SRIKANTH
 */
public class ExtendedCheckProcessor {
    
        NormalizedFactService myNormalizedFactService;

     HashMap<String, HashMap<String, List<NormalizedFact>>> cache;
             
    
    public ExtendedCheckProcessor(
            NormalizedFactService ns)
    {
      
        this.myNormalizedFactService = ns;
        cache = new HashMap<>();
        
    }
    
    public List<NormalizedFact> getFactByEntityList( HashMap<String, Entity> myEntityList,
            ExtendedCheckResolvedTermExpression exp)
    {
        loadCacheforExpression(exp);
        
        List<NormalizedFact> ret = new ArrayList<>();
        
        HashMap<String, List<NormalizedFact>> factsMap = cache.get(exp.expression.expression);
        if( factsMap == null  ||  factsMap.isEmpty() ) return ret;
        for( String entityId: myEntityList.keySet())
        {
            if( factsMap.containsKey(entityId))
            {
                ret.addAll(factsMap.get(entityId));
            }
        }
        return ret;
    }
    
    private void loadCacheforExpression(ExtendedCheckResolvedTermExpression cte)
    {
        if( cache.containsKey(cte.expression.expression)) return;
        
         List<NormalizedFact> facts = 
                myNormalizedFactService.findValueInfoByExtendedElements( cte);
         
        
         cache.put(cte.expression.expression, buildFactsHash(facts));
    }
    
    private HashMap<String, List<NormalizedFact>> buildFactsHash( List<NormalizedFact> facts)
    {
        HashMap<String, List<NormalizedFact>> ret= new HashMap<>();
        
        for( NormalizedFact fact : facts)
        {
            String cikString = fact.getCikString();
            if( !ret.containsKey(cikString))
            {
                ret.put( cikString, new ArrayList<>());
            }
            
            
            ret.get(cikString).add(fact);
        }
        return ret;
    }
}
