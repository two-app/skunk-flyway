package two.database.usr.config

import scala.concurrent.duration._

import skunk.Session

case class SkunkConfig(
    host: String,
    database: String,
    port: Int = 5432,
    user: String = "postgres",
    password: Option[String] = None,
    maxConcurrentSessions: Int = 5,
    debug: Boolean = false,
    readTimeout: FiniteDuration = Int.MaxValue.seconds,
    writeTimeout: FiniteDuration = 5.seconds,
    parameters: Map[String, String] = Session.DefaultConnectionParameters
)

case class FlywayConfig(
    host: String,
    schema: String,
    port: Int = 5432,
    user: String = "postgres",
    password: Option[String] = None,
    locations: String = "migration"
)

object FlywayConfig {
  def apply(skunk: SkunkConfig): FlywayConfig = FlywayConfig(
    host = skunk.host,
    schema = skunk.database,
    user = skunk.user,
    password = skunk.password
  )
}

case class DatabaseConfig(
    skunk: SkunkConfig,
    flyway: Option[FlywayConfig] = None
)
