package skunk.codec.extra

import java.time.{Instant, ZoneOffset}

import skunk.Codec
import skunk.codec.all._

object Temporal {
  val instant: Codec[Instant] =
    timestamptz.imap(_.toInstant)(_.atOffset(ZoneOffset.UTC))
}

package object all {
  val instant: Codec[Instant] = Temporal.instant
}
