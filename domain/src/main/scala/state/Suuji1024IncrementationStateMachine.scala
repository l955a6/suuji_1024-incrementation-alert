package blue.l955a6.incrementationAlert.domain.state

import blue.l955a6.incrementationAlert.domain.model.Message
import blue.l955a6.incrementationAlert.domain.value.number.IncrementationNumber

final case class Suuji1024IncrementationStateMachine(
  initialNumber: IncrementationNumber,
  state: Suuji1024IncrementationStateMachine.State
) {
  import Suuji1024IncrementationStateMachine.*

  def send(event: Suuji1024IncrementationStateMachine.Event): Suuji1024IncrementationStateMachine =
    event match {
      case Event.IncrementMessage(number, message) =>
        state match
          case State.Idle =>
            val next = State.Monitoring(number, message)
            copy(state = next)
          case currentState @ State.Monitoring(current, lastAcceptedIncrementationMessage) =>
            val next =
              if (number.isIncrementedFrom(current))
                State.Monitoring(number, message)
              else if (number == initialNumber)
                // 初期の数値と同じ数値が流れてきた場合、それはインクリメントを意図しているのではなく
                // 単に名前を読んだだけの可能性が高いのでエラーにはせずスルーする
                currentState
              else
                // TODO: 長期的にインクリメントしていくことを意識せず、ちょっとだけインクリメントしようとする人がいるかもしれないので
                //       インクリメントを監視し始める閾値を導入することを考える
                State.Failure(
                  current,
                  lastAcceptedIncrementationMessage,
                  message
                )
            copy(state = next)
          case State.Failure(
                lastNumber,
                lastAcceptedIncrementationMessage,
                invalidIncrementationMessage
              ) =>
            val next =
              if (number == initialNumber)
                State.Monitoring(
                  current = number,
                  lastAcceptedIncrementationMessage = message
                )
              else State.Idle
            copy(state = next)

      case Event.NormalMessage(message) =>
        state match {
          case State.Failure(_, _, _) =>
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

    case Monitoring(
      current: IncrementationNumber,
      lastAcceptedIncrementationMessage: Message
    )

    case Failure(
      lastNumber: IncrementationNumber,
      lastAcceptedIncrementationMessage: Message,
      invalidIncrementationMessage: Message
    )
  }

  enum Event {
    case IncrementMessage(
      number: IncrementationNumber,
      message: Message
    )

    case NormalMessage(message: Message)
  }
}
