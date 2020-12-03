package com.helloscala.akka.security.oauth.server.jwt

import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }
import akka.actor.typed.{ ActorRef, Behavior }
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, EntityContext, EntityRef, EntityTypeKey }
import akka.pattern.StatusReply
import akka.util.Timeout
import com.helloscala.akka.security.exception.AkkaSecurityException
import com.helloscala.akka.security.oauth.jose.JoseHeader
import com.helloscala.akka.security.oauth.jwt.Jwt
import com.helloscala.akka.security.oauth.server.authentication.OAuth2ClientCredentialsAuthentication
import com.helloscala.akka.security.oauth.server.crypto.keys.{ KeyManager, KeyUtils, ManagedKey }
import com.nimbusds.jose.crypto.{ ECDSASigner, RSASSASigner }
import com.nimbusds.jose.{ JWSHeader, JWSSigner }
import com.nimbusds.jwt.{ JWTClaimsSet, SignedJWT }
import helloscala.fusion.cloud.EntityIds

import java.security.PrivateKey
import java.security.interfaces.ECPrivateKey
import scala.concurrent.duration._

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-09-19 18:28:55
 */
object JwtEncoder {
  trait Command
  val TypeKey: EntityTypeKey[Command] = EntityTypeKey("JwtEncoder")

  case class Encode(
      authentication: OAuth2ClientCredentialsAuthentication,
      joseHeader: Option[JoseHeader],
      jwtClaim: JWTClaimsSet,
      replyTo: ActorRef[StatusReply[Jwt]])
      extends Command

  case class EncodeWithManagedKey(
      managedKey: ManagedKey,
      joseHeader: JoseHeader,
      jwtClaim: JWTClaimsSet,
      replyTo: ActorRef[StatusReply[Jwt]])
      extends Command
}

import com.helloscala.akka.security.oauth.server.jwt.JwtEncoder._
class DefaultJwtEncoder(context: ActorContext[Command]) {
  implicit private val system = context.system
  implicit private val ec = system.executionContext
  implicit private val timeout: Timeout = 5.seconds

  def keyManager: EntityRef[KeyManager.Command] =
    ClusterSharding(system).entityRefFor(KeyManager.TypeKey, EntityIds.entityId)

  def active(): Behavior[Command] = Behaviors.receiveMessagePartial {
    case Encode(authentication, joseHeaderOption, jwtClaim, replyTo) =>
      val identityId = authentication.registeredClient.id
      val f = keyManager.ask[Option[ManagedKey]](ref => KeyManager.FindByIdentityId(identityId, ref))
      f.foreach {
        case Some(managedKey) =>
          val joseHeader = joseHeaderOption.getOrElse {
            JoseHeader(new JWSHeader.Builder(KeyUtils.toJwsAlgorithm(managedKey.getAlgorithm)).build())
          }
          context.self ! EncodeWithManagedKey(managedKey, joseHeader, jwtClaim, replyTo)
        case None => replyTo ! StatusReply.error(s"ManagedKey not found, identityId is $identityId")
      }
      Behaviors.same

    case EncodeWithManagedKey(managedKey, joseHeader, claim, replyTo) =>
      try {
        val jwsSigner: JWSSigner = managedKey.getAlgorithm match {
          case KeyUtils.EC  => new ECDSASigner(managedKey.getKey[ECPrivateKey])
          case KeyUtils.RSA => new RSASSASigner(managedKey.getKey[PrivateKey])
          case _            => throw new AkkaSecurityException(s"Unsupported key type '${managedKey.getAlgorithm}'.")
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
  }
}
object DefaultJwtEncoder {
  def apply(entityContext: EntityContext[Command]): Behavior[Command] =
    Behaviors.setup(context => new DefaultJwtEncoder(context).active())
}
