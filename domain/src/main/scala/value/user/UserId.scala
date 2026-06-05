package blue.l955a6.incrementationAlert.domain.value.user

opaque type UserId = String

object UserId {
  def apply(value: String): UserId = value
}
