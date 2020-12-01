package helloscala.fusion.consul

import java.util.Objects

import akka.actor.BootstrapSetup
import akka.actor.setup.ActorSystemSetup
import akka.actor.typed.{ ActorSystem, Behavior, Props }
import com.orbitz.consul.model.agent.ImmutableRegistration
import com.typesafe.config.{ Config, ConfigFactory }
import helloscala.fusion.consul.FusionCloudConsul.DISCOVERY
import helloscala.fusion.core.BaseFusionCloud

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 22:48:40
 */
class FusionCloudConsul(val fusionConsul: FusionConsul, val config: Config) extends BaseFusionCloud {
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

  def register(registration: ImmutableRegistration): FusionCloudConsul = {
    fusionConsul.register(registration)
    this
  }

  override def createActorSystem[T](guardianBehavior: Behavior[T]): ActorSystem[T] = {
    ActorSystem(
      guardianBehavior,
      config.getString("fusion.akka.name"),
      ActorSystemSetup.create(BootstrapSetup(config)),
      Props.empty)
  }

  def configureRegistration(builder: ImmutableRegistration.Builder): ImmutableRegistration.Builder = {
    val tags =
      if (config.hasPath(DISCOVERY.TAGS)) config.getStringList(DISCOVERY.TAGS)
      else new java.util.LinkedList[String]()
    if (config.hasPath(DISCOVERY.SECURE)) {
      tags.add(DISCOVERY.SECURE + "=" + config.getBoolean(DISCOVERY.SECURE))
    }
    builder
      .id(s"$applicationName-$serverPort")
      .name(applicationName)
      .addTags("secure=false", s"gRPC.serverPort=$serverPort")
      .addAllTags(tags)
      .address(serverHost)
      .port(serverPort)
  }
}

object FusionCloudConsul {
  object CONFIG {
    private val BASE = "fusion.cloud.fusionConsul.config"
    val KEY = s"$BASE.key"
  }
  object DISCOVERY {
    private val BASE = "fusion.cloud.consul.discovery"
    val TAGS = s"$BASE.tags"
    val SECURE = s"$BASE.secure"
  }

  def fromByLocalConfig(localConfig: Config = ConfigFactory.load()): FusionCloudConsul = {
    val fusionConsul = FusionConsul.fromByLocalConfig(localConfig)
    new FusionCloudConsul(fusionConsul, fusionConsul.getConfig(CONFIG.KEY, localConfig))
  }

  def apply(consul: FusionConsul): FusionCloudConsul = new FusionCloudConsul(consul, consul.getConfig(CONFIG.KEY))
}
