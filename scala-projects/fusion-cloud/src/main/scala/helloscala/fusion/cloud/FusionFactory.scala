package helloscala.fusion.cloud

import akka.actor.typed.{ ActorSystem, Behavior }

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-01 23:40:06
 */
trait FusionFactory extends FusionActorSystemFactory {
  def actorSystem: ActorSystem[_]
}

trait FusionActorSystemFactory {
  def createActorSystem[T](guardianBehavior: Behavior[T]): ActorSystem[T]
}
