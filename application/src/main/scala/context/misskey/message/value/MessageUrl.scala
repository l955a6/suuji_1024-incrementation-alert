package blue.l955a6.incrementationMonitor.application.context.misskey.message.value

import blue.l955a6.incrementationMonitor.application.platform.json.OpaqueCodec
import io.circe.Codec

opaque type MessageUrl = String

object MessageUrl {
  def apply(value: String): MessageUrl =
    // TODO: バリデーションの追加
    value

  given Codec[MessageUrl] = OpaqueCodec.opaqueTypeCodec(MessageUrl.apply)
}
