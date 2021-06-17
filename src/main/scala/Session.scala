package two.database.session

import cats.effect._
import cats.implicits._
import natchez.Trace
import org.flywaydb.core.Flyway
import skunk._
import two.database.config._

object DatabaseSession {

  /** Migrates the database before creating and returning a Skunk session pool
    *
    * @param config the database configuration. The flyway configuration
    * can be derived from skunk config if not provided.
    * @return a session pool resource
    */
  def pool[F[_]: ContextShift: Concurrent: Trace](
      config: DatabaseConfig
  ): Resource[F, Resource[F, Session[F]]] = {
    val skunk = config.skunk
    val flyway = config.flyway.getOrElse(FlywayConfig(skunk))

    for {
      _ <- Resource.eval(migrate(flyway))
      session <-
        Session.pooled(
          host = skunk.host,
          port = skunk.port,
          database = skunk.database,
          user = skunk.user,
          password = skunk.password,
          max = skunk.maxConcurrentSessions,
          debug = skunk.debug
        )
    } yield session
  }

  def migrate[F[_]: Sync](config: FlywayConfig): F[Any] = {
    flyway(config).map(_.migrate())
  }

  def clean[F[_]: Sync](config: FlywayConfig): F[Any] = {
    flyway(config).map(_.clean())
  }

  private def flyway[F[_]: Sync](
      config: FlywayConfig
  ): F[Flyway] = {
    Sync[F].delay {
      Flyway
        .configure()
        .createSchemas(true)
        .schemas(config.schema)
        .defaultSchema(config.schema)
        .dataSource(jdbc(config), config.user, config.password.orNull)
        .locations("migration")
        .load()
    }
  }

  private def jdbc(config: FlywayConfig): String = {
    s"jdbc:postgresql://${config.host}:${config.port}/${config.schema}"
  }
}
