package com.igniubi.mybatis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 类说明
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
@Configuration
@ConditionalOnProperty(
        prefix = "igniubi.dataSource",
        name = {"masterJdbcUrl"}
)
@ConfigurationProperties(
        prefix = "igniubi.dataSource"
)
public class MasterDataSourceConfigurer {
    private String driverClassName;
    private String masterJdbcUrl;
    private String masterUsername;
    private String masterPassword;
    private int maximumPoolSize;
    private long connectionTimeout;
    private boolean cachePrepStmts;
    private boolean useServerPrepStmts;
    private int prepStmtCacheSize;
    private int prepStmtCacheSqlLimit;

    public MasterDataSourceConfigurer() {
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getMasterJdbcUrl() {
        return this.masterJdbcUrl;
    }

    public void setMasterJdbcUrl(String masterJdbcUrl) {
        this.masterJdbcUrl = masterJdbcUrl;
    }

    public String getMasterUsername() {
        return this.masterUsername;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    public String getMasterPassword() {
        return this.masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public boolean isCachePrepStmts() {
        return this.cachePrepStmts;
    }

    public void setCachePrepStmts(boolean cachePrepStmts) {
        this.cachePrepStmts = cachePrepStmts;
    }

    public boolean isUseServerPrepStmts() {
        return this.useServerPrepStmts;
    }

    public void setUseServerPrepStmts(boolean useServerPrepStmts) {
        this.useServerPrepStmts = useServerPrepStmts;
    }

    public int getPrepStmtCacheSize() {
        return this.prepStmtCacheSize;
    }

    public void setPrepStmtCacheSize(int prepStmtCacheSize) {
        this.prepStmtCacheSize = prepStmtCacheSize;
    }

    public int getPrepStmtCacheSqlLimit() {
        return this.prepStmtCacheSqlLimit;
    }

    public void setPrepStmtCacheSqlLimit(int prepStmtCacheSqlLimit) {
        this.prepStmtCacheSqlLimit = prepStmtCacheSqlLimit;
    }
}

