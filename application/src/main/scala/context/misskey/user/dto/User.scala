package blue.l955a6.incrementationMonitor.application.context.misskey.user.dto

import blue.l955a6.incrementationMonitor.application.context.misskey.user.value.UserHost
import blue.l955a6.incrementationMonitor.application.context.misskey.user.value.UserId
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class User(id: UserId, host: UserHost)

object User {
  given Decoder[User] = deriveDecoder
}
