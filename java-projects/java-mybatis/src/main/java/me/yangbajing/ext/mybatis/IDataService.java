package me.yangbajing.ext.mybatis;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IDataService<T> extends IService<T> {
    int insertBatch(List<T> entityList, int batchSize);

    default boolean insert(T entity) {
        return save(entity);
    }
}
