package helloscala.fusion.cloud

import akka.actor.typed.{ ActorRef, Extension }
import akka.cluster.sharding.typed.scaladsl.EntityTypeKey
import akka.pattern.StatusReply

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-02 12:08:17
 */
trait FusionCloudDiscovery extends Extension {}

object FusionCloudDiscovery {
  trait Command
  val TypeKey: EntityTypeKey[Command] = EntityTypeKey("FusionCloudDiscovery")

  case class GetRegistration(replyTo: ActorRef[StatusReply[Registration]]) extends Command

  case class AddRegistration(registration: Registration) extends Command
}

abstract class Registration {}
