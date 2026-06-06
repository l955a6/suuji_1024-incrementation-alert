package blue.l955a6.incrementationAlert.domain.model

import blue.l955a6.incrementationAlert.domain.value.message.MessageContent
import blue.l955a6.incrementationAlert.domain.value.message.MessageId
import blue.l955a6.incrementationAlert.domain.value.message.MessageUrl
import blue.l955a6.incrementationAlert.domain.value.number.IncrementationNumberDigits

/**
 * TODO: numberDigitsがcontentに含まれていることをバリデーションする
 */
final case class IncrementationMessage(
  id: MessageId,
  content: MessageContent,
  numberDigits: IncrementationNumberDigits,
  url: MessageUrl,
  user: User
)
