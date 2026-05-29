package blue.l955a6.incrementationAlert.domain.model

import blue.l955a6.incrementationAlert.domain.value.message.MessageContent
import blue.l955a6.incrementationAlert.domain.value.message.MessageId
import blue.l955a6.incrementationAlert.domain.value.message.MessageUrl
import blue.l955a6.incrementationAlert.domain.value.number.IncrementationNumber

final case class IncrementationMessage(
  id: MessageId,
  content: MessageContent,
  incrementationNumber: IncrementationNumber,
  url: MessageUrl,
  user: User
)
