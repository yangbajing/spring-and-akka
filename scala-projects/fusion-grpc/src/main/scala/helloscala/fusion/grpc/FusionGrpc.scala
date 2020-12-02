package helloscala.fusion.grpc

import akka.actor.typed.{ ActorSystem, Extension, ExtensionId }
import com.typesafe.scalalogging.StrictLogging
import helloscala.fusion.cloud.{ FusionCloud, FusionCloudDiscovery, Registration }

import scala.util.{ Failure, Success }

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-02 13:24:53
 */
class FusionGrpc(val system: ActorSystem[_]) extends Extension with StrictLogging {
  import system.executionContext
  val cloud: FusionCloud = FusionCloud(system)
  import cloud.timeout

  cloud.entityRefs.cloudDiscovery
    .askWithStatus[Registration](replyTo => FusionCloudDiscovery.GetRegistration(replyTo))
    .onComplete {
      case Success(registration) =>
        cloud.entityRefs.cloudDiscovery ! FusionCloudDiscovery.AddRegistration(registration)
      case Failure(e) =>
        logger.error("Failed to get the Registration object.", e)
    }
}

object FusionGrpc extends ExtensionId[FusionGrpc] {
  override def createExtension(system: ActorSystem[_]): FusionGrpc = new FusionGrpc(system)

}
