package com.helloscala.akka.security.oauth.server.crypto.keys

import com.nimbusds.jose.jwk._
import com.nimbusds.jose.{ JOSEException, JWSAlgorithm }
import helloscala.common.types.ObjectId
import helloscala.common.util.Utils
import org.apache.commons.lang3.StringUtils
import org.bouncycastle.util.io.pem.{ PemObject, PemReader, PemWriter }

import java.io._
import java.math.BigInteger
import java.security._
import java.security.interfaces.{ ECPublicKey, RSAPublicKey }
import java.security.spec._
import java.time.Instant
import javax.crypto.SecretKey

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-09-20 14:06:18
 */
object KeyUtils {
  val RSA = "RSA"
  val EC = "EC"

  def toJwsAlgorithm(algorithm: String): JWSAlgorithm = algorithm match {
    case KeyUtils.EC  => JWSAlgorithm.ES256
    case KeyUtils.RSA => JWSAlgorithm.RS256
  }

  def isValidAlgorithm(algorithm: String): Boolean = algorithm == EC || algorithm == RSA

  def writePemPublic(`type`: String, encoded: Array[Byte]): String =
    writePemString(
      if (StringUtils.isBlank(`type`)) "PUBLIC KEY"
      else `type` + " PUBLIC KEY",
      encoded)

  def writePemPrivate(`type`: String, encoded: Array[Byte]): String =
    writePemString(
      if (StringUtils.isBlank(`type`)) "PRIVATE KEY"
      else `type` + " PRIVATE KEY",
      encoded)

  private def writePemString(`type`: String, encoded: Array[Byte]) = {
    val writer = new StringWriter
    try {
      writePem(writer, `type`, encoded)
      writer.toString
    } catch {
      case e: IOException =>
        throw new IllegalStateException(e)
    } finally Utils.closeQuiet(writer)
  }

  def getPublicKey(pemObject: PemObject, algorithm: String): PublicKey =
    try {
      val factory = KeyFactory.getInstance(algorithm)
      factory.generatePublic(new X509EncodedKeySpec(pemObject.getContent))
    } catch {
      case e @ (_: NoSuchAlgorithmException | _: InvalidKeySpecException) =>
        throw new IllegalStateException(e)
    }

  def getPrivateKey(pemObject: PemObject, algorithm: String): PrivateKey =
    try {
      val factory = KeyFactory.getInstance(algorithm)
      factory.generatePrivate(new PKCS8EncodedKeySpec(pemObject.getContent))
    } catch {
      case e @ (_: NoSuchAlgorithmException | _: InvalidKeySpecException) =>
        throw new IllegalStateException(e)
    }

  def readPem(reader: Reader): PemObject = {
    val pemReader = new PemReader(reader)
    try pemReader.readPemObject
    catch {
      case e: IOException =>
        throw new IllegalStateException(e)
    } finally Utils.closeQuiet(pemReader)
  }

  def writePem(writer: Writer, `type`: String, encoded: Array[Byte]): Unit = {
    val pemWriter = new PemWriter(writer)
    try {
      pemWriter.writeObject(new PemObject(`type`, encoded))
      pemWriter.flush()
    } catch {
      case e: IOException =>
        throw new IllegalStateException(e)
    }
  }

  @throws[IOException]
  def toBytes(key: PublicKey): Array[Byte] = {
    val byteOut = new ByteArrayOutputStream
    val out = new ObjectOutputStream(byteOut)
    try {
      out.writeObject(key)
      out.flush()
      byteOut.toByteArray
    } finally Utils.closeQuiet(out)
  }

  @throws[IOException]
  def toBytes(key: PrivateKey): Array[Byte] = {
    val byteOut = new ByteArrayOutputStream
    val out = new ObjectOutputStream(byteOut)
    try {
      out.writeObject(key)
      out.flush()
      byteOut.toByteArray
    } finally Utils.closeQuiet(out)
  }

  @throws[IOException]
  def toBytes(key: SecretKey): Array[Byte] = {
    val byteOut = new ByteArrayOutputStream
    val out = new ObjectOutputStream(byteOut)
    try {
      out.writeObject(key)
      out.flush()
      byteOut.toByteArray
    } finally Utils.closeQuiet(out)
  }

  @throws[IOException]
  @throws[ClassNotFoundException]
  def toPublicKey(bytes: Array[Byte]): PublicKey = {
    val in = new ObjectInputStream(new ByteArrayInputStream(bytes))
    try in.readObject.asInstanceOf[PublicKey]
    finally Utils.closeQuiet(in)
  }

  @throws[IOException]
  @throws[ClassNotFoundException]
  def toPrivateKey(bytes: Array[Byte]): PrivateKey = {
    val in = new ObjectInputStream(new ByteArrayInputStream(bytes))
    try in.readObject.asInstanceOf[PrivateKey]
    finally Utils.closeQuiet(in)
  }

  def convert(managedKey: ManagedKey): Option[JWK] = {
    managedKey.publicKey match {
      case publicKey: RSAPublicKey =>
        val rsaKey = new RSAKey.Builder(publicKey)
          .keyUse(KeyUse.SIGNATURE)
          .algorithm(JWSAlgorithm.RS256)
          .keyID(managedKey.id)
          .build
        Some(rsaKey)
      case publicKey: ECPublicKey =>
        val curve = Curve.forECParameterSpec(publicKey.getParams)
        val ecKey = new ECKey.Builder(curve, publicKey)
          .keyUse(KeyUse.SIGNATURE)
          .algorithm(JWSAlgorithm.ES256)
          .keyID(managedKey.id)
          .build
        Some(ecKey)
      case _ =>
        None
    }
  }

  def generateRsaKey(keysize: Int = 2048): KeyPair = {
    try {
      val keyPairGenerator = KeyPairGenerator.getInstance(KeyUtils.RSA)
      keyPairGenerator.initialize(keysize)
      keyPairGenerator.generateKeyPair
    } catch {
      case ex: Exception =>
        throw new IllegalStateException(ex)
    }
  }

  def generateRsaManagedKey(keysize: Int = 2048): ManagedKey = {
    val keyPair = generateRsaKey(keysize)
    ManagedKey(ObjectId.getString(), keyPair.getPrivate, keyPair.getPublic)
  }

  def generateEcKey(): KeyPair = {
    val ellipticCurve = new EllipticCurve(
      new ECFieldFp(new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")),
      new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948"),
      new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291"))
    val ecPoint = new ECPoint(
      new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"),
      new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109"))
    val ecParameterSpec = new ECParameterSpec(
      ellipticCurve,
      ecPoint,
      new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"),
      1)
    try {
      val keyPairGenerator = KeyPairGenerator.getInstance(KeyUtils.EC)
      keyPairGenerator.initialize(ecParameterSpec)
      keyPairGenerator.generateKeyPair
    } catch {
      case ex: Exception =>
        throw new IllegalStateException(ex)
    }
  }

  def generateEcManagedKey(ellipticCurve: EllipticCurve, ecPoint: ECPoint, n: BigInteger, h: Int): ManagedKey = {
    val ecParameterSpec = new ECParameterSpec(ellipticCurve, ecPoint, n, h)
    try {
      val keyPairGenerator = KeyPairGenerator.getInstance("EC")
      keyPairGenerator.initialize(ecParameterSpec)
      val keyPair = keyPairGenerator.generateKeyPair
      ManagedKey(ObjectId.getString(), keyPair.getPrivate, keyPair.getPublic)
    } catch {
      case ex: Exception =>
        throw new IllegalStateException(ex)
    }
  }

  @throws[JOSEException]
  def toManagedKey(jwk: JWK): ManagedKey = {
    jwk match {
      case rsaKey: RSAKey =>
        ManagedKey(jwk.getKeyID, rsaKey.toPrivateKey, rsaKey.toPublicKey)
      case ecKey: ECKey =>
        ManagedKey(jwk.getKeyID, ecKey.toPrivateKey, ecKey.toPublicKey)
      case _ => throw new IllegalStateException(s"Invalid key type that is $jwk, need be RSA or EC.")
    }
  }

  def generateKeys(): List[ManagedKey] = {
    val rsaKeyPair = generateRsaKey()
    val rsaManagedKey =
      ManagedKey("rsa-key", rsaKeyPair.getPrivate, rsaKeyPair.getPublic, "", Instant.now(), Instant.MAX)
    val ecKeyPair = generateEcKey()
    val ecManagedKey = ManagedKey("ec-key", ecKeyPair.getPrivate, ecKeyPair.getPublic, "", Instant.now(), Instant.MAX)
    List(rsaManagedKey, ecManagedKey)
  }
}
