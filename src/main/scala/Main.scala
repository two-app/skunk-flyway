import cats.effect._
import natchez.Trace.Implicits.noop
import skunk._
import skunk.codec.all._
import skunk.implicits._
import two.database.config._
import two.database.session.DatabaseSession

object Hello extends IOApp {

  val session: Resource[IO, Resource[IO, Session[IO]]] = DatabaseSession.pool(
    DatabaseConfig(skunk =
      SkunkConfig("localhost", "world", 5432, "jimmy", Some("banana"))
    )
  )

  def run(args: List[String]): IO[ExitCode] =
    session.use { s => doRun(s) }

  def doRun(se: Resource[IO, Session[IO]]) = {
    se.use { s =>
      for {
        d <- s.unique(sql"select current_date".query(date))
        _ <- IO(println(s"The current date is $d."))
      } yield ExitCode.Success
    }
  }
}
