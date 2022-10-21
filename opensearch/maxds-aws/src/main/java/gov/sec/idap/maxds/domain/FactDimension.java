/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

public class FactDimension
{
       public String dimName;
     //  public String dimLabel ;
       public String memberName;
     //  public String memberLabel;
       
       
       public String toString()
       {
           return String.format("%s@%s", memberName, dimName);
       }
}

