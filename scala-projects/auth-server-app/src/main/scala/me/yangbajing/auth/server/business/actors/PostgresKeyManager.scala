package me.yangbajing.auth.server.business.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }
import akka.cluster.sharding.typed.scaladsl.EntityContext
import com.helloscala.akka.security.oauth.server.crypto.keys.KeyManager._
import com.helloscala.akka.security.oauth.server.crypto.keys.ManagedKey
import com.typesafe.scalalogging.StrictLogging
import me.yangbajing.auth.server.db.PostgresSchema

import scala.concurrent.Future

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:21:00
 */
object PostgresKeyManager {
  def apply(entityContext: EntityContext[Command]): Behavior[Command] =
    Behaviors.setup(context => new PostgresKeyManager(context).receive())
}

class PostgresKeyManager(context: ActorContext[Command]) extends StrictLogging {
  import context.executionContext
  private val managedKeyRepository = PostgresSchema(context.system).managedKeyRepository

  def receive(): Behavior[Command] = Behaviors.receiveMessage {
    case FindByIdentityId(identityId, replyTo) =>
      managedKeyRepository
        .findLastByIdentityId(identityId)
        .map(_.map(_.toManagedKey))
        .recover {
          case e =>
            logger.error(s"FindByIdentityId($identityId) error.", e)
            None
        }
        .foreach(result => replyTo ! result)
      Behaviors.same

    case FindById(id, replyTo) =>
      managedKeyRepository
        .findById(id)
        .map(_.map(_.toManagedKey))
        .recover {
          case e =>
            logger.error(s"FindById($id) error.", e)
            None
        }
        .foreach(result => replyTo ! result)
      Behaviors.same

    case FindByAlgorithm(algorithm, replyTo) =>
      val f: Future[Set[ManagedKey]] =
        managedKeyRepository
          .findByAlgorithm(algorithm)
          .map { list =>
            list.map(_.toManagedKey).toSet
          }
          .recover {
            case e =>
              logger.error(s"FindByAlgorithm($algorithm) error.", e)
              Set()
          }
      f.foreach(result => replyTo ! result)
      Behaviors.same

    case FindAll(replyTo) =>
      val f: Future[Set[ManagedKey]] =
        managedKeyRepository
          .findAll()
          .map { list =>
            list.map(_.toManagedKey).toSet
          }
          .recover {
            case e =>
              logger.error(s"FindAll error.", e)
              Set()
          }
      f.foreach(result => replyTo ! result)
      Behaviors.same
  }
}
