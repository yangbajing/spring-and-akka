package me.yangbajing.core.util;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;

import java.util.concurrent.Executor;

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 14:52:30
 */
public interface ReactorUtils {
    static <T> Mono<T> toMono(ListenableFuture<T> listenableFuture, Executor executor) {
        return Mono.create(sink -> Futures.addCallback(listenableFuture, new FutureCallback<>() {
            @Override
            public void onFailure(Throwable t) {
                sink.error(t);
            }

            @Override
            public void onSuccess(T userBO) {
                sink.success(userBO);
            }
        }, executor));
    }
}
