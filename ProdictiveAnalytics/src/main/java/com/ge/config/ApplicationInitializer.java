package com.ge.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;

public class ApplicationInitializer implements ApplicationContextInitializer<AnnotationConfigEmbeddedWebApplicationContext> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void initialize(
			AnnotationConfigEmbeddedWebApplicationContext applicationContext) {
		Cloud cloud = getCloud();
		ConfigurableEnvironment appEnvironment = applicationContext
				.getEnvironment();
		if (cloud != null) {
			appEnvironment.addActiveProfile("cloud");
			log.info("Cloud profile active");
		}else{
			log.info("Local Profile Active");
			System.setProperty("http.proxyHost", "cis-india-pitc-bangalorez.proxy.corporate.ge.com"); 
			System.setProperty("http.proxyPort", "80"); 
			System.setProperty("https.proxyHost", "cis-india-pitc-bangalorez.proxy.corporate.ge.com"); 
			System.setProperty("https.proxyPort", "80");
		}
	}

	private Cloud getCloud() {
		try {
			CloudFactory cloudFactory = new CloudFactory();
			return cloudFactory.getCloud();
		} catch (CloudException ce) {
			return null;
		}
	}

}
