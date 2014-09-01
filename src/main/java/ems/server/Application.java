package ems.server;

import ems.driver.common.Location;
import ems.driver.common.Status;
import ems.server.business.UserManager;
import ems.server.data.DeviceRepository;
import ems.server.data.EmsConfigurationRepository;
import ems.server.data.EventRepository;
import ems.server.data.SpecificationRepository;
import ems.server.domain.*;
import ems.server.utils.EventHelper;
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
            "classpath:/static/", "classpath:/public/"};
    @Autowired
    EventRepository eventRepository;
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
        registry.addViewController("/mktg/about").setViewName("mktg");
        registry.addViewController("/mktg/contact").setViewName("mktg");
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

    @Override
    public void run(String... args) throws Exception {
        eventRepository.deleteAll();
        deviceRepository.deleteAll();
        specificationRepository.deleteAll();
        if (specificationRepository.count() == 0) {
            Specification s1 = new Specification();
            s1.setName("AcmeProbe");
            s1.setType(Type.TYPE_PROBE);
            s1.setDriver("/drivers/probe.json");
            specificationRepository.save(s1);

            Device d1 = new Device();
            d1.setName("Device001");
            d1.setStatus(Status.OK);
            d1.setSpecification(s1);
            deviceRepository.save(d1);

            EventHelper.addEvents(eventRepository, d1);

            Device d2 = new Device();
            d2.setName("Device002");
            d2.setStatus(Status.WARN);
            d2.setSpecification(s1);
            deviceRepository.save(d2);

            EventHelper.addEvents(eventRepository, d2);

            Specification s2 = new Specification();
            s2.setName("AcmeModulator");
            s2.setType(Type.TYPE_MODULATOR);
            s2.setDriver("/drivers/modulator.json");
            specificationRepository.save(s2);

            Device d3 = new Device();
            d3.setName("Device003");
            d3.setStatus(Status.OK);
            d3.setSpecification(s2);
            d3.setLocation(new Location(45.0, 10.0));
            deviceRepository.save(d3);

            EventHelper.addEvents(eventRepository, d3);

            Device d4 = new Device();
            d4.setName("Device004");
            d4.setStatus(Status.ERROR);
            d4.setSpecification(s2);
            d4.setLocation(new Location(45.123, 8.971));
            deviceRepository.save(d4);

            EventHelper.addEvents(eventRepository, d4);
        }
        configurationRepository.deleteAll();
        if (configurationRepository.count() == 0) {
            configurationRepository.save(new EmsConfigurationEntry("map_latitude", 41.28348));
            configurationRepository.save(new EmsConfigurationEntry("map_longitude", 10.52626));
        }
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
            } else {
                userManager.allocateUser(user);
            }

            EmsUser admin = new EmsUser("admin", "admin", Arrays.asList(new EmsRole("ROLE_ADMIN"), new EmsRole("ROLE_USER")));
            admin.setName("Administrator user");
            if (!userManager.userCreated("admin")) {
                userManager.createUser(admin);
            }
        }
    }
}
