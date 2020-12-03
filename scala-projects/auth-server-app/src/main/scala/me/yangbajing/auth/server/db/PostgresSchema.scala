package me.yangbajing.auth.server.db

import akka.actor.typed.{ ActorSystem, Extension, ExtensionId }
import me.yangbajing.auth.server.db.repository.{ ClientDetailRepository, ManagedKeyRepository }

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 11:35:53
 */
class PostgresSchema(system: ActorSystem[_]) extends Extension {
  val profile = AuthProfile
  val db: AuthProfile.backend.DatabaseDef = profile.api.Database.forConfig("fusion.jdbc.datasource")
  system.classicSystem.registerOnTermination(() => db.close())

  val managedKeyRepository = new ManagedKeyRepository(profile, db)
  val clientDetailRepository = new ClientDetailRepository(profile, db)

}
object PostgresSchema extends ExtensionId[PostgresSchema] {
  override def createExtension(system: ActorSystem[_]): PostgresSchema = new PostgresSchema(system)
}
