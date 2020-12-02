package helloscala.fusion.cloud

import akka.actor.typed.ActorSystem
import akka.cluster.MemberStatus
import akka.cluster.typed.Cluster

import scala.util.Random

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:29:21
 */
object EntityIds {
  def entityId: String = "default"

  def entityId(system: ActorSystem[_]): String = "id-" + Random.nextInt(memberOnLimit(system))

  def memberOnLimit(system: ActorSystem[_]): Int = {
    val n = Cluster(system).state.members.count(_.status == MemberStatus.Up)
    if (n == 0) 1 else n
  }
}
