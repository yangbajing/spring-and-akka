package me.yangbajing.auth.server

import akka.actor.typed.ActorSystem
import com.helloscala.akka.security.oauth.server.OAuth2AuthorizationServerEntitiesCreator
import helloscala.fusion.core.BaseFusionCloud

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:37:57
 */
class AuthorizationServerEntitiesCreator(val fusionCloud: BaseFusionCloud)
    extends OAuth2AuthorizationServerEntitiesCreator {
  override def system: ActorSystem[_] = fusionCloud.actorSystem
}
