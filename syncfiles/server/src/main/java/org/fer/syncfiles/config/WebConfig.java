package org.fer.syncfiles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
//EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/api/hdfs/**")
//			.allowedOrigins("http://localhost:4100")
//			.allowedMethods("POST", "GET", "PUT", "DELETE")
//			//.allowedHeaders("header1", "header2", "header3")
//			//.exposedHeaders("header1", "header2")
//			.allowCredentials(false).maxAge(3600);
//	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/app/**").addResourceLocations("/app/");
		registry.addResourceHandler("/assets/**").addResourceLocations("/assets/");
		registry.addResourceHandler("/*.html").addResourceLocations("/");
		registry.addResourceHandler("/*.ico").addResourceLocations("/");
		registry.addResourceHandler("/*.css").addResourceLocations("/");
		registry.addResourceHandler("/*.js").addResourceLocations("/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/detail/**").setViewName("forward:/index.html");
		registry.addViewController("/running/**").setViewName("forward:/index.html");
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("http://localhost:4100");
		config.addAllowedOrigin("http://91.121.112.80");
		config.addAllowedOrigin("http://127.0.0.1");
		config.addAllowedOrigin("http://educappli.fr");
		config.addAllowedOrigin("http://www.educappli.fr");
		config.addAllowedOrigin("http://syncfiles.educappli.fr");
		config.addAllowedOrigin("*");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("DELETE");
		config.setAllowCredentials(false);
		config.setMaxAge(3600L);
		if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
//			log.debug("Registering CORS filter");
			source.registerCorsConfiguration("/api/**", config);
			source.registerCorsConfiguration("/v2/api-docs", config);
		}
		return new CorsFilter(source);
	}


}
