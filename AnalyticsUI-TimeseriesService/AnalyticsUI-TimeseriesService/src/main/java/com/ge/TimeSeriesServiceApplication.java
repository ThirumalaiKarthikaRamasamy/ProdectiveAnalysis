package com.ge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@SpringBootApplication
@EnableOAuth2Client
public class TimeSeriesServiceApplication {

	@Value("${brilliantLab.timeseries.zoneId}")
	String zoneId;

	@Bean
	@ConfigurationProperties("security.oauth2.client")
	public ClientCredentialsResourceDetails details() {
		return new ClientCredentialsResourceDetails();
	}

	@Bean
	public OAuth2RestTemplate restTemplate(OAuth2ClientContext context, ClientCredentialsResourceDetails details) {
		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(details, context);
		restTemplate.getInterceptors().add(headersAddingInterceptor());
		return restTemplate;
	}

	public ClientHttpRequestInterceptor headersAddingInterceptor() {
		return (request, body, execution) -> {
			request.getHeaders().set("Predix-Zone-Id", zoneId);
			return execution.execute(request, body);
		};
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("u").password("p").roles("USER", "ADMIN");
	}

	public static void main(String[] args) {
		System.setProperty("http.proxyHost", "sjc1intproxy01.crd.ge.com"); 
		System.setProperty("http.proxyPort", "8080"); 
		System.setProperty("https.proxyHost", "sjc1intproxy01.crd.ge.com"); 
		System.setProperty("https.proxyPort", "8080");
		SpringApplication.run(TimeSeriesServiceApplication.class, args);
	}
}
