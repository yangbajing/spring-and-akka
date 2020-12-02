package helloscala.fusion.consul

import akka.actor.typed.{ ActorSystem, ExtensionId }
import com.typesafe.config.Config
import helloscala.fusion.cloud.FusionCloudConfig

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-02 11:42:25
 */
class FusionCloudConfigConsul(val system: ActorSystem[_]) extends FusionCloudConfig {
  val fusionConsul: FusionConsul = FusionConsul.fromByConfig(system.settings.config)
  override def config: Config = system.settings.config
}

object FusionCloudConfigConsul extends ExtensionId[FusionCloudConfigConsul] {
  override def createExtension(system: ActorSystem[_]): FusionCloudConfigConsul = new FusionCloudConfigConsul(system)
}
