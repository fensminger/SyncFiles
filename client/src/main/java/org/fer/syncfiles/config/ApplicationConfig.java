package org.fer.syncfiles.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * User: wilfried Date: 29/12/13 Time: 15:37
 */
@Configuration
@Import({ApplicationProdProperties.class, ApplicationTestProperties.class, ScreensConfiguration.class})
// select according to profile ...
public class ApplicationConfig {


}
