/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.ArrayList;


public class TermRuleStatistics {
    
    
    public int totalEntityCount;
    public String coverage;
    public int mappedEntityCount;
    public int unMappedEntityCount;
    public ArrayList<TermRuleStatisticsByRank> resultsByRank = new ArrayList<>();
    
    public static class TermRuleStatisticsByRank
    {
       public int rankId;
       public int mappedEntityCount; 
       public int nonDistinctMappedEntityCount;
    }
    
    public static TermRuleStatisticsByRank CreateTermRuleStatisticsByRank(int rankId)
    {
        TermRuleStatisticsByRank data = new TermRuleStatisticsByRank();
        data.rankId = rankId;
        return data;
    }
    
    public void AddStatiticsForRank( int rankId, int count)
    {
        TermRuleStatisticsByRank data = new TermRuleStatisticsByRank();
        data.rankId = rankId;
        data.mappedEntityCount=count;
        resultsByRank.add(data);
    }
    
   
}
