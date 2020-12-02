package com.helloscala.akka.security.oauth.server

import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, Entity }
import com.helloscala.akka.security.oauth.server.authentication.client.{
  InMemoryRegisteredClientRepository,
  RegisteredClientRepository
}
import com.helloscala.akka.security.oauth.server.crypto.keys.{ InMemoryKeyManager, KeyManager }
import com.helloscala.akka.security.oauth.server.jwt.{ DefaultJwtEncoder, JwtEncoder }

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:37:23
 */
trait OAuth2AuthorizationServerEntitiesCreator {
  def system: ActorSystem[_]

  def initJwtEncoder(): ActorRef[ShardingEnvelope[JwtEncoder.Command]] = {
    ClusterSharding(system).init(Entity(JwtEncoder.TypeKey)(ec => DefaultJwtEncoder(ec)))
  }

  def initKeyManager(): ActorRef[ShardingEnvelope[KeyManager.Command]] = {
    ClusterSharding(system).init(Entity(KeyManager.TypeKey)(ec => InMemoryKeyManager(ec)))
  }

  def initRegisteredClientRepository(): ActorRef[ShardingEnvelope[RegisteredClientRepository.Command]] = {
    ClusterSharding(system).init(Entity(RegisteredClientRepository.TypeKey)(ec =>
      InMemoryRegisteredClientRepository(ec)))
  }

  def initOauth2AuthorizationService(): ActorRef[ShardingEnvelope[OAuth2AuthorizationService.Command]] = {
    ClusterSharding(system).init(Entity(OAuth2AuthorizationService.TypeKey)(ec =>
      InMemoryOauth2AuthorizationService(ec)))
  }

  final def init(): Unit = {
    initKeyManager()
    initJwtEncoder()
    initRegisteredClientRepository()
    initOauth2AuthorizationService()
  }
}
