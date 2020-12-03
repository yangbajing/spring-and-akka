package me.yangbajing.auth.server.db

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import com.helloscala.akka.security.oauth.server.crypto.keys.KeyUtils
import com.typesafe.config.ConfigFactory
import me.yangbajing.auth.server.db.domain.{ ClientDetailDO, ManagedKeyDO }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.time.LocalDateTime

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 11:17:28
 */
class AuthProfileTestTest
    extends ScalaTestWithActorTestKit(ConfigFactory.load())
    with AnyWordSpecLike
    with Matchers
    with ScalaFutures {
  val schema = PostgresSchema(system)

  "Client" should {
    val messageClient = ClientDetailDO(
      "000000000000000000000000",
      "messaging-client",
      "secret",
      List("client_credentials"),
      List("basic"),
      Nil,
      List("message.read", "message.write"),
      LocalDateTime.now(),
      LocalDateTime.now(),
      None,
      None)
    val ecClient = ClientDetailDO(
      "000000000000000000000001",
      "ec-client",
      "secret",
      List("client_credentials"),
      List("basic"),
      Nil,
      List("message.read", "message.write", "api.read", "api.write"),
      LocalDateTime.now(),
      LocalDateTime.now(),
      None,
      None)
    "create client" in {
      val ret = schema.clientDetailRepository.saveAll(Seq(messageClient, ecClient)).futureValue
      ret.isDefined shouldBe true
      println(ret)
    }

    "create managed key" in {
      schema.managedKeyRepository.save(ManagedKeyDO.from(messageClient, KeyUtils.RSA)).futureValue shouldBe 1
      schema.managedKeyRepository.save(ManagedKeyDO.from(ecClient, KeyUtils.EC)).futureValue shouldBe 1
    }
  }

}
