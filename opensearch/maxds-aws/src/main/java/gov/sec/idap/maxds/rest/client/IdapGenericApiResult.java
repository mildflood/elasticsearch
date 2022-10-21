/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.rest.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdapGenericApiResult {

   // @JsonProperty("facet_counts")  public IdapFacetResult facet_counts;
    @JsonProperty("facet_counts")
    public Map<String, Object> facet_counts;
    @JsonProperty("responseHeader")
    public Map<String, Object> responseHeader;
    @JsonProperty("response")
    public Map<String, Object> response;
    @JsonProperty("nextCursorMark")
    public Map<String, Object> nextCursorMark;
    @JsonProperty("highlighting")
    public Map<String, Object> highlighting;
}
