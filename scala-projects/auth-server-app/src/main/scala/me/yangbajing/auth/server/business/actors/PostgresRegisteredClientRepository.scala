package me.yangbajing.auth.server.business.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }
import akka.cluster.sharding.typed.scaladsl.EntityContext
import com.helloscala.akka.security.oauth.server.authentication.client.RegisteredClientRepository._
import com.typesafe.scalalogging.StrictLogging
import me.yangbajing.auth.server.db.PostgresSchema

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 09:27:31
 */
object PostgresRegisteredClientRepository {
  def apply(entityContext: EntityContext[Command]): Behavior[Command] =
    Behaviors.setup(context => new PostgresRegisteredClientRepository(context).receive())
}

class PostgresRegisteredClientRepository(context: ActorContext[Command]) extends StrictLogging {
  import context.executionContext
  private val clientDetailRepository = PostgresSchema(context.system).clientDetailRepository

  def receive(): Behavior[Command] = Behaviors.receiveMessage {
    case FindById(id, replyTo) =>
      clientDetailRepository
        .findById(id)
        .map(_.map(_.toRegisteredClient))
        .recover {
          case e =>
            logger.error(s"FindById($id) error.", e)
            None
        }
        .foreach(result => replyTo ! result)
      Behaviors.same

    case FindByClientId(clientId, replyTo) =>
      clientDetailRepository
        .findByClientId(clientId)
        .map(_.map(_.toRegisteredClient))
        .recover {
          case e =>
            logger.error(s"FindByClientId($clientId) error.", e)
            None
        }
        .foreach(result => replyTo ! result)
      Behaviors.same
  }

}
