package ems.server;

import ems.server.business.UserManager;
import ems.server.data.DeviceRepository;
import ems.server.data.SpecificationRepository;
import ems.server.data.EmsConfigurationRepository;
import ems.server.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * Application
 * Created by thebaz on 25/08/14.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends WebMvcConfigurerAdapter implements CommandLineRunner {
    private static final String[] RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/" };
    @Autowired
    SpecificationRepository specificationRepository;
    @Autowired
    DeviceRepository deviceRepository;
    @Autowired
    EmsConfigurationRepository configurationRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/about").setViewName("mktg");
        registry.addViewController("/contact").setViewName("mktg");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations(
                    RESOURCE_LOCATIONS);
        }
    }

    @Bean
    public ApplicationSecurity applicationSecurity() {
        return new ApplicationSecurity();
    }

    @Bean
    public AuthenticationSecurity authenticationSecurity() {
        return new AuthenticationSecurity();
    }

    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Autowired
        private SecurityProperties security;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/webjars/**", "/about", "/contact").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated();
            http
                    .formLogin()
                    .loginPage("/login")
                    .failureUrl("/login?error")
                    .permitAll()
                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                    .and()
                    .rememberMe();
        }
    }

    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    protected static class AuthenticationSecurity extends
            GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private DataSource dataSource;
        @Autowired
        private UserManager userManager;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userManager).passwordEncoder(userManager.getEncoder());
            auth.jdbcAuthentication().dataSource(dataSource);

            for (EmsUser emsUser : userManager.findAllUsers()) {
                userManager.allocateUser(emsUser);
            }

            EmsUser user = new EmsUser("user", "user", Arrays.asList(new EmsRole("ROLE_USER")));
            user.setName("Normal user");
            if (!userManager.userCreated("user")) {
                userManager.createUser(user);
            }
            else {
                userManager.allocateUser(user);
            }

            EmsUser admin = new EmsUser("admin", "admin", Arrays.asList(new EmsRole("ROLE_ADMIN"), new EmsRole("ROLE_USER")));
            admin.setName("Administrator user");
            if (!userManager.userCreated("admin")) {
                userManager.createUser(admin);
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        if(specificationRepository.count() == 0) {
            Specification specification = new Specification();
            specification.setName("AcmeProbe");
            specification.setType(Type.TYPE_PROBE);
            specificationRepository.save(specification);

            Device d1 = new Device();
            d1.setName("Device001");
            d1.setSpecification(specification);
            deviceRepository.save(d1);

            Device d2 = new Device();
            d2.setName("Device002");
            d2.setSpecification(specification);
            deviceRepository.save(d2);
        }
        if(configurationRepository.count() == 0) {
            configurationRepository.save(new EmsConfigurationEntry("test", "test"));
        }
    }
}
