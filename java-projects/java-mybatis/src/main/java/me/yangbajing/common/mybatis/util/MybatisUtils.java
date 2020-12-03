//package me.yangbajing.common.mybatis.util;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.core.metadata.OrderItem;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import me.yangbajing.common.model.DefaultPageQuery;
//import me.yangbajing.common.model.OrderByItem;
//import me.yangbajing.common.model.PageQuery;
//import me.yangbajing.project.PageResult;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * @author yangjing (yang.xunjing@qq.com)
// * @date 2020-07-26 16:37
// */
//public interface MybatisUtils {
//    static <T> Page<T> getPage(HttpServletRequest request) {
//        return getPage(request, PageQuery.DEFAULT_SIZE);
//    }
//
//    static <T> Page<T> getPage(HttpServletRequest request, int size) {
//        int _size = size, _index = 1;
//        if (request.getParameter("_size") != null) {
//            _size = Integer.parseInt(request.getParameter("_size"));
//        }
//        if (request.getParameter("_index") != null) {
//            _index = Integer.parseInt(request.getParameter("_index"));
//        }
//        return new Page<>(_index, _size);
//    }
//
//    static <T> Page<T> toPage(PageQuery query) {
//        List<OrderItem> orders = query.getOrderBys().stream()
//                .map(by -> by.isAsc() ? OrderItem.asc(by.getColumn()) : OrderItem.desc(by.getColumn()))
//                .collect(Collectors.toList());
//        return new Page<T>(query.getCurrent(), query.getSize()).addOrder(orders);
//    }
//
//    static <T> PageQuery toPageQuery(IPage<T> page) {
//        DefaultPageQuery query = new DefaultPageQuery();
//        query.setCurrent((int) page.getCurrent());
//        query.setSize((int) page.getSize());
//        List<OrderByItem> orders = page.orders().stream()
//                .map(item -> {
//                    OrderByItem by = new OrderByItem();
//                    by.setAsc(item.isAsc());
//                    by.setColumn(item.getColumn());
//                    return by;
//                })
//                .collect(Collectors.toList());
//        query.addOrderBy(orders);
//        return query;
//    }
//
//    static <T> PageResult<T> toPageResult(IPage<T> page) {
//        if (page == null) {
//            return PageResult.create();
//        }
//
//        return PageResult.create((int) page.getCurrent(), (int) page.getSize(), (int) page.getPages(), (int) page.getTotal(), page.getRecords(), null);
//    }
//
//    static <T, R> PageResult<R> toPageResult(IPage<T> page, Function<T, R> mapFunction) {
//        if (page == null) {
//            return PageResult.create();
//        }
//        List<R> rows = page.getRecords().stream().map(mapFunction).collect(Collectors.toList());
//        return PageResult.create((int) page.getCurrent(), (int) page.getSize(), (int) page.getPages(), (int) page.getTotal(), rows, null);
//    }
//}
