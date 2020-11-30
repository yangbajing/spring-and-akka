package me.yangbajing.auth.server

import akka.actor.typed.{ ActorSystem, SpawnProtocol }
import akka.http.scaladsl.Http
import com.helloscala.akka.security.oauth.server.{ OAuth2AuthorizationServerCreator, OAuth2Route }
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-11-30 19:44:07
 */
object AuthServerApplication extends StrictLogging {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(SpawnProtocol(), "oauth-server")
    import system.executionContext
    Await.result(OAuth2AuthorizationServerCreator.init(system), 2.seconds)
    val route = new OAuth2Route(system).route
    val bindingFuture = Http().newServerAt("localhost", 9000).bind(route)

    logger.info(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
