/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author srika
 */
public class GroupTermMapInformation implements Comparator<GroupTermMapInformation> {
    
    
    @Override
    public int compare(GroupTermMapInformation o1, GroupTermMapInformation o2) {
        
        
        if( o1.termId == null) return 1;
        if( o2.termId ==  null ) return -1;
        return  o1.termId.compareTo(o2.termId);
    }
    
    public String termId;
    public String name;
    public String description;
      
    public Boolean isEditing = false;
    public Boolean isTermRule = true;
    
    public List<TermMapInformation> mappedInfoSets;
    
}
