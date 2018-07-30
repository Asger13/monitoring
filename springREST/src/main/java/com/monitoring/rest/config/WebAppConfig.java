package com.monitoring.rest.config;

import javax.sql.DataSource;

import com.monitoring.rest.DAO.InterfaceDAO;
import com.monitoring.rest.DAO.InterfaceDAOImpl;
import com.monitoring.rest.DAO.LastCoordinateDAO;
import com.monitoring.rest.DAO.LastCoordinateDAOImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;


@Configuration
@EnableWebMvc
@ComponentScan("com.monitoring.rest")
public class WebAppConfig {

    @Bean
    public UrlBasedViewResolver setupViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/pages/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);

        return resolver;
    }

    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5433/monitoring");
        dataSource.setUsername("postgres");
        dataSource.setPassword("1");

        return dataSource;
    }

    @Bean
    public LastCoordinateDAO getLastCoordinateDAO() {
        return new LastCoordinateDAOImpl(getDataSource());
    }

    @Bean
    public InterfaceDAO getInterfaceDAO() {
        return new InterfaceDAOImpl(getDataSource());
    }


}
