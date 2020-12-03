package me.yangbajing.auth.server.db.repository

import me.yangbajing.auth.server.db.AuthProfile
import me.yangbajing.auth.server.db.domain.ManagedKeyDO
import me.yangbajing.auth.server.db.schema.ManagedKeyTable

import scala.concurrent.Future

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 10:28:02
 */
class ManagedKeyRepository(profile: AuthProfile.type, db: AuthProfile.backend.DatabaseDef) {
  import profile.api._
  private val tManagedKey = TableQuery[ManagedKeyTable]

  def save(key: ManagedKeyDO): Future[Int] = db.run(tManagedKey.insertOrUpdate(key).transactionally)

  def saveAll(keys: Seq[ManagedKeyDO]): Future[Option[Int]] =
    db.run(tManagedKey.insertOrUpdateAll(keys).transactionally)

  def findLastByIdentityId(identityId: String): Future[Option[ManagedKeyDO]] =
    db.run(tManagedKey.filter(_.identityId === identityId).sortBy(_.activatedOn.desc).result.headOption)

  def findAll(): Future[Seq[ManagedKeyDO]] = db.run(tManagedKey.result)

  def findByAlgorithm(algorithm: String): Future[Seq[ManagedKeyDO]] =
    db.run(tManagedKey.filter(_.algorithm === algorithm).result)

  def findById(id: String): Future[Option[ManagedKeyDO]] = db.run(tManagedKey.filter(_.id === id).result.headOption)
}
