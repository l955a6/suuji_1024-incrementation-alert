package blue.l955a6.incrementationAlert.domain.value.user

opaque type UserHost = String

object UserHost {
  def apply(value: String): UserHost = value
}
