package blue.l955a6.incrementationMonitor.application.context.misskey.user.value

opaque type UserHost = String

object UserHost {
  def apply(value: String): UserHost = value
}
