package blue.l955a6.incrementationAlert.domain.model

import blue.l955a6.incrementationAlert.domain.value.user.UserHost
import blue.l955a6.incrementationAlert.domain.value.user.UserId

final case class User(id: UserId, host: UserHost)
