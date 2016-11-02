package org.fer.syncfiles.config;

import org.fer.syncfiles.security.XAuthTokenConfigurer;
import org.fer.syncfiles.security.hmac.HmacRequester;
import org.fer.syncfiles.security.hmac.HmacSecurityConfigurer;
import org.fer.syncfiles.services.AuthenticationService;
import org.fer.syncfiles.services.HmacUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration
 * Created by Michael DESIGAUD on 14/02/2016.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private HmacRequester hmacRequester;

    @Autowired
    private HmacUserDetailsService hmacUserDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/vendor/**")
                .antMatchers("*.{ico}")
                .antMatchers("*.{html}")
                .antMatchers("*.{css}")
                .antMatchers("*.{js}")
                .antMatchers("/app/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/api/config/**").permitAll()
                    .antMatchers("/websocket/**").permitAll()
                    .antMatchers("/api/authenticate").anonymous()
                    .antMatchers("/").anonymous()
                    .antMatchers("/favicon.ico").anonymous()
                    .antMatchers("/api/**").permitAll()
//                    .antMatchers("/api/**").authenticated()
                .and()
                .csrf()
                    .disable()
                    .headers()
                    .frameOptions().disable()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout()
                    .permitAll()
                .and()
                    .apply(authTokenConfigurer())
                .and()
                    .apply(hmacSecurityConfigurer());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hmacUserDetailsService);


//        InMemoryUserDetailsManagerConfigurer configurer = auth
//                .inMemoryAuthentication()
//                    .passwordEncoder(passwordEncoder());
//
//        for(UserDTO userDTO : MockUsers.getUsers()) {
//            configurer.withUser(userDTO.getLogin())
//                    .password(passwordEncoder().encode(userDTO.getPassword()))
//                    .roles(userDTO.getProfile().name());
//        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private HmacSecurityConfigurer hmacSecurityConfigurer(){
        return new HmacSecurityConfigurer(hmacRequester);
    }

    private XAuthTokenConfigurer authTokenConfigurer(){
        return new XAuthTokenConfigurer(authenticationService);
    }
}
