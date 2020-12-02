package me.yangbajing.auth.server.grpc.impl

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.{ Sink, Source }
import auth.grpc.{ GreeterService, HelloReply, HelloRequest }
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-02 16:13:14
 */
class GreeterServiceImpl(system: ActorSystem[_]) extends GreeterService with StrictLogging {
  import system.executionContext
  implicit val ts = system

  /**
   * ////////////////////
   * Sends a greeting //
   * //////&#42;****&#47;////////
   *      HELLO       //
   * //////&#42;****&#47;////////
   */
  override def sayHello(in: HelloRequest): Future[HelloReply] = {
    Future.successful(HelloReply(s"Hi ${in.name}, this is Scala."))
  }

  /**
   * Comment spanning
   * on several lines
   */
  override def itKeepsTalking(in: Source[HelloRequest, NotUsed]): Future[HelloReply] =
    in.throttle(10, 5.seconds).runWith(Sink.seq).map(list => HelloReply(list.map(_.name).mkString(" ")))

  /**
   *  C style comments
   */
  override def itKeepsReplying(in: HelloRequest): Source[HelloReply, NotUsed] = Source.single(HelloReply(in.name))

  /**
   * C style comments
   * on several lines
   * with non-empty heading/trailing line */
  override def streamHellos(in: Source[HelloRequest, NotUsed]): Source[HelloReply, NotUsed] =
    in.map(req => HelloReply(req.name))
}
