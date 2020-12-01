package helloscala.fusion.core

import akka.actor.typed.{ ActorSystem, Behavior }
import com.typesafe.config.Config

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:40:06
 */
trait BaseFusionCloud extends BaseConfigProperties with FusionCloudActorSystemFactory {
  def actorSystem: ActorSystem[_]
}

trait FusionCloudActorSystemFactory {
  def createActorSystem[T](guardianBehavior: Behavior[T]): ActorSystem[T]
}

trait BaseConfigProperties {
  def config: Config

  def applicationName: String = config.getString("fusion.application.name")
  def serverPort: Int = config.getInt("server.serverPort")
  def serverHost: String = config.getString("server.host")
}
