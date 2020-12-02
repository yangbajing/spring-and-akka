package helloscala.fusion.consul

import akka.actor.typed.{ ActorSystem, ExtensionId }
import com.orbitz.consul.model.agent.ImmutableRegistration
import com.typesafe.config.Config
import helloscala.fusion.cloud.FusionCloudDiscovery
import helloscala.fusion.consul.FusionConsulFactory.DISCOVERY

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-02 11:43:49
 */
class FusionCloudDiscoveryConsul(val system: ActorSystem[_], val cloudConfig: FusionCloudConfigConsul)
    extends FusionCloudDiscovery {
  def config: Config = cloudConfig.config

  def register(registration: ImmutableRegistration): FusionCloudDiscoveryConsul = {
    cloudConfig.fusionConsul.register(registration)
    this
  }

  def configureRegistration(builder: ImmutableRegistration.Builder): ImmutableRegistration.Builder = {
    val applicationName = cloudConfig.applicationName
    val serverHost = cloudConfig.serverHost
    val serverPort = cloudConfig.serverPort

    val tags =
      if (config.hasPath(DISCOVERY.TAGS)) config.getStringList(DISCOVERY.TAGS)
      else new java.util.LinkedList[String]()

    tags.add("secure=" + (config.hasPath(DISCOVERY.SECURE) && config.getBoolean(DISCOVERY.SECURE)))
    tags.add(s"gRPC.port=$serverPort")

    builder
      .id(s"$applicationName-$serverPort")
      .name(applicationName)
      .addAllTags(tags)
      .address(serverHost)
      .port(serverPort)
  }
}

object FusionCloudDiscoveryConsul extends ExtensionId[FusionCloudDiscoveryConsul] {
  override def createExtension(system: ActorSystem[_]): FusionCloudDiscoveryConsul = {
    val fusionCloudConfig = FusionCloudConfigConsul(system)
    new FusionCloudDiscoveryConsul(system, fusionCloudConfig)
  }
}
