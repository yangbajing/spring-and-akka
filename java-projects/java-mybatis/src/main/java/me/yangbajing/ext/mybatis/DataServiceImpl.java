package me.yangbajing.ext.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionHolder;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.sql.Statement;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class DataServiceImpl<M extends BaseMapper<T>, T> implements IDataService<T> {

    protected Log log = LogFactory.getLog(getClass());

    @Autowired
    protected M baseMapper;

    @Override
    public M getBaseMapper() {
        return baseMapper;
    }

    protected Class<?> entityClass = currentModelClass();

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     * @deprecated 3.3.1
     */
    @Deprecated
    protected boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    protected Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 1);
    }

    /**
     * 批量操作 SqlSession
     *
     * @deprecated 3.3.0
     */
    @Deprecated
    protected SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(entityClass);
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     * @deprecated 3.3.0
     */
    @Deprecated
    protected void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(entityClass));
    }

    /**
     * 获取 SqlStatement
     *
     * @param sqlMethod ignore
     * @return ignore
     */
    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(entityClass).getSqlStatement(sqlMethod.getMethod());
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Override
    public boolean saveOrUpdate(T entity) {
        if (null != entity) {
            Class<?> cls = entity.getClass();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
            Object idVal = ReflectionKit.getFieldValue(entity, tableInfo.getKeyProperty());
            return StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal)) ? save(entity) : updateById(entity);
        }
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for domain!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from domain!");
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
            if (StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal))) {
                sqlSession.insert(tableInfo.getSqlStatement(SqlMethod.INSERT_ONE.getMethod()), entity);
            } else {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, entity);
                sqlSession.update(tableInfo.getSqlStatement(SqlMethod.UPDATE_BY_ID.getMethod()), param);
            }
        });
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        if (throwEx) {
            return baseMapper.selectOne(queryWrapper);
        }
        return SqlHelper.getObject(log, baseMapper.selectList(queryWrapper));
    }

    @Override
    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(log, baseMapper.selectMaps(queryWrapper));
    }

    @Override
    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(log, listObjs(queryWrapper, mapper));
    }

    /**
     * 执行批量操作
     *
     * @param consumer consumer
     * @since 3.3.0
     * @deprecated 3.3.1 后面我打算移除掉 {@link #executeBatch(Collection, int, BiConsumer)} }.
     */
    @Deprecated
    protected boolean executeBatch(Consumer<SqlSession> consumer) {
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(entityClass);
        SqlSessionHolder sqlSessionHolder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sqlSessionFactory);
        boolean transaction = TransactionSynchronizationManager.isSynchronizationActive();
        if (sqlSessionHolder != null) {
            SqlSession sqlSession = sqlSessionHolder.getSqlSession();
            //原生无法支持执行器切换，当存在批量操作时，会嵌套两个session的，优先commit上一个session
            //按道理来说，这里的值应该一直为false。
            sqlSession.commit(!transaction);
        }
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        if (!transaction) {
            log.warn("SqlSession [" + sqlSession + "] was not registered for synchronization because DataSource is not transactional");
        }
        try {
            consumer.accept(sqlSession);
            //非事物情况下，强制commit。
            sqlSession.commit(!transaction);
            return true;
        } catch (Throwable t) {
            sqlSession.rollback();
            Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
            if (unwrapped instanceof RuntimeException) {
                MyBatisExceptionTranslator myBatisExceptionTranslator
                        = new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), true);
                throw Objects.requireNonNull(myBatisExceptionTranslator.translateExceptionIfPossible((RuntimeException) unwrapped));
            }
            throw ExceptionUtils.mpe(unwrapped);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 执行批量操作
     *
     * @param list      数据集合
     * @param batchSize 批量大小
     * @param consumer  执行方法
     * @param <E>       泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        Assert.isFalse(batchSize < 1, "batchSize must not be less than one");
        return !CollectionUtils.isEmpty(list) && executeBatch(sqlSession -> {
            int size = list.size();
            int i = 1;
            for (E element : list) {
                consumer.accept(sqlSession, element);
                if ((i % batchSize == 0) || i == size) {
                    sqlSession.flushStatements();
                }
                i++;
            }
        });
    }

    /**
     * 执行批量操作（默认批次提交数量{@link IService#DEFAULT_BATCH_SIZE}）
     *
     * @param list     数据集合
     * @param consumer 执行方法
     * @param <E>      泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
        return executeBatch(list, DEFAULT_BATCH_SIZE, consumer);
    }

    @Override
    public int insertBatch(List<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            return 0;
        }
        String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
        List<BatchResult> rets = batchExecute(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
        return rets.stream().mapToInt(result -> Arrays.stream(result.getUpdateCounts()).map(n -> n == Statement.SUCCESS_NO_INFO ? 1 : n).sum()).sum();
    }

    /**
     * 执行批量操作
     *
     * @param list      数据集合
     * @param batchSize 批量大小
     * @param consumer  执行方法
     * @param <E>       泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> List<BatchResult> batchExecute(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        Assert.isFalse(batchSize < 1, "batchSize must not be less than one");
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        final List<BatchResult> results = new LinkedList<>();
        executeBatch(sqlSession -> {
            int size = list.size();
            int i = 1;
            for (E element : list) {
                consumer.accept(sqlSession, element);
                if ((i % batchSize == 0) || i == size) {
                    List<BatchResult> rets = sqlSession.flushStatements();
                    results.addAll(rets);
                }
                i++;
            }
        });
        return results;
    }

}
