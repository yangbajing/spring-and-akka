package me.yangbajing.auth.server.oauth

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }
import akka.cluster.sharding.typed.scaladsl.EntityContext
import com.helloscala.akka.security.oauth.server.crypto.keys.KeyManager._

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:21:00
 */
class PostgresKeyManager(context: ActorContext[Command]) {
  def receive(): Behavior[Command] = Behaviors.receiveMessage {
    case _ =>
      Behaviors.same
  }
}
object PostgresKeyManager {
  def apply(entityContext: EntityContext[Command]): Behavior[Command] =
    Behaviors.setup(context => new PostgresKeyManager(context).receive())
}
