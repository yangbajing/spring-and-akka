package me.yangbajing.user.grpc.service.impl;

import io.grpc.stub.StreamObserver;
import me.yangbajing.user.grpc.QueryUser;
import me.yangbajing.user.grpc.UserBO;
import me.yangbajing.user.grpc.UserServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 10:04:21
 */
@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    @Override
    public void getUser(QueryUser request, StreamObserver<UserBO> responseObserver) {
        UserBO reply = UserBO.newBuilder()
                .setId(1L)
                .setUsername("Hello ==> " + request.getUsername())
                .setPhone("13845678901")
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
