package me.yangbajing.auth.server.oauth

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, Entity, EntityContext }
import com.helloscala.akka.security.oauth.server.crypto.keys.KeyManager

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:21:00
 */
object PostgresKeyManager {
  def init(system: ActorSystem[_]): ActorRef[ShardingEnvelope[KeyManager.Command]] = {
    ClusterSharding(system).init(Entity(KeyManager.TypeKey)(entityContext => apply(entityContext)))
  }

  def apply(entityContext: EntityContext[KeyManager.Command]): Behavior[KeyManager.Command] = Behaviors.setup {
    context =>
      Behaviors.receiveMessage {
        case _ =>
          Behaviors.same
      }
  }
}
