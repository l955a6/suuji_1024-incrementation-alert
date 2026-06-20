package blue.l955a6.incrementationMonitor.application.context.misskey.user.value

opaque type UserId = String

object UserId {
  def apply(value: String): UserId = value
}
