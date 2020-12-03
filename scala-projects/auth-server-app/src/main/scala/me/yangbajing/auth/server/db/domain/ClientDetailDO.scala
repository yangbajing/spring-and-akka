package me.yangbajing.auth.server.db.domain

import com.helloscala.akka.security.oauth.server.authentication.client.RegisteredClient

import java.time.LocalDateTime

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 10:39:39
 */
case class ClientDetailDO(
    id: String,
    clientId: String,
    clientSecret: String,
    clientAuthenticationMethods: List[String],
    authorizationGrantTypes: List[String],
    redirectUris: List[String],
    scopes: List[String],
    utc8Create: LocalDateTime,
    utc8Modified: LocalDateTime,
    userId: Option[Long],
    dataPushUrl: Option[String]) {
  def toRegisteredClient: RegisteredClient =
    RegisteredClient(id, clientId, clientSecret, redirectUris.toSet, scopes.toSet)
}
