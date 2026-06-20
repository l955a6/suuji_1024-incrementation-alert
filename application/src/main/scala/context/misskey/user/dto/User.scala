package blue.l955a6.incrementationMonitor.application.context.misskey.user.dto

import blue.l955a6.incrementationMonitor.application.context.misskey.user.value.UserHost
import blue.l955a6.incrementationMonitor.application.context.misskey.user.value.UserId

final case class User(id: UserId, host: UserHost)
