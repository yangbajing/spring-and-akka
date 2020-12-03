package me.yangbajing.auth.server.db.schema

import me.yangbajing.auth.server.db.AuthProfile.api._
import me.yangbajing.auth.server.db.domain.ClientDetailDO

import java.time.LocalDateTime

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 10:41:54
 */
class ClientDetailTable(tag: Tag) extends Table[ClientDetailDO](tag, Some("oauth"), "client_detail") {
  val id = column[String]("id", O.PrimaryKey)
  val clientId = column[String]("client_id")
  val clientSecret = column[String]("client_secret")
  val clientAuthenticationMethods = column[List[String]]("client_authentication_methods")
  val authorizationGrantTypes = column[List[String]]("authorization_grant_types")
  val redirectUris = column[List[String]]("redirect_uris")
  val scopes = column[List[String]]("scopes")
  val utc8Create = column[LocalDateTime]("utc8_create")
  val utc8Updated = column[LocalDateTime]("utc8_updated")
  val userId = column[Option[Long]]("user_id")
  val dataPushUrl = column[Option[String]]("data_push_url")
  override def * =
    (
      id,
      clientId,
      clientSecret,
      clientAuthenticationMethods,
      authorizationGrantTypes,
      redirectUris,
      scopes,
      utc8Create,
      utc8Updated,
      userId,
      dataPushUrl).mapTo[ClientDetailDO]
}
