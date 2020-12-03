package me.yangbajing.auth.server.db.repository

import me.yangbajing.auth.server.db.AuthProfile
import me.yangbajing.auth.server.db.domain.ClientDetailDO
import me.yangbajing.auth.server.db.schema.ClientDetailTable

import scala.concurrent.Future

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 12:04:45
 */
class ClientDetailRepository(profile: AuthProfile.type, db: AuthProfile.backend.DatabaseDef) {
  import profile.api._
  private val tClientDetail = TableQuery[ClientDetailTable]

  def findByClientId(clientId: String): Future[Option[ClientDetailDO]] =
    db.run(tClientDetail.filter(_.clientId === clientId).result.headOption)

  def findById(id: String): Future[Option[ClientDetailDO]] =
    db.run(tClientDetail.filter(_.id === id).result.headOption)

  def save(clientDetail: ClientDetailDO): Future[Int] = {
    db.run(tClientDetail.insertOrUpdate(clientDetail).transactionally)
  }

  def saveAll(clientDetails: Seq[ClientDetailDO]): Future[Option[Int]] = {
    db.run(tClientDetail.insertOrUpdateAll(clientDetails).transactionally)
  }
}
