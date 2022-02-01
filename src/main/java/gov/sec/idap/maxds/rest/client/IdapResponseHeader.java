/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.rest.client;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdapResponseHeader {
	@JsonProperty("status") private Long status;
	@JsonProperty("QTime") private Long qtime;
	//@JsonAnySetter private final Map<String, Object> params = new HashMap<>();
	@JsonProperty("info") private Map<String, Object> info = new HashMap<>();
        @JsonProperty("params") private Map<String, Object> params = new HashMap<>();
}
