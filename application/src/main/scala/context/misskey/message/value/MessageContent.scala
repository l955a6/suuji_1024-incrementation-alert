package blue.l955a6.incrementationMonitor.application.context.misskey.message.value

import blue.l955a6.incrementationMonitor.application.platform.json.OpaqueCodec
import io.circe.Codec

opaque type MessageContent = String

object MessageContent {
  def apply(value: String): MessageContent = value

  given Codec[MessageContent] = OpaqueCodec.opaqueTypeCodec(MessageContent.apply)
}
