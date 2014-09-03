package ems.server;

import ems.driver.domain.DriverType;
import ems.driver.domain.common.Location;
import ems.driver.domain.common.Status;
import ems.protocol.domain.ProtocolType;
import ems.server.business.UserManager;
import ems.server.data.DeviceRepository;
import ems.server.data.EmsConfigurationRepository;
import ems.server.data.EventRepository;
import ems.server.data.SpecificationRepository;
import ems.server.domain.*;
import ems.server.utils.DeviceHelper;
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
import java.net.InetAddress;
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
            s1.setDriverType(DriverType.fromValue("probe"));
            s1.setDriver("probe.json");
            s1.setProtocolType(ProtocolType.fromValue("snmp"));
            s1.setProtocol("snmp_protocol.json");
            specificationRepository.save(s1);

            String name = "Device001";
            Status status = Status.OK;
            Device d1 = DeviceHelper.getInstance().createDevice(name, s1, status);
            d1.setAddress("127.0.0.1");
            d1.setPort(1061);
            deviceRepository.save(d1);

            EventHelper.getInstance().addEvents(eventRepository, d1);

            Device d2 = DeviceHelper.getInstance().createDevice("Device002", s1, Status.WARN);
            d2.setAddress("127.0.0.1");
            d2.setPort(1062);
            deviceRepository.save(d2);

            EventHelper.getInstance().addEvents(eventRepository, d2);

            Specification s2 = new Specification();
            s2.setName("AcmeModulator");
            s2.setDriverType(DriverType.fromValue("modulator"));
            s2.setDriver("/drivers/modulator.json");
            s2.setProtocolType(ProtocolType.fromValue("snmp"));
            s2.setProtocol("snmp_protocol.json");
            specificationRepository.save(s2);

            Location l1 = new Location();
            l1.setLatitude(45.0);
            l1.setLongitude(10.0);
            Device d3 = DeviceHelper.getInstance().createDevice("Device003", s2, Status.OK, l1);
            d3.setAddress("127.0.0.1");
            d3.setPort(1063);
            deviceRepository.save(d3);

            EventHelper.getInstance().addEvents(eventRepository, d3);

            Location l2 = new Location();
            l2.setLatitude(45.123);
            l2.setLongitude(8.871);
            Device d4 = DeviceHelper.getInstance().createDevice("Device004", s2, Status.ERROR, l2);
            d4.setAddress("127.0.0.1");
            d4.setPort(1064);
            deviceRepository.save(d4);

            EventHelper.getInstance().addEvents(eventRepository, d4);
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
