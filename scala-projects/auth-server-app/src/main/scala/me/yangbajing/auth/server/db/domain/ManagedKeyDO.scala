package me.yangbajing.auth.server.db.domain

import com.helloscala.akka.security.oauth.server.crypto.keys.{ KeyUtils, ManagedKey }
import helloscala.common.types.ObjectId

import java.io.StringReader
import java.security.PrivateKey
import java.time.Instant

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 10:34:23
 */
case class ManagedKeyDO(
    id: String,
    algorithm: String,
    key: String,
    publicKey: String,
    // client_id or openid
    identityId: String,
    activatedOn: Instant,
    deactivatedOn: Instant) {
  def toManagedKey: ManagedKey =
    ManagedKey(
      id,
      KeyUtils.getPrivateKey(KeyUtils.readPem(new StringReader(key)), algorithm),
      KeyUtils.getPublicKey(KeyUtils.readPem(new StringReader(publicKey)), algorithm),
      identityId,
      activatedOn,
      deactivatedOn)
}

object ManagedKeyDO {
  def from(client: ClientDetailDO, algorithm: String): ManagedKeyDO = {
    val keyPair = algorithm match {
      case KeyUtils.EC  => KeyUtils.generateEcKey()
      case KeyUtils.RSA => KeyUtils.generateRsaKey()
    }
    ManagedKeyDO(
      ObjectId.getString(),
      algorithm,
      KeyUtils.writePemPrivate(algorithm, keyPair.getPrivate.getEncoded),
      KeyUtils.writePemPublic(algorithm, keyPair.getPublic.getEncoded),
      client.id,
      Instant.now(),
      Instant.MAX)
  }

  def from(managedKey: ManagedKey, identityId: String): ManagedKeyDO = {
    require(
      KeyUtils.isValidAlgorithm(managedKey.getAlgorithm),
      s"The algorithm name '${managedKey.getAlgorithm}{}' is invalid, so the valid values are RSA or EC.")

    ManagedKeyDO(
      managedKey.id,
      managedKey.getAlgorithm,
      KeyUtils.writePemPrivate(managedKey.getAlgorithm, managedKey.getKey[PrivateKey].getEncoded),
      KeyUtils.writePemPublic(managedKey.getAlgorithm, managedKey.publicKey.getEncoded),
      identityId,
      managedKey.activatedOn,
      managedKey.deactivatedOn)
  }
}
