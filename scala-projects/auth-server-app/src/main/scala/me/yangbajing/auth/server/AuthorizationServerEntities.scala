package me.yangbajing.auth.server

import akka.actor.typed.{ ActorRef, ActorSystem, ExtensionId }
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, Entity }
import com.helloscala.akka.security.oauth.server.authentication.client.RegisteredClientRepository
import com.helloscala.akka.security.oauth.server.crypto.keys.KeyManager
import com.helloscala.akka.security.oauth.server.{ OAuth2AuthorizationServerCreator, OAuth2AuthorizationService }
import me.yangbajing.auth.server.business.actors.{
  PostgresKeyManager,
  PostgresOAuth2AuthorizationService,
  PostgresRegisteredClientRepository
}

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:37:57
 */
class AuthorizationServerEntities(val system: ActorSystem[_]) extends OAuth2AuthorizationServerCreator {
  override def initKeyManager(): ActorRef[ShardingEnvelope[KeyManager.Command]] =
    ClusterSharding(system).init(Entity(KeyManager.TypeKey)(PostgresKeyManager.apply))

  override def initRegisteredClientRepository(): ActorRef[ShardingEnvelope[RegisteredClientRepository.Command]] =
    ClusterSharding(system).init(Entity(RegisteredClientRepository.TypeKey)(PostgresRegisteredClientRepository.apply))

  override def initOauth2AuthorizationService(): ActorRef[ShardingEnvelope[OAuth2AuthorizationService.Command]] =
    ClusterSharding(system).init(Entity(OAuth2AuthorizationService.TypeKey)(PostgresOAuth2AuthorizationService.apply))
}

object AuthorizationServerEntities extends ExtensionId[AuthorizationServerEntities] {
  override def createExtension(system: ActorSystem[_]): AuthorizationServerEntities =
    new AuthorizationServerEntities(system)
}
