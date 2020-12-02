package helloscala.fusion.cloud

import akka.actor.typed.{ ActorSystem, Extension, ExtensionId }
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, EntityRef }
import akka.util.Timeout

import scala.concurrent.duration._

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-02 13:34:38
 */
class FusionCloud(val system: ActorSystem[_]) extends Extension {
  implicit val timeout: Timeout = 5.seconds
  val entityRefs = new EntityRefs(system)
}

object FusionCloud extends ExtensionId[FusionCloud] {
  override def createExtension(system: ActorSystem[_]): FusionCloud = new FusionCloud(system)
}

class EntityRefs(system: ActorSystem[_]) {
  def cloudDiscovery: EntityRef[FusionCloudDiscovery.Command] =
    ClusterSharding(system).entityRefFor(FusionCloudDiscovery.TypeKey, EntityIds.entityId(system))
}
