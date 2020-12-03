package me.yangbajing.auth.server.db.repository

import me.yangbajing.auth.server.db.AuthProfile
import org.scalatest.{ BeforeAndAfterAll, OptionValues }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 15:39:55
 */
class ClientDetailRepositoryTest
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with BeforeAndAfterAll {
  import AuthProfile.api._

  private var db: AuthProfile.backend.Database = _

  "ClientDetail" should {
    "create" in {}
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    db = Database.forConfig("fusion.jdbc.datasource")
  }

  override protected def afterAll(): Unit = {
    db.close()
    super.afterAll()
  }
}
