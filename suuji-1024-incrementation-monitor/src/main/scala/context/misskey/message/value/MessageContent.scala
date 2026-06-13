package blue.l955a6.incrementationMonitor.context.misskey.message.value

opaque type MessageContent = String

object MessageContent {
  def apply(value: String): MessageContent = value
}
