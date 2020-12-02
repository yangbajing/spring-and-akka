package akka.fusion.management

import akka.actor.ExtendedActorSystem
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Route
import akka.management.HealthCheckRoutes
import akka.management.scaladsl.{ AkkaManagement, ManagementRouteProviderSettings }

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-02 17:05:49
 */
object HealthCheckUtils {
  def healthRoute(system: ActorSystem[_]): Route = {
    val provider = new HealthCheckRoutes(system.classicSystem.asInstanceOf[ExtendedActorSystem])
    val settings = AkkaManagement(system).settings
    val protocol = "http" // changed to "https" if ManagementRouteProviderSettings.withHttpsConnectionContext is used
    val selfBaseUri =
      Uri(s"$protocol://${settings.Http.Hostname}:${settings.Http.Port}${settings.Http.BasePath.fold("")("/" + _)}")
    provider.routes(ManagementRouteProviderSettings(selfBaseUri, settings.Http.RouteProvidersReadOnly))
  }
}
