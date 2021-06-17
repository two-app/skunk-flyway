package two.database.session

import scala.concurrent.ExecutionContext

import cats.effect._
import natchez.Trace.Implicits.noop
import two.database.config._

import ExecutionContext.Implicits.global

class SessionTest extends munit.FunSuite {

  implicit val CS: ContextShift[IO] =
    IO.contextShift(global)

  implicit def timer(implicit ec: ExecutionContext): Timer[IO] =
    IO.timer(ec)

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
