package blue.l955a6.incrementationAlert.domain.model

import blue.l955a6.incrementationAlert.domain.value.message.MessageContent
import blue.l955a6.incrementationAlert.domain.value.message.MessageId
import blue.l955a6.incrementationAlert.domain.value.message.MessageUrl

case class Message(
  id: MessageId,
  content: MessageContent,
  url: MessageUrl,
  user: User
)
