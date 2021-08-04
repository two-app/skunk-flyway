package skunk.codec.extra

import java.time.Instant

import cats.effect.IO
import natchez.Trace.Implicits.noop
import skunk.codec.extra.all._
import skunk.implicits._
import two.database.config._
import two.database.session.DatabaseSession

class CodecSuite extends munit.CatsEffectSuite {
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

  val insert = sql"INSERT INTO world.country VALUES ($instant)".command
  val query = sql"SELECT * FROM world.country".query(instant)

  test("codec for Instant") {
    cleanMigrate().unsafeRunSync()

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
