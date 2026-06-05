package blue.l955a6.incrementationAlert.domain.state

import blue.l955a6.incrementationAlert.domain.model.IncrementationMessage
import blue.l955a6.incrementationAlert.domain.model.Message
import blue.l955a6.incrementationAlert.domain.value.number.IncrementationNumberDigits

/**
 * インクリメントが1ずつ行われているか、同じ数字が重複していないかを検証するステートマシンです。
 *
 * @param initialNumber
 *   どの数字からインクリメントを開始するか。
 * @param maxNumber
 *   どの数字でインクリメントを完了とするか。 SNSにメッセージとして投稿できる文字数の上限など、プラットフォームの事情により設定される。
 */
final case class Suuji1024IncrementationStateMachine(
  initialNumberDigits: IncrementationNumberDigits,
  maxNumberDigits: IncrementationNumberDigits,
  state: Suuji1024IncrementationStateMachine.State
) {
  import Suuji1024IncrementationStateMachine.*

  require(
    initialNumberDigits.width == maxNumberDigits.width,
    s"$this の initialNumberDigits と maxNumberDigits の半角/全角は揃っている必要があります"
  )

  // TODO: initialNumberDigitsの大きさ < maxNumberDigitsの大きさ であることを検証する

  def send(event: Suuji1024IncrementationStateMachine.Event): Suuji1024IncrementationStateMachine =
    event match {
      case Event.Incrementation(incrementationMessage) =>
        state match
          case State.Idle =>
            if (incrementationMessage.numberDigits == initialNumberDigits)
              copy(state = State.Monitoring(incrementationMessage))
            else this
          case currentState @ State.Monitoring(lastAcceptedIncrementationMessage) =>
            val next =
              if (
                incrementationMessage.numberDigits.isIncrementedFrom(
                  lastAcceptedIncrementationMessage.numberDigits
                ) && incrementationMessage.numberDigits == maxNumberDigits
              ) State.Completed(incrementationMessage)
              else if (
                incrementationMessage.numberDigits.isIncrementedFrom(
                  lastAcceptedIncrementationMessage.numberDigits
                )
              ) State.Monitoring(incrementationMessage)
              else if (incrementationMessage.numberDigits == initialNumberDigits)
                // 初期の数値と同じ数値が流れてきた場合、それはインクリメントを意図しているのではなく
                // 単に名前を読んだだけの可能性が高いのでエラーにはせずスルーする
                currentState
              else
                // TODO: 長期的にインクリメントしていくことを意識せず、ちょっとだけインクリメントしようとする人がいるかもしれないので
                //       インクリメントを監視し始める閾値を導入することを考える
                State.Failure(
                  lastAcceptedIncrementationMessage,
                  incrementationMessage
                )
            copy(state = next)
          case State.Failure(_, _) | State.Completed(_) =>
            val next =
              if (incrementationMessage.numberDigits == initialNumberDigits)
                State.Monitoring(incrementationMessage)
              else State.Idle
            copy(state = next)
      case Event.NormalMessage(message) =>
        state match {
          case State.Failure(_, _) | State.Completed(_) =>
            copy(state = State.Idle)
          case _ =>
            this
        }
    }
}

object Suuji1024IncrementationStateMachine {
  enum State {

    /**
     * インクリメント監視を行っていない状態です。
     *
     * この状態で特定のメッセージを受け取るとインクリメント監視を開始します。
     *
     * このような命名ですがすうじ１０２４があずきインターネットのアイドルであることとは関係ありません。
     */
    case Idle

    case Monitoring(lastAcceptedIncrementationMessage: IncrementationMessage)

    case Completed(lastAcceptedIncrementationMessage: IncrementationMessage)

    case Failure(
      lastAcceptedIncrementationMessage: IncrementationMessage,
      invalidIncrementationMessage: IncrementationMessage
    )
  }

  enum Event {
    case Incrementation(incrementationMessage: IncrementationMessage)

    case NormalMessage(message: Message)
  }
}
