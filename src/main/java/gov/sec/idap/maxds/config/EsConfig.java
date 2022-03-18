package gov.sec.idap.maxds.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
//import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "gov.sec.idap.maxds.elasticsearch.repository")
@ComponentScan(basePackages = { "gov.sec.idap.maxds.elasticsearch" })
//public class EsConfig {
public class EsConfig extends AbstractElasticsearchConfiguration {

	@Value("${elasticsearch.url}")
    public String elasticsearchUrl;
	
	@Value("${elasticsearch.username}")
    public String elasticsearchUserName;
	
	@Value("${elasticsearch.password}")
    public String elasticsearchPassword;


	// OpenSearch
	@Value("${opensearch.domain.endpoint}")
	public String osUrl;

	@Value("${opensearch.username}")
	public String osUserName;

	@Value("${opensearch.password}")
	public String osPassword;

	@Bean
	@Override
	public RestHighLevelClient elasticsearchClient() {
		final ClientConfiguration config = ClientConfiguration.builder().connectedTo(osUrl + ":443").usingSsl()
				.withBasicAuth(osUserName, osPassword).build();
		
		return RestClients.create(config).rest();
	}

//	@Bean
//	public RestHighLevelClient opensearchClient() {
//		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//		
//		System.out.println("End point: " + osUrl);
//		System.out.println("User Name: " + osUserName);
//		System.out.println("Password: " + osPassword);
//
//		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(osUserName, osPassword));
//
//		RestClientBuilder builder = RestClient.builder(new HttpHost(osUrl, -1, "https"))
//				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//					@Override
//					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//					}
//				});
//		RestHighLevelClient client = new RestHighLevelClient(builder);
//
//		return client;
//	}

}
