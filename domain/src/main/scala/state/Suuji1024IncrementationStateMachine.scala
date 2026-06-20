package blue.l955a6.incrementationMonitor.domain.state

import blue.l955a6.incrementationMonitor.domain.value.number.IncrementationNumberDigits

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

  def send(
    event: Suuji1024IncrementationStateMachine.Event
  ): Suuji1024IncrementationStateMachine =
    event match {
      case Event.Incrementation(digits) =>
        state match
          case State.Idle =>
            if (digits == initialNumberDigits)
              copy(state = State.Monitoring(digits))
            else this
          case currentState @ State.Monitoring(current) =>
            val next =
              if (digits.isIncrementedFrom(current) && digits == maxNumberDigits)
                State.Completed
              else if (digits.isIncrementedFrom(current))
                State.Monitoring(digits)
              else if (digits == initialNumberDigits)
                // 初期の数値と同じ数値が流れてきた場合、それはインクリメントを意図しているのではなく
                // 単に名前を読んだだけの可能性が高いのでエラーにはせずスルーする
                currentState
              else
                // TODO: 長期的にインクリメントしていくことを意識せず、ちょっとだけインクリメントしようとする人がいるかもしれないので
                //       インクリメントを監視し始める閾値を導入することを考える
                State.Failed
            copy(state = next)
          case State.Failed | State.Completed =>
            val next =
              if (digits == initialNumberDigits)
                State.Monitoring(digits)
              else State.Idle
            copy(state = next)
      case Event.Noop =>
        state match {
          case State.Failed | State.Completed =>
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

    case Monitoring(current: IncrementationNumberDigits)

    case Completed

    case Failed
  }

  enum Event {
    case Incrementation(digits: IncrementationNumberDigits)

    case Noop
  }
}
