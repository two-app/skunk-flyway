package two.database.session

import cats.Applicative
import cats.implicits._
import skunk._
import skunk.codec.all._
import skunk.implicits._

trait SkunkHealthDao[F[_]] {
  def check: F[Unit]
}

object SkunkHealthDao {
  val select: Query[Void, Int] = sql"SELECT 1".query(int4)

  def fromSession[F[_]: Applicative](s: Session[F]): SkunkHealthDao[F] = {
    new SkunkHealthDao[F] {
      def check: F[Unit] = s.unique(select).map(_ => ())
    }
  }
}
