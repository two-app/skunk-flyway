package two.database.usr.session

import cats.effect._
import skunk.implicits._
import skunk.codec.all._
import two.database.usr.config._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import natchez.Trace.Implicits.noop

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
          n <- session.unique(sql"select 3".query(int4))
        } yield assertEquals(n, 3)
      }
    }

    test.use(identity).unsafeToFuture()
  }
}
