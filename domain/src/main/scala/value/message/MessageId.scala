package blue.l955a6.incrementationAlert.domain.value.message

opaque type MessageId = String

object MessageId {
  def apply(value: String): MessageId = value
}
