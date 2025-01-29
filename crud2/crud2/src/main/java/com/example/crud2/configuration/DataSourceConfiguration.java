package com.example.crud2.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class DataSourceConfiguration {
	
    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/postgres");
        dataSourceBuilder.username("postgres");
        dataSourceBuilder.password("root");
        return dataSourceBuilder.build();
    }
    
    @Bean 
    public SqlSessionFactory sqlSessionFactory() throws Exception { 
    	SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean(); 
    	sessionFactory.setDataSource(getDataSource());  
    	Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"); 
    	sessionFactory.setMapperLocations(res);
    	return sessionFactory.getObject();
    }
}
