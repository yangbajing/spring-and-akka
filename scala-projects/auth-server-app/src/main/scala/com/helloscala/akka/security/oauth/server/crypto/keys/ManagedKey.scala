package com.helloscala.akka.security.oauth.server.crypto.keys

import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey
import java.time.Instant

import javax.crypto.SecretKey

import scala.reflect.ClassTag

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-09-20 13:53:27
 */
case class ManagedKey(
    id: String,
    // client_id or openid
    key: Key,
    publicKey: PublicKey,
    identityId: String = "",
    activatedOn: Instant = Instant.now(),
    deactivatedOn: Instant = Instant.MAX) {

  def getAlgorithm: String = key.getAlgorithm
  def getKey[T <: Key: ClassTag]: T = key.asInstanceOf[T]
  def isActive: Boolean = deactivatedOn.isAfter(Instant.now())
//  def isSymmetric: Boolean = classOf[SecretKey].isAssignableFrom(key.getClass)
  def isAsymmetric: Boolean = classOf[PrivateKey].isAssignableFrom(key.getClass)
}
