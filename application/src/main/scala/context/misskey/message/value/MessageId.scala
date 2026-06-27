package blue.l955a6.incrementationMonitor.application.context.misskey.message.value

import blue.l955a6.incrementationMonitor.application.platform.json.OpaqueCodec
import io.circe.Codec

opaque type MessageId = String

object MessageId {
  def apply(value: String): MessageId = value

  given Codec[MessageId] = OpaqueCodec.opaqueTypeCodec(MessageId.apply)
}
