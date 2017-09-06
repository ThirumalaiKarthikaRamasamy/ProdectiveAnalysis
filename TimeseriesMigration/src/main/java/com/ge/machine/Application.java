package com.ge.machine;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import com.ge.machine.config.ApplicationInitializer;
@ComponentScan("com.ge.machine")
@SpringBootApplication
@EnableOAuth2Client
//@EnableResourceServer
@EnableAsync(proxyTargetClass=true,mode=AdviceMode.PROXY) 
public class Application extends WebSecurityConfigurerAdapter
{
	@Bean
	@ConfigurationProperties("security.oauth2.client")
	public ClientCredentialsResourceDetails details() {
		return new ClientCredentialsResourceDetails();
	}

	@Bean
	public OAuth2RestTemplate restTemplate(OAuth2ClientContext context, ClientCredentialsResourceDetails details) {
		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(details, context);
		return restTemplate;
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class)
		.initializers(new ApplicationInitializer())
		.run(args);
	}

	public SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		application.initializers(new ApplicationInitializer());
		application.sources(Application.class);
		return application;
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http.headers().frameOptions().disable();
	    http.csrf().disable();
	}
}
