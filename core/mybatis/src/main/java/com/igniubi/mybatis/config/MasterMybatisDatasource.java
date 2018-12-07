package com.igniubi.mybatis.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 公共数据源(主)
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
@Configuration
@EnableConfigurationProperties({MasterDataSourceConfigurer.class})
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
@ConditionalOnBean(MasterDataSourceConfigurer.class)
@MapperScan(value = "com.igniubi.*.mapper",
  sqlSessionFactoryRef="masterSqlSessionFactory")

public class MasterMybatisDatasource {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterMybatisDatasource.class);
    public static final String MASTER_TRANSACTION_MANAGER = "masterTransactionManager";
    @Autowired
    private MasterDataSourceConfigurer masterDataSourceConfigurer;

    public MasterMybatisDatasource() {
    }

    @Primary
    @Bean(
        name = {"masterSqlSessionFactory"}
    )
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource dataSource) throws Exception {
        LOGGER.info("====== masterSqlSessionFactory init ======");
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:mybatis-config.xml");
        if (resources != null && resources.length > 0) {
            factory.setConfigLocation(resources[0]);
        }
        //设置mapper文件读取位置
        Resource[] mapperResources = resolver.getResources("classpath:mapper/*.xml");
        factory.setMapperLocations(mapperResources);
        return factory.getObject();
    }

    @Primary
    @Bean(
        name = {"masterDataSource"}
    )
    public DataSource masterDataSource() {
        LOGGER.info("====== masterDataSource init ======");
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(this.masterDataSourceConfigurer.getDriverClassName());
        ds.setMaximumPoolSize(this.masterDataSourceConfigurer.getMaximumPoolSize());
        ds.setConnectionTimeout(this.masterDataSourceConfigurer.getConnectionTimeout());
        ds.setJdbcUrl(this.masterDataSourceConfigurer.getMasterJdbcUrl());
        ds.setUsername(this.masterDataSourceConfigurer.getMasterUsername());
        ds.setPassword(this.masterDataSourceConfigurer.getMasterPassword());
        ds.setMinimumIdle(5);
        ds.addDataSourceProperty("maxLifetime", Integer.valueOf(60000));
        ds.addDataSourceProperty("cachePrepStmts", this.masterDataSourceConfigurer.isCachePrepStmts());
        ds.addDataSourceProperty("prepStmtCacheSize", this.masterDataSourceConfigurer.getPrepStmtCacheSize());
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", this.masterDataSourceConfigurer.getPrepStmtCacheSqlLimit());
        ds.addDataSourceProperty("useServerPrepStmts", this.masterDataSourceConfigurer.isUseServerPrepStmts());
        return ds;
    }

    @Primary
    @Bean(
        name = {"masterTransactionManager"}
    )
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(this.masterDataSource());
    }
}

