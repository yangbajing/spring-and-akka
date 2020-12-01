package me.yangbajing.portal.controller.api;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import me.yangbajing.core.util.ReactorUtils;
import me.yangbajing.user.grpc.QueryUser;
import me.yangbajing.user.grpc.UserBO;
import me.yangbajing.user.grpc.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 10:24:27
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    @Autowired
    private ThreadPoolTaskExecutor applicationTaskExecutor;

    @GrpcClient("user-app")
    private UserServiceGrpc.UserServiceFutureStub userService;

    @GetMapping(path = "get/{id}", produces = "application/json")
    public Mono<String> getUser(@PathVariable Long id) {
        ListenableFuture<UserBO> listenableFuture = userService.getUser(QueryUser.newBuilder().setId(id).build());
        return ReactorUtils
                .toMono(listenableFuture, applicationTaskExecutor)
                .flatMap(user -> {
            try {
                return Mono.just(JsonFormat.printer().printingEnumsAsInts().includingDefaultValueFields().print(user));
            } catch (InvalidProtocolBufferException e) {
                return Mono.error(e);
            }
        });
    }
}
