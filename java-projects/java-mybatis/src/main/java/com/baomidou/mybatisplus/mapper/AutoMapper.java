package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author yangjing (yang.xunjing@qq.com)
 * @date 2020-07-23 23:00
 * @deprecated 直接实现 {@link BaseMapper} 接口，v3.6 时删除！
 */
@Deprecated
public interface AutoMapper<T> extends BaseMapper<T> {
    /**
     * @deprecated Use the {@link #updateById(T)} method instead
     */
    @Deprecated
    default int updateSelectiveById(T entity) {
        return updateById(entity);
    }

    /**
     * @deprecated Use the {@link #insert(T)} method instead
     */
    @Deprecated
    default int insertSelective(T entity) {
        return insert(entity);
    }
}
