package com.igniubi.mybatis.interceptor;

import com.igniubi.common.page.PagerHelper;
import com.igniubi.common.page.PagerInfo;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

/**
 * 拦截器: query操作
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */

@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
)})
public class PagerInfoInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PagerInfoInterceptor.class);
    private static final List<ResultMapping> EMPTY_RESULT_MAPPING = new ArrayList<>(0);
    private static final String SQL_FROM = "FROM";
    private static final String SQL_COUNT = "SELECT COUNT(0) FROM ";
    private Field additionalParametersField;

    public PagerInfoInterceptor() {
    }

    /**
     * 拦截操作
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object var14;
        try {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement)args[0];
            Object parameter = args[1];
            RowBounds rowBounds = (RowBounds)args[2];
            ResultHandler resultHandler = (ResultHandler)args[3];
            Executor executor = (Executor)invocation.getTarget();
            List resultList = null;
            CacheKey cacheKey;
            BoundSql boundSql;
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);

            PagerInfo pager = this.getPagerInfo();
            if (pager == null) {
                return invocation.proceed();
            }

            Map<String, Object> additionalParameters = (Map)this.additionalParametersField.get(boundSql);
            Long count = this.getCount(pager, boundSql, executor, ms, parameter, additionalParameters, resultHandler);
            if (count > 0L) {
                pager.setTotal(count.intValue());
                String pageSql = this.getPageSql(pager, boundSql.getSql(), cacheKey);
                BoundSql pageBound = new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameter);

                for (Object key : additionalParameters.keySet()) {
                    pageBound.setAdditionalParameter((String)key, additionalParameters.get(key));
                }

                resultList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBound);
            } else {
                pager.setTotal(0);
                resultList = new ArrayList(0);
            }

            pager.setList((List)resultList);
            var14 = resultList;
        } finally {
            PagerHelper.clearPage();
        }

        return var14;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        try {
            this.additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
            this.additionalParametersField.setAccessible(true);
        } catch (NoSuchFieldException var3) {
            LOGGER.error("加载additionalParameters属性反射异常", var3);
        }
    }

    private boolean isPager(MappedStatement ms) {
        String id = ms.getId();
        String[] ids = id.split("\\.");
        return ids[ids.length - 1].startsWith("listPager");
    }

    private PagerInfo getPagerInfo() {
        return PagerHelper.getLocalPage();
    }

    private String getCountSql(String sql) {
        String sourceSql = sql.replace("FROM", "from");
        String[] sourceSqlArr = sourceSql.split("from", 2);
        if (sourceSqlArr.length != 2) {
            LOGGER.error("分页查询错误，拼接count语句异常：" + sql);
            return null;
        } else {
            sourceSql = "select count(0) from " + sourceSql.substring(sourceSqlArr[0].length() + 4, sourceSql.length());
            if (sourceSql.matches("([\\s\\S]*)group[\\s\\S]+by([\\s\\S]*)")) {
                sourceSql = "select count(0) from (" + sourceSql + ") _group";
            }

            return sourceSql;
        }
    }

    private String getPageSql(PagerInfo pager, String sql, CacheKey pageKey) {
        int pageNumber = pager.getPageNum();
        int pageSize = pager.getPageSize();
        int totalCount = pager.getTotal();
        if (pageSize <= 0 || pageSize > 1000) {
            pageSize = 20;
        }

        int pageCount = totalCount / pageSize;
        if (totalCount % pageSize > 0) {
            ++pageCount;
        }

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        int startRow = (pageNumber - 1) * pageSize;
        pager.setPageNum(pageNumber);
        pager.setPageSize(pageSize);
        pager.setPages(pageCount);
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        if (startRow == 0) {
            sqlBuilder.append(" LIMIT ");
            sqlBuilder.append(pageSize);
        } else {
            sqlBuilder.append(" LIMIT ");
            sqlBuilder.append(startRow);
            sqlBuilder.append(",");
            sqlBuilder.append(pageSize);
            pageKey.update(startRow);
        }

        pageKey.update(pageSize);
        return sqlBuilder.toString();
    }

    private MappedStatement createCountMappedStatement(MappedStatement ms) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId() + "_COUNT", ms.getSqlSource(), ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            String[] var4 = ms.getKeyProperties();
            int var5 = var4.length;

            for (String keyProperty : var4) {
                keyProperties.append(keyProperty).append(",");
            }

            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        List<ResultMap> resultMaps = new ArrayList<>();
        ResultMap resultMap = (new org.apache.ibatis.mapping.ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class, EMPTY_RESULT_MAPPING)).build();
        resultMaps.add(resultMap);
        builder.resultMaps(resultMaps);
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    private Long getCount(PagerInfo pagerInfo, BoundSql boundSql, Executor executor, MappedStatement ms, Object parameter, Map<String, Object> additionalParameters, ResultHandler resultHandler) throws SQLException {
        MappedStatement countMappedStatement = null;
        BoundSql countBoundSql = null;
        String countMapperId = pagerInfo.getTotalMapperId();
        if (countMapperId != null && !countMapperId.equals("")) {
            try {
                countMappedStatement = ms.getConfiguration().getMappedStatement(countMapperId, false);
            } catch (Throwable var15) {
                LOGGER.error("%s MappedStatement不存在", countMapperId);
                return 0L;
            }

            countBoundSql = countMappedStatement.getBoundSql(parameter);
        } else {
            String countSql = this.getCountSql(boundSql.getSql());
            countMappedStatement = this.createCountMappedStatement(ms);
            countBoundSql = new BoundSql(ms.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);
        }

        CacheKey countKey = executor.createCacheKey(ms, parameter, RowBounds.DEFAULT, boundSql);
        countKey.update("_Count");

        for (Object key : additionalParameters.keySet()) {
            countBoundSql.setAdditionalParameter((String)key, additionalParameters.get(key));
        }

        Object countResultObj = executor.query(countMappedStatement, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);
        Long count = 0L;
        if (countResultObj != null) {
            List countResultList = (List)countResultObj;
            if (countResultList.size() > 0 && countResultList.get(0) != null) {
                count = (Long)countResultList.get(0);
            }
        }

        return count;
    }
}