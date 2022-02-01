/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.rest.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @param <T> Response from Rest Client
 */
public class SecRestClient<T> {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public SecRestClient() {

    }

    public List<T> doGetListFromIdap(Class<T> responseTypeClass,
            String url) {

        LOG.debug("Url : " + url);

        HttpHeaders requestHeaders = new HttpHeaders();

        HttpEntity<IdapApiResult> requestEntity
                = new HttpEntity<>(null, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<IdapApiResult> responseType
                = new ParameterizedTypeReference<IdapApiResult>() {
        };

        ResponseEntity<IdapApiResult> restResponse = restTemplate.exchange(url,
                HttpMethod.GET, requestEntity, responseType);

        ObjectMapper objectMapper = new ObjectMapper();

        IdapApiResult apiResult = restResponse.getBody();

        List<T> ret = new ArrayList<>();

        if (apiResult != null && apiResult.response != null && apiResult.response.docs != null) {

            apiResult.response.docs.forEach((doc) -> {

                ret.add((T) objectMapper.convertValue(doc, responseTypeClass));

            });

        }

        return ret;
    }

    public IdapGenericApiResult getIdapResult(String url) {

        LOG.debug("Url : " + url);

        HttpHeaders requestHeaders = new HttpHeaders();

        HttpEntity<IdapApiResult> requestEntity
                = new HttpEntity<>(null, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<IdapGenericApiResult> responseType
                = new ParameterizedTypeReference<IdapGenericApiResult>() {
        };

        URI uri = URI.create(url);

        ResponseEntity<IdapGenericApiResult> restResponse = restTemplate.exchange(uri,
                HttpMethod.GET, requestEntity, responseType);

        return restResponse.getBody();

    }

    public String getIdapResultString(String url) {

        LOG.debug("Url : " + url);

        HttpHeaders requestHeaders = new HttpHeaders();

        HttpEntity<String> requestEntity
                = new HttpEntity<>(null, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<String> responseType
                = new ParameterizedTypeReference<String>() {
        };
        URI uri = URI.create(url);
        ResponseEntity<String> restResponse = restTemplate.exchange(uri,
                HttpMethod.GET, requestEntity, responseType);

        return restResponse.getBody();

    }

    public T doGet(Class<T> responseTypeClass,
            String url, String... pathVariables) {

        LOG.debug("Url : " + url);

        HttpHeaders requestHeaders = new HttpHeaders();

        HttpEntity<T> requestEntity
                = new HttpEntity<>(null, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<T> responseType
                = new ParameterizedTypeReference<T>() {
        };

        ResponseEntity<T> restResponse = restTemplate.exchange(url,
                HttpMethod.GET, requestEntity,
                responseType, (Object[]) pathVariables);

        ObjectMapper objectMapper = new ObjectMapper();

        return (T) objectMapper.convertValue(restResponse.getBody(),
                responseTypeClass);
    }

    public List<T> doGetList(Class<T> responseTypeClass,
            String url, String... pathVariables) {

        LOG.debug("Url : " + url);
        List<T> list = new ArrayList<>();
        HttpHeaders requestHeaders = new HttpHeaders();

        HttpEntity<T> requestEntity = new HttpEntity<>(null, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<List<T>> responseType
                = new ParameterizedTypeReference<List<T>>() {
        };

        ResponseEntity<List<T>> restResponse = restTemplate.exchange(url,
                HttpMethod.GET, requestEntity, responseType,
                (Object[]) pathVariables);

        ObjectMapper objectMapper = new ObjectMapper();
        for (Object o : restResponse.getBody()) {
            list.add((T) objectMapper.convertValue(o, responseTypeClass));
        }
        return list;
    }

    
}
