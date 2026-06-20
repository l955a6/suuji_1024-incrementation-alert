package blue.l955a6.incrementationMonitor.context.misskey.message.value

opaque type MessageId = String

object MessageId {
  def apply(value: String): MessageId = value
}
