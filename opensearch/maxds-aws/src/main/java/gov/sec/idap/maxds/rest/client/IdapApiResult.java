/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.rest.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class IdapApiResult {
	@JsonProperty("responseHeader") public IdapResponseHeader responseHeader;
	@JsonProperty("response") public IdapResponseDetail response;
	@JsonProperty("nextCursorMark") public String nextCursorMark;
}
