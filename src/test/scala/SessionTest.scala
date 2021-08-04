package two.database.session

import cats.effect._
import natchez.Trace.Implicits.noop
import two.database.config._

class SessionTest extends munit.CatsEffectSuite {
  val db = DatabaseConfig(
    skunk = SkunkConfig(
      host = "localhost",
      database = "world",
      password = Some("helloworld")
    )
  )

  val flyway = FlywayConfig(db.skunk)

  def cleanMigrate(): IO[Unit] = for {
    _ <- DatabaseSession.clean[IO](flyway)
    _ <- DatabaseSession.migrate[IO](flyway)
  } yield ()

  test("Cleans and migrates the database") {
    cleanMigrate().unsafeToFuture()
  }

  test("Inserting and retrieving data via the Skunk pool") {
    val test = for {
      pool <- DatabaseSession.pool[IO](db)
    } yield {
      pool.use { session =>
        for {
          res <- SkunkHealthDao.fromSession[IO](session).check
        } yield assertEquals(res, ())
      }
    }

    test.use(identity).unsafeToFuture()
  }
}
