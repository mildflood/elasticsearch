/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation.Domain;

/**
 *
 * @author srika
 */
public class FYFQInfo {
     
      public String entityId;
      public int FY;
      public  String FQ;
        
        public FYFQInfo(int fy , String fq)
        {
            this.FY = fy;
            this.FQ = fq;
        }
        
         public FYFQInfo(String e, int fy , String fq)
        {
            this.entityId = e;
            this.FY = fy;
            this.FQ = fq;
        }
}
