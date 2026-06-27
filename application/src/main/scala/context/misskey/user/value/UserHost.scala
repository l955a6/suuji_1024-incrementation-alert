package blue.l955a6.incrementationMonitor.application.context.misskey.user.value

import blue.l955a6.incrementationMonitor.application.platform.json.OpaqueCodec
import io.circe.Codec

opaque type UserHost = String

object UserHost {
  def apply(value: String): UserHost = value

  given Codec[UserHost] = OpaqueCodec.opaqueTypeCodec(UserHost.apply)
}
