package com.helloscala.akka.security.oauth.server

import akka.actor.typed.{ ActorRef, ActorSystem, Extension, ExtensionId }
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, Entity }
import com.helloscala.akka.security.oauth.server.authentication.client.{
  InMemoryRegisteredClientRepository,
  RegisteredClientRepository
}
import com.helloscala.akka.security.oauth.server.crypto.keys.{ InMemoryKeyManager, KeyManager }
import com.helloscala.akka.security.oauth.server.jwt.{ DefaultJwtEncoder, JwtEncoder }

import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:37:23
 */
trait OAuth2AuthorizationServerCreator extends Extension {
  private val beInit = new AtomicBoolean(false)
  def system: ActorSystem[_]

  private var _jwtEncoder: ActorRef[ShardingEnvelope[JwtEncoder.Command]] = _
  private var _keyManager: ActorRef[ShardingEnvelope[KeyManager.Command]] = _
  private var _registeredClientRepository: ActorRef[ShardingEnvelope[RegisteredClientRepository.Command]] = _
  private var _oauth2AuthorizationService: ActorRef[ShardingEnvelope[OAuth2AuthorizationService.Command]] = _

  def jwtEncoder: ActorRef[ShardingEnvelope[JwtEncoder.Command]] = _jwtEncoder
  def keyManager: ActorRef[ShardingEnvelope[KeyManager.Command]] = _keyManager
  def registeredClientRepository: ActorRef[ShardingEnvelope[RegisteredClientRepository.Command]] =
    _registeredClientRepository
  def oauth2AuthorizationService: ActorRef[ShardingEnvelope[OAuth2AuthorizationService.Command]] =
    _oauth2AuthorizationService

  protected def initJwtEncoder(): ActorRef[ShardingEnvelope[JwtEncoder.Command]] = {
    ClusterSharding(system).init(Entity(JwtEncoder.TypeKey)(ec => DefaultJwtEncoder(ec)))
  }

  protected def initKeyManager(): ActorRef[ShardingEnvelope[KeyManager.Command]] = {
    ClusterSharding(system).init(Entity(KeyManager.TypeKey)(ec => InMemoryKeyManager(ec)))
  }

  protected def initRegisteredClientRepository(): ActorRef[ShardingEnvelope[RegisteredClientRepository.Command]] = {
    ClusterSharding(system).init(Entity(RegisteredClientRepository.TypeKey)(ec =>
      InMemoryRegisteredClientRepository(ec)))
  }

  protected def initOauth2AuthorizationService(): ActorRef[ShardingEnvelope[OAuth2AuthorizationService.Command]] = {
    ClusterSharding(system).init(Entity(OAuth2AuthorizationService.TypeKey)(ec =>
      InMemoryOauth2AuthorizationService(ec)))
  }

  final def init(): Unit = {
    if (beInit.compareAndSet(false, true)) {
      _keyManager = initKeyManager()
      _jwtEncoder = initJwtEncoder()
      _registeredClientRepository = initRegisteredClientRepository()
      _oauth2AuthorizationService = initOauth2AuthorizationService()
    }
  }
}

object InmemoryOAuth2AuthorizationServer extends ExtensionId[OAuth2AuthorizationServerCreator] {
  override def createExtension(typedSystem: ActorSystem[_]): OAuth2AuthorizationServerCreator =
    new OAuth2AuthorizationServerCreator {
      override def system: ActorSystem[_] = typedSystem
    }
}
