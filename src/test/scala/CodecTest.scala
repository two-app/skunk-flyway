package skunk.codec.extra

import java.time.Instant

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO, _}
import natchez.Trace.Implicits.noop
import skunk.codec.extra.all._
import skunk.implicits._
import two.database.config._
import two.database.session.DatabaseSession

import ExecutionContext.Implicits.global

class CodecSuite extends munit.FunSuite {
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

  val insert = sql"INSERT INTO country VALUES ($instant)".command
  val query = sql"SELECT * FROM country".query(instant)

  test("codec for Instant") {
    val test = for {
      pool <- DatabaseSession.pool[IO](db)
    } yield {
      val now = Instant.now()
      pool.use { session =>
        for {
          _ <- session.prepare(insert).use(_.execute(now))
          dt <- session.unique(query)
        } yield assertEquals(dt, now)
      }
    }

    test.use(identity).unsafeToFuture()
  }

}
