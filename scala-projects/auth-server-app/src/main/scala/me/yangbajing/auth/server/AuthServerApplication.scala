package me.yangbajing.auth.server

import akka.actor.typed.SpawnProtocol
import akka.http.scaladsl.Http
import akka.management.scaladsl.AkkaManagement
import auth.grpc.GreeterServiceHandler
import com.helloscala.akka.security.oauth.server.OAuth2Route
import com.orbitz.consul.model.agent.{ ImmutableRegCheck, ImmutableRegistration }
import com.typesafe.scalalogging.StrictLogging
import helloscala.fusion.consul.{ FusionCloudConfigConsul, FusionCloudDiscoveryConsul, FusionConsulFactory }
import helloscala.fusion.grpc.GrpcUtils
import me.yangbajing.auth.server.grpc.impl.GreeterServiceImpl

import scala.io.StdIn

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-11-30 19:44:07
 */
object AuthServerApplication extends StrictLogging {
  def main(args: Array[String]): Unit = {
    implicit val system = FusionConsulFactory.fromByConfig().initActorSystem(SpawnProtocol())
    import system.executionContext

    AuthorizationServerEntities(system).init()

    val cloudConfig = FusionCloudConfigConsul(system)
    val cloudDiscovery = FusionCloudDiscoveryConsul(system)

    val grpcServices = GreeterServiceHandler(new GreeterServiceImpl(system))
//    val managementRoute = ClusterHttpManagementRoutes(akka.cluster.Cluster(system))
    val route = GrpcUtils.concatRoute(new OAuth2Route(system).route, GrpcUtils.toRoute(grpcServices) /*,
      managementRoute,
      HealthCheckUtils.healthRoute(system)*/ )

    val bindingFuture = Http().newServerAt(cloudConfig.serverHost, cloudConfig.serverPort).bind(route)
    AkkaManagement(system).start().foreach { uri =>
      val registrationBuilder = cloudDiscovery.configureRegistration(ImmutableRegistration.builder())
      val check = ImmutableRegCheck
        .builder()
        .interval("5.s")
        .http(s"http://${uri.authority.host}:${uri.authority.port}/health/alive")
        .build()
      registrationBuilder.check(check)
      cloudDiscovery.register(registrationBuilder.build())
    }

    bindingFuture.foreach { binding =>
      val registrationBuilder = cloudDiscovery.configureRegistration(ImmutableRegistration.builder())
      registrationBuilder.port(binding.localAddress.getPort)
      cloudDiscovery.register(registrationBuilder.build())
    }

    logger.info(
      s"Server online at http://${cloudConfig.serverHost}:${cloudConfig.serverPort}/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the serverPort
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
