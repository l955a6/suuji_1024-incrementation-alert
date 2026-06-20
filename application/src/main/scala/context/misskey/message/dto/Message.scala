package blue.l955a6.incrementationMonitor.application.context.misskey.message.dto

import blue.l955a6.incrementationMonitor.application.context.misskey.message.value.MessageContent
import blue.l955a6.incrementationMonitor.application.context.misskey.message.value.MessageId
import blue.l955a6.incrementationMonitor.application.context.misskey.message.value.MessageUrl
import blue.l955a6.incrementationMonitor.application.context.misskey.user.dto.User

case class Message(
  id: MessageId,
  content: MessageContent,
  url: MessageUrl,
  user: User
)
