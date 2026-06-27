package blue.l955a6.incrementationMonitor.application.context.misskey.user.value

import blue.l955a6.incrementationMonitor.application.platform.json.OpaqueCodec
import io.circe.Codec

opaque type UserId = String

object UserId {
  def apply(value: String): UserId = value

  given Codec[UserId] = OpaqueCodec.opaqueTypeCodec(UserId.apply)
}
