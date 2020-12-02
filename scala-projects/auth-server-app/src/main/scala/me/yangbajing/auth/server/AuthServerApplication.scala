package me.yangbajing.auth.server

import akka.actor.typed.SpawnProtocol
import akka.fusion.management.HealthCheckUtils
import akka.http.scaladsl.Http
import akka.management.cluster.scaladsl.ClusterHttpManagementRoutes
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
    val fusionFactory = FusionConsulFactory.fromByConfig()
    implicit val system = fusionFactory.initActorSystem(SpawnProtocol())
    import system.executionContext

    new AuthorizationServerEntitiesCreator(system).init()

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
      registrationBuilder.check(
        ImmutableRegCheck.builder().interval("5.s").http("http://172.17.0.1:8558/health/alive").build())
      cloudDiscovery.register(registrationBuilder.build())
    }

    bindingFuture.foreach { binding =>
      val registrationBuilder = cloudDiscovery.configureRegistration(ImmutableRegistration.builder())
      registrationBuilder.port(binding.localAddress.getPort)
      cloudDiscovery.register(registrationBuilder.build())
    }

    logger.info(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the serverPort
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
