package blue.l955a6.incrementationMonitor.application.context.misskey.message.value

opaque type MessageUrl = String

object MessageUrl {
  def apply(value: String): MessageUrl =
    // TODO: バリデーションの追加
    value
}
