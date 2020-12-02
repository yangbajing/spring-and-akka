package com.helloscala.akka.security.oauth.server

import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, EntityRef }
import akka.util.Timeout
import com.helloscala.akka.security.authentication.AuthenticationProvider
import com.helloscala.akka.security.oauth.server.authentication.OAuth2Authorize
import com.helloscala.akka.security.oauth.server.authentication.client.RegisteredClientRepository
import com.helloscala.akka.security.oauth.server.crypto.keys.KeyManager
import com.helloscala.akka.security.oauth.server.jwt.JwtEncoder
import helloscala.fusion.cloud.EntityIds

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.reflect.ClassTag

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-09-20 22:20:15
 */
class OAuth2AuthorizationServerConfigure(system: ActorSystem[_]) {
  implicit val timeout: Timeout = 5.seconds

  private var _clientCredentialsAuthenticationProvider: AuthenticationProvider = _

  def registeredClientRepository: EntityRef[RegisteredClientRepository.Command] =
    ClusterSharding(system).entityRefFor(RegisteredClientRepository.TypeKey, EntityIds.entityId)

  def jwtEncoder: EntityRef[JwtEncoder.Command] =
    ClusterSharding(system).entityRefFor(JwtEncoder.TypeKey, EntityIds.entityId)

  def keyManager: EntityRef[KeyManager.Command] =
    ClusterSharding(system).entityRefFor(KeyManager.TypeKey, EntityIds.entityId)

  def authorizationService: EntityRef[OAuth2AuthorizationService.Command] =
    ClusterSharding(system).entityRefFor(OAuth2AuthorizationService.TypeKey, EntityIds.entityId)

  def oauth2AuthorizeProvider: ActorRef[OAuth2Authorize.Command] = ???

  def clientCredentialsAuthenticationProvider: AuthenticationProvider = _clientCredentialsAuthenticationProvider

  def init(): Future[Unit] = {
    _clientCredentialsAuthenticationProvider = getClientCredentialsAuthenticationProvider()
    Future.successful(())
  }

  def getClientCredentialsAuthenticationProvider(): AuthenticationProvider =
    createInstanceFor[AuthenticationProvider]("akka.security.server.authentication-provider.client-credentials")

  protected def createInstanceFor[T: ClassTag](path: String): T = {
    val fqcn = system.settings.config.getString(path)
    system.dynamicAccess
      .createInstanceFor[T](fqcn, List(classOf[ActorSystem[_]] -> system))
      .orElse(system.dynamicAccess.createInstanceFor[T](fqcn, Nil))
      .getOrElse(throw new ExceptionInInitializerError(s"Initial $fqcn class error."))
  }
}
