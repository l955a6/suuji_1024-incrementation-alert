package blue.l955a6.incrementationAlert.domain.value.message

opaque type MessageUrl = String

object MessageUrl {
  def apply(value: String): MessageUrl =
    // TODO: バリデーションの追加
    value
}
