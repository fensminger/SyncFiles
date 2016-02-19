package org.fer.syncfiles.server;

import org.fer.syncfiles.service.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//@SpringBootApplication
//@RestController
//@ComponentScan(basePackages = {"org.fer.syncfiles"})
//@EnableAutoConfiguration(exclude = {JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class, SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class EmbeddedServer {

//	@RequestMapping("/")
//	String home() {
//		return "Hello World!";
//	}
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(EmbeddedServer.class);
//    }

	/**
	 * @param args
	 * @throws Exception
	 */
//	public static void main(String[] args) throws Exception {
//		SpringApplication.run(EmbeddedServer.class, args);
//		System.setProperty("https.protocols", "TLSv1");

//		SpringApplication app = new SpringApplication(EmbeddedServer.class);
//		app.setShowBanner(false);

//		app.run(args);

		// set port to 9999
//	}

}
