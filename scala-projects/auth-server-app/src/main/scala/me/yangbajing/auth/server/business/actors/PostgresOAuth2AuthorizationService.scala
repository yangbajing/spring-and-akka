package me.yangbajing.auth.server.business.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }
import akka.cluster.sharding.typed.scaladsl.EntityContext
import com.helloscala.akka.security.oauth.server.OAuth2AuthorizationService._

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 09:30:11
 */
object PostgresOAuth2AuthorizationService {
  def apply(entityContext: EntityContext[Command]): Behavior[Command] =
    Behaviors.setup(context => new PostgresOAuth2AuthorizationService(context).receive())
}

class PostgresOAuth2AuthorizationService(context: ActorContext[Command]) {
  def receive(): Behavior[Command] = Behaviors.receiveMessage { case _ => Behaviors.same }

}
