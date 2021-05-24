package two.database.usr.config

import scala.concurrent.duration._

import skunk.Session

class ConfigTest extends munit.FunSuite {
  test("Skunk Config Defaults") {
    val expected = SkunkConfig(
      host = "test-host",
      database = "test-db",
      port = 5432,
      user = "postgres",
      password = None,
      maxConcurrentSessions = 5,
      debug = false,
      readTimeout = Int.MaxValue.seconds,
      writeTimeout = 5.seconds,
      parameters = Session.DefaultConnectionParameters
    )

    val actual = SkunkConfig("test-host", "test-db")

    assertEquals(actual, expected)
  }

  test("Derived Flyway Configuration") {
    val skunk = SkunkConfig(host = "test-host", database = "test-db")
    val expected = FlywayConfig(
      host = skunk.host,
      schema = skunk.database,
      user = skunk.user,
      password = skunk.password
    )

    val actual = FlywayConfig(skunk)

    assertEquals(actual, expected)
  }
}
