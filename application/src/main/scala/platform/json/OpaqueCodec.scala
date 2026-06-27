package blue.l955a6.incrementationMonitor.application.platform.json

import io.circe.{Codec, Decoder, Encoder}

object OpaqueCodec {
  inline def opaqueTypeCodec[Base: Encoder: Decoder, Opaque](toOpaque: Base => Opaque): Codec[Opaque] =
    Codec.from(
      summon[Decoder[Base]].map(toOpaque),
      summon[Encoder[Base]].contramap(_.asInstanceOf[Base])
    )
}
