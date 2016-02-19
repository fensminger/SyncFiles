package org.fer.syncfiles.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * User: wilfried Date: 29/12/13 Time: 22:21
 */

@Configuration
@Profile("prod")
@PropertySource("classpath:application-prod.properties")
public class ApplicationProdProperties {


}
