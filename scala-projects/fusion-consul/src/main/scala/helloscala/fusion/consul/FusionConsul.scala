package helloscala.fusion.consul

import com.google.common.net.HostAndPort
import com.orbitz.consul.Consul
import com.orbitz.consul.model.agent.Registration
import com.typesafe.config.{ Config, ConfigFactory }

import java.nio.charset.{ Charset, StandardCharsets }
import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 18:50:02
 */
class FusionConsul private (consul: Consul) extends AutoCloseable {
  def getConfig(key: String, localConfig: Config = ConfigFactory.parseString("{}")): Config = {
    val text = getValueAsString(key).getOrElse(
      throw new IllegalArgumentException(s"The key of Consul is not exists that is [$key]."))
    val config = ConfigFactory.parseString(text)
    config.withFallback(localConfig).withFallback(ConfigFactory.load())
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

  def fromByConfig(localConfig: Config = ConfigFactory.load()): FusionConsul =
    FusionConsul(localConfig.getString("fusion.cloud.consul.host"), localConfig.getInt("fusion.cloud.consul.port"))
}
