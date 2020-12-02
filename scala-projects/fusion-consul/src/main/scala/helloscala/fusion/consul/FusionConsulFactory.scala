package helloscala.fusion.consul

import akka.actor.BootstrapSetup
import akka.actor.setup.ActorSystemSetup
import akka.actor.typed.{ ActorSystem, Behavior, Props }
import com.typesafe.config.{ Config, ConfigFactory }
import helloscala.fusion.cloud.FusionFactory

import java.util.Objects

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 22:48:40
 */
class FusionConsulFactory(val fusionConsul: FusionConsul, val config: Config) extends FusionFactory {
  private var _actorSystem: ActorSystem[Nothing] = _
  def actorSystem: ActorSystem[Nothing] = Objects.requireNonNull(_actorSystem, "The _actorSystem is not initialized.")

  def initActorSystem[T](guardianBehavior: Behavior[T]): ActorSystem[T] = synchronized {
    if (_actorSystem == null) {
      _actorSystem = createActorSystem(guardianBehavior)
      init()
    }
    _actorSystem.asInstanceOf[ActorSystem[T]]
  }

  private def init(): Unit = {
    actorSystem.classicSystem.registerOnTermination(() => fusionConsul.close())
  }

  override def createActorSystem[T](guardianBehavior: Behavior[T]): ActorSystem[T] = {
    ActorSystem(
      guardianBehavior,
      config.getString("fusion.akka.name"),
      ActorSystemSetup.create(BootstrapSetup(config)),
      Props.empty)
  }

}

object FusionConsulFactory {
  object CONFIG {
    private val BASE = "fusion.cloud.consul.config"
    val KEY = s"$BASE.key"
  }
  object DISCOVERY {
    private val BASE = "fusion.cloud.consul.discovery"
    val TAGS = s"$BASE.tags"
    val SECURE = s"$BASE.secure"
  }

  def fromByConfig(localConfig: Config = ConfigFactory.load()): FusionConsulFactory = {
    val fusionConsul = FusionConsul.fromByConfig(localConfig)
    val key = localConfig.getString(CONFIG.KEY)
    val config = fusionConsul.getConfig(key, localConfig)
    new FusionConsulFactory(fusionConsul, config)
  }

  def apply(consul: FusionConsul): FusionConsulFactory = new FusionConsulFactory(consul, consul.getConfig(CONFIG.KEY))
}
