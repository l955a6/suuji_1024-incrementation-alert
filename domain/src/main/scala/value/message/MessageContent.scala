package blue.l955a6.incrementationAlert.domain.value.message

opaque type MessageContent = String

object MessageContent {
  def apply(value: String): MessageContent = value
}
