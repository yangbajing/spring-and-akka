package me.yangbajing.auth.server.db.schema

import me.yangbajing.auth.server.db.AuthProfile.api._
import me.yangbajing.auth.server.db.domain.ManagedKeyDO

import java.time.Instant

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 10:28:38
 */
class ManagedKeyTable(tag: Tag) extends Table[ManagedKeyDO](tag, Some("oauth"), "managed_key") {
  val id = column[String]("id", O.PrimaryKey)
  val algorithm = column[String]("algorithm")
  val key = column[String]("key")
  val publicKey = column[String]("public_key")
  val identityId = column[String]("identity_id")
  val activatedOn = column[Instant]("activated_on")
  val deactivatedOn = column[Instant]("deactivated_on")

  override def * =
    (id, algorithm, key, publicKey, identityId, activatedOn, deactivatedOn) <> ((ManagedKeyDO.apply _).tupled, ManagedKeyDO.unapply)
}
