package de.avpod.sampleboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
@SpringBootApplication
public class Application {

    @Autowired
    CustomRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping("/hello")
    public String helloWorld(@RequestParam(value = "name", required = false) String name) {
        return "Hello " + name + "!";
    }

    @RequestMapping("/findCustomerCard")
    public int findCardNumber(@RequestParam(value = "fullName") String name) {
        Customer customer = repository.findByFullName(name);
        if (customer != null)
            return customer.cardNumber;
        return -1;
    }

    @PreAuthorize("hasRole('ROLE_DEVELOPERS')")
    @RequestMapping(value = "/persistCustomer", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public CustomerResponse postSomething(Customer entity) {
        Customer savedEntity = repository.save(entity);
        return new CustomerResponse(savedEntity.fullName + "_" + savedEntity.id);
    }


    public class CustomerResponse {
        private final String responseText;

        CustomerResponse(String response) {
            this.responseText = response;
        }

        public String getResponseText() {
            return responseText;
        }

    }


    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(securedEnabled = true)
    public class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth
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
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
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
        }

    }
}
