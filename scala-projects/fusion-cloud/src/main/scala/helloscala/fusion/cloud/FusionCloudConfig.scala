package helloscala.fusion.cloud

import akka.actor.typed.Extension
import com.typesafe.config.Config

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-02 11:41:25
 */
trait FusionCloudConfig extends Extension with BaseConfigProperties

trait BaseConfigProperties {
  def config: Config

  def applicationName: String = config.getString("fusion.application.name")
  def serverPort: Int = config.getInt("server.port")
  def serverHost: String = config.getString("server.host")
}
