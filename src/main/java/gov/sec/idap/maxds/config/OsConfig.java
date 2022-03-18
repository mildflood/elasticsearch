package gov.sec.idap.maxds.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.opensearch.client.RestHighLevelClient;

public class OsConfig {
	@Value("${opensearch.domain.endpoint}")
    public String osUrl;
	
	@Value("${opensearch.username}")
    public String osUserName;
	
	@Value("${opensearch.password}")
    public String osPassword;

    @Bean
    public RestHighLevelClient opensearchClient() {
    	final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(AuthScope.ANY,
          new UsernamePasswordCredentials(osUserName, osPassword));
        
        RestClientBuilder builder = RestClient.builder(new HttpHost(osUrl, -1, "https"))
          .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
              return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
              });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        
        return client;

    }
}
