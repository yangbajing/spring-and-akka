package com.helloscala.akka.security.oauth.server.jwt

import java.security.PrivateKey
import java.security.interfaces.ECPrivateKey
import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }
import akka.actor.typed.{ ActorRef, Behavior }
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, EntityContext, EntityRef, EntityTypeKey }
import akka.pattern.StatusReply
import akka.util.Timeout
import com.helloscala.akka.security.exception.AkkaSecurityException
import com.helloscala.akka.security.oauth.jose.JoseHeader
import com.helloscala.akka.security.oauth.jwt.Jwt
import com.helloscala.akka.security.oauth.server.authentication.OAuth2ClientCredentialsAuthentication
import com.helloscala.akka.security.oauth.server.crypto.keys.{ KeyManager, ManagedKey }
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.{ ECDSASigner, MACSigner, RSASSASigner }
import com.nimbusds.jwt.{ JWTClaimsSet, SignedJWT }
import helloscala.fusion.cloud.EntityIds

import javax.crypto.SecretKey
import scala.concurrent.duration._

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-09-19 18:28:55
 */
object JwtEncoder {
  trait Command
  val TypeKey: EntityTypeKey[Command] = EntityTypeKey("JwtEncoder")
//  val Key: ServiceKey[Command] = ServiceKey[Command]("JwtEncoder")

  case class Encode(
      authentication: OAuth2ClientCredentialsAuthentication,
      joseHeader: JoseHeader,
      jwtClaim: JWTClaimsSet,
      replyTo: ActorRef[StatusReply[Jwt]])
      extends Command

  case class EncodeWithManagedKey(
      managedKey: ManagedKey,
      joseHeader: JoseHeader,
      jwtClaim: JWTClaimsSet,
      replyTo: ActorRef[StatusReply[Jwt]])
      extends Command

//  case class KeyManagerWrapper(listing: Receptionist.Listing) extends Command
}

import com.helloscala.akka.security.oauth.server.jwt.JwtEncoder._
class DefaultJwtEncoder(context: ActorContext[Command]) {
  implicit private val system = context.system
  implicit private val ec = system.executionContext
  implicit private val timeout: Timeout = 5.seconds

//  context.system.receptionist
//    .tell(Receptionist.Find(KeyManager.Key, context.messageAdapter[Receptionist.Listing](KeyManagerWrapper)))

  def keyManager: EntityRef[KeyManager.Command] =
    ClusterSharding(system).entityRefFor(KeyManager.TypeKey, EntityIds.entityId)

//  def inactive(stash: StashBuffer[Command]): Behavior[Command] = Behaviors.receiveMessage {
//    case KeyManagerWrapper(KeyManager.Key.Listing(listing)) =>
//      if (listing.isEmpty) Behaviors.same
//      else stash.unstashAll(active(listing.head))
//
//    case message =>
//      stash.stash(message)
//      Behaviors.same
//  }

  def active( /*keyManager: ActorRef[KeyManager.Command]*/ ): Behavior[Command] = Behaviors.receiveMessagePartial {
    case Encode(authentication, joseHeader, jwtClaim, replyTo) =>
      val keyId = authentication.registeredClient.keyId
      val f = keyManager.ask[Option[ManagedKey]](ref => KeyManager.FindById(keyId, ref))
      f.foreach {
        case Some(managedKey) => context.self ! EncodeWithManagedKey(managedKey, joseHeader, jwtClaim, replyTo)
        case None             => replyTo ! StatusReply.error(s"ManagedKey not found, key id is $keyId")
      }
      Behaviors.same

    case EncodeWithManagedKey(managedKey, joseHeader, claim, replyTo) =>
      try {
        val jwsSigner: JWSSigner = if (managedKey.isAsymmetric) {
          managedKey.getAlgorithm match {
            case "RSA" => new RSASSASigner(managedKey.getKey[PrivateKey])
            case "EC"  => new ECDSASigner(managedKey.getKey[ECPrivateKey])
            case _     => throw new AkkaSecurityException(s"Unsupported key type '${managedKey.getAlgorithm}'.")
          }
        } else {
          val secretKey = managedKey.getKey[SecretKey]
          new MACSigner(secretKey)
        }
        val jwsHeader = joseHeader.toJwsHeader(managedKey.id)
        val jwtClaim = new JWTClaimsSet.Builder(claim).jwtID(managedKey.id).build()
        val signedJWT = new SignedJWT(jwsHeader, jwtClaim)
        signedJWT.sign(jwsSigner)
        val tokenValue = signedJWT.serialize()
        val jwt = Jwt(tokenValue, jwtClaim.getIssueTime.toInstant, jwtClaim.getExpirationTime.toInstant)
        replyTo ! StatusReply.success(jwt)
      } catch {
        case e: Throwable =>
          replyTo ! StatusReply.error(e)
      }
      Behaviors.same

//    case KeyManagerWrapper(KeyManager.Key.Listing(listing)) =>
//      if (listing.isEmpty) Behaviors.same
//      else active(listing.head)
  }
}
object DefaultJwtEncoder {
  def apply(entityContext: EntityContext[Command]): Behavior[Command] =
    Behaviors.setup(context => new DefaultJwtEncoder(context).active())
}
