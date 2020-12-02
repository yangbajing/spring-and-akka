package me.yangbajing.auth.server

import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, Entity }
import com.helloscala.akka.security.oauth.server.authentication.client.RegisteredClientRepository
import com.helloscala.akka.security.oauth.server.crypto.keys.KeyManager
import com.helloscala.akka.security.oauth.server.{
  OAuth2AuthorizationServerEntitiesCreator,
  OAuth2AuthorizationService
}
import me.yangbajing.auth.server.oauth.PostgresKeyManager

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:37:57
 */
class AuthorizationServerEntitiesCreator(val system: ActorSystem[_]) extends OAuth2AuthorizationServerEntitiesCreator {
  override def initKeyManager(): ActorRef[ShardingEnvelope[KeyManager.Command]] = {
    super.initKeyManager()
//    ClusterSharding(system).init(Entity(KeyManager.TypeKey)(entityContext => PostgresKeyManager(entityContext)))
  }

  override def initRegisteredClientRepository(): ActorRef[ShardingEnvelope[RegisteredClientRepository.Command]] =
    super.initRegisteredClientRepository()

  override def initOauth2AuthorizationService(): ActorRef[ShardingEnvelope[OAuth2AuthorizationService.Command]] =
    super.initOauth2AuthorizationService()
}
