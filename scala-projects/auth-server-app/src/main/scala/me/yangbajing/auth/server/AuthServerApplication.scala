package me.yangbajing.auth.server

import akka.actor.typed.SpawnProtocol
import akka.http.scaladsl.Http
import com.helloscala.akka.security.oauth.server.OAuth2Route
import com.orbitz.consul.model.agent.ImmutableRegistration
import com.typesafe.scalalogging.StrictLogging
import helloscala.fusion.consul.FusionCloudConsul

import scala.io.StdIn

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-11-30 19:44:07
 */
object AuthServerApplication extends StrictLogging {
  def main(args: Array[String]): Unit = {
    val fusionCloud = FusionCloudConsul.fromByLocalConfig()
    implicit val system = fusionCloud.initActorSystem(SpawnProtocol())
    import system.executionContext

    new AuthorizationServerEntitiesCreator(fusionCloud).init()

    val route = new OAuth2Route(system).route
    val bindingFuture = Http().newServerAt(fusionCloud.serverHost, fusionCloud.serverPort).bind(route)
    val registrationBuilder = fusionCloud.configureRegistration(ImmutableRegistration.builder())
    fusionCloud.register(registrationBuilder.build())

    logger.info(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the serverPort
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
