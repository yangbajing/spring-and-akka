package me.yangbajing.auth.server.db

import com.github.tminglei.slickpg.{ ExPostgresProfile, PgArraySupport, PgDate2Support }
import slick.basic.Capability
import slick.jdbc.JdbcCapabilities

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 10:43:04
 */
trait AuthProfile extends ExPostgresProfile with PgArraySupport with PgDate2Support {
  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI extends API with ArrayImplicits with DateTimeImplicits {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
  }
}
object AuthProfile extends AuthProfile
