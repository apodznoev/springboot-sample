package de.avpod.sampleboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Created by apodznoev
 * date 21.11.2017.
 */
    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(securedEnabled = true)
    public class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    //@formatter:off
                .ldapAuthentication()
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .contextSource()
                .url("ldap://localhost:8389/dc=springframework,dc=org")
                .ldif("classpath:test-server.ldif")
            .and()
                .passwordCompare()
                .passwordEncoder(new LdapShaPasswordEncoder())
                .passwordAttribute("userPassword");
                //@formatter:on
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    //@formatter:off
                    .authorizeRequests()
                    .antMatchers("/**/hello", "/**/login")
                    .permitAll()
            .and()
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
            .and()
                    .formLogin()
                    .failureForwardUrl("/errorAuth.html")
                    .defaultSuccessUrl("/index.html")
            .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .and()
                    .csrf()
                    .disable();
            //@formatter:on
        }
    }