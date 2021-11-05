/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.calculation.Domain;

import java.util.ArrayList;


/**
 *
 * @author SRIKANTH
 */
public class ExtendedCheckResolvedTermExpression extends ResolvedTermExpression {

    public String balanceType;
    public String periodType;
    public Boolean isShareItemType;
    public ArrayList<String> containsWords = new ArrayList<>();
    public ArrayList<String> doesNotContainsWords = new ArrayList<>();

    private String solrElementQueryString = null;

    //cell_elt:/.*(ASSET).*/ AND -cell_elt:/.*Liabilit.*/ AND -cell_elt:/.*deriv.*/ AND -cell_elt:/.*loan.*/ AND -cell_elt:/.*fut.*/ AND -cell_elt:/.*good.*/
    public String buildSolrQueryPartForElementName() {
        if (solrElementQueryString == null) {

            StringBuilder bld = new StringBuilder();
            bld.append(buildSolrRegex(this.containsWords, ""));
            bld.append(buildSolrRegex(this.doesNotContainsWords, "-"));

            solrElementQueryString = bld.toString();
        }

        return solrElementQueryString;
    }

    private String buildSolrRegex(ArrayList<String> terms,
            String prefix) {
        if (terms == null || terms.isEmpty()) {
            return "";
        }

        StringBuilder bld = new StringBuilder();

        for (String term : terms) {
            bld.append(String.format(" AND %scell_elt:/.*%s.*/", prefix, term));
        }

        return bld.toString();

    }

}
