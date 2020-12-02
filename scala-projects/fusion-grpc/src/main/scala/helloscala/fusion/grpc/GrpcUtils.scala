package helloscala.fusion.grpc

import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import akka.http.scaladsl.server.Directives.{ complete, concat, extractRequest, onSuccess }
import akka.http.scaladsl.server.Route

import scala.concurrent.Future

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-02 16:28:36
 */
object GrpcUtils {
  def concatRoute(route: Route*): Route = concat(route: _*)

  def toRoute(func: HttpRequest => Future[HttpResponse]): Route = extractRequest { request =>
    onSuccess(func(request)) { response =>
      complete(response)
    }
  }
}
