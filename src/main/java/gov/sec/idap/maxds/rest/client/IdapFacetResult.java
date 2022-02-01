/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.rest.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class IdapFacetResult {
   
        @JsonProperty("facet_queries") public IdapFacetPivot facet_queries;
        @JsonProperty("facet_fields") public IdapFacetPivot facet_fields;
        @JsonProperty("facet_ranges") public IdapFacetPivot facet_ranges;
        @JsonProperty("facet_intervals") public IdapFacetPivot facet_intervals;
        @JsonProperty("facet_heatmaps") public IdapFacetPivot facet_heatmaps;
        @JsonProperty("facet_pivot") public IdapFacetPivot facet_pivot;
        
     
}
