package helloscala.fusion.consul

import java.nio.charset.{ Charset, StandardCharsets }

import com.google.common.net.HostAndPort
import com.orbitz.consul.Consul
import com.orbitz.consul.model.agent.Registration
import com.typesafe.config.{ Config, ConfigFactory }

import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 18:50:02
 */
class FusionConsul private (consul: Consul) extends AutoCloseable {
  def getConfig(key: String, localConfig: Config = ConfigFactory.parseString("{}")): Config = {
    ConfigFactory
      .parseString(
        getValueAsString(key).getOrElse(
          throw new IllegalArgumentException(s"The key of Consul is not exists that is [$key].")))
      .withFallback(localConfig)
      .withFallback(ConfigFactory.load())
  }

  def getValueAsString(key: String, charset: Charset = StandardCharsets.UTF_8): Option[String] =
    consul.keyValueClient().getValueAsString(key, charset).toScala

  def getValuesAsString(key: String, charset: Charset = StandardCharsets.UTF_8): Vector[String] =
    consul.keyValueClient().getValuesAsString(key, charset).asScala.toVector

  def register(registration: Registration): FusionConsul = {
    consul.agentClient().register(registration)
    this
  }

  override def close(): Unit = consul.destroy()
}

object FusionConsul {
  def apply(consul: Consul): FusionConsul = new FusionConsul(consul)

  def apply(host: String, port: Int): FusionConsul =
    apply(Consul.builder().withHostAndPort(HostAndPort.fromParts(host, port)).build())

  def fromByLocalConfig(localConfig: Config = ConfigFactory.load()): FusionConsul =
    FusionConsul(
      localConfig.getString("fusion.cloud.fusionConsul.host"),
      localConfig.getInt("fusion.cloud.fusionConsul.serverPort"))

//  def actorSystem[T](
//      guardianBehavior: Behavior[T],
//      name: String,
//      setup: ActorSystemSetup,
//      guardianProps: Props = Props.empty): ActorSystem[T] = ActorSystem(guardianBehavior, name, setup, guardianProps)

//  def actorSystem[T](guardianBehavior: Behavior[T], config: Config): ActorSystem[T] =
//    actorSystem(
//      guardianBehavior,
//      config.getString("fusion.application.name"),
//      ActorSystemSetup.create(BootstrapSetup(config)))

//  def actorSystemFromLocalConfig[T](
//      guardianBehavior: Behavior[T],
//      localConfig: Config = ConfigFactory.load()): ActorSystem[T] = {
//    val fusionConsul = fromByLocalConfig(localConfig)
//    val key = localConfig.getString("fusion.cloud.fusionConsul.localConfig.key")
//    val c = ConfigFactory
//      .parseString(
//        fusionConsul
//          .getValueAsString(key)
//          .getOrElse(throw new IllegalArgumentException(s"The key of Consul is not exists that is [$key].")))
//      .withFallback(localConfig)
//      .withFallback(ConfigFactory.load())
//    ConfigFactory.invalidateCaches()
//    actorSystem(
//      guardianBehavior,
//      c.getString("fusion.application.name"),
//      ActorSystemSetup.create(BootstrapSetup(c)),
//      Props.empty)
//  }
}
