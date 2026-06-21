package blue.l955a6.incrementationMonitor.domain.state

import blue.l955a6.incrementationMonitor.domain.state.Suuji1024IncrementationStateMachine.Event
import blue.l955a6.incrementationMonitor.domain.value.number.IncrementationNumberDigits
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Suuji1024IncrementationStateMachineTest extends AnyFlatSpec with Matchers with EitherValues {
  val suuji1024FullWidth = IncrementationNumberDigits("１０２４").get
  val suuji1024HalfWidth = IncrementationNumberDigits("1024").get
  val maxFullWidth = IncrementationNumberDigits("２０４８").get
  val maxHalfWidth = IncrementationNumberDigits("2048").get
  val idle = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024FullWidth,
    maxNumberDigits = maxFullWidth,
    state = Suuji1024IncrementationStateMachine.State.Idle
  )

  private def incrementationNumberDigits(s: String): IncrementationNumberDigits =
    IncrementationNumberDigits(s).getOrElse(
      fail(s"`$s`はIncrementationNumberDigitsとして解釈できません")
    )

  private def incrementation(s: String): Suuji1024IncrementationStateMachine.Event =
    Suuji1024IncrementationStateMachine.Event.Incrementation(incrementationNumberDigits(s))

  extension (e: Suuji1024IncrementationStateMachine.Event) {
    def digits: IncrementationNumberDigits =
      e match {
        case Event.Incrementation(digits) =>
          digits
        case Event.Noop =>
          fail(s"`$e` はIncrementationではありません")
      }
  }

  it should "インクリメント監視を行っていないとき、最初の数字と同じ数字でインクリメントするとインクリメント監視を開始する" in {
    val event = incrementation("１０２４")

    val nextState = idle.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Monitoring(event.digits)
    nextState shouldEqual expected
  }

  it should "インクリメント監視を行っていないとき、最初の数字より小さい数字でインクリメントしても何もしない" in {
    val event = incrementation("１０２３")

    val nextState = idle.send(event).value.state
    nextState shouldEqual Suuji1024IncrementationStateMachine.State.Idle
  }

  it should "インクリメント監視を行っていないとき、最初の数字より大きい数字でインクリメントしても何もしない" in {
    val event = incrementation("１０２５")

    val nextState = idle.send(event).value.state
    nextState shouldEqual Suuji1024IncrementationStateMachine.State.Idle
  }

  it should "インクリメント監視を行っていないとき、最初の数字と同じ大きさだが半角/全角が異なる数字でインクリメントしても何もしない(数字が半角の場合)" in {
    val event = incrementation("1024")

    val nextState = idle.send(event).value.state
    nextState shouldEqual Suuji1024IncrementationStateMachine.State.Idle
  }

  it should "インクリメント監視を行っていないとき、最初の数字と同じ大きさだが半角/全角が異なる数字でインクリメントしても何もしない(数字が全角の場合)" in {
    val stateMachine = Suuji1024IncrementationStateMachine(
      initialNumberDigits = incrementationNumberDigits("1024"),
      maxNumberDigits = incrementationNumberDigits("2048"),
      state = Suuji1024IncrementationStateMachine.State.Idle
    )
    val event = incrementation("１０２４")

    val nextState = stateMachine.send(event).value.state
    nextState shouldEqual Suuji1024IncrementationStateMachine.State.Idle
  }

  it should "インクリメント監視を行っていないとき、Noopを受け取っても何もしない" in {
    val event = Suuji1024IncrementationStateMachine.Event.Noop

    val nextState = idle.send(event).value.state
    nextState shouldEqual Suuji1024IncrementationStateMachine.State.Idle
  }

  val monitoringFullWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024FullWidth,
    maxNumberDigits = maxFullWidth,
    state = Suuji1024IncrementationStateMachine.State.Monitoring(
      incrementationNumberDigits("１０２４")
    )
  )
  val monitoringHalfWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024HalfWidth,
    maxNumberDigits = maxHalfWidth,
    state = Suuji1024IncrementationStateMachine.State.Monitoring(
      incrementationNumberDigits("1024")
    )
  )

  it should "インクリメント監視を開始しているとき、現在の数字より1大きく半角/全角が同じ数字でインクリメントされ、まだ上限の数字でない場合、現在の数字を更新しインクリメント監視を継続する(全角の場合)" in {
    val event = incrementation("１０２５")

    val nextState = monitoringFullWidth.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Monitoring(event.digits)
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より1大きく半角/全角が同じ数字でインクリメントされ、まだ上限の数字でない場合、現在の数字を更新しインクリメント監視を継続する(半角の場合)" in {
    val event = incrementation("1025")

    val nextState = monitoringHalfWidth.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Monitoring(event.digits)
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字が最初の数字で、かつインクリメントしようとした数字も同じ大きさだった場合、何もせず監視を続ける(全角の場合)" in {
    val event = incrementation("１０２４")

    val nextState = monitoringFullWidth.send(event).value.state
    val expected = monitoringFullWidth.state
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字が最初の数字で、かつインクリメントしようとした数字も同じ大きさだった場合、何もせず監視を続ける(半角の場合)" in {
    val event = incrementation("1024")

    val nextState = monitoringHalfWidth.send(event).value.state
    val expected = monitoringHalfWidth.state
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字と同じ大きさかつ最初の数字と同じ大きさではない数字でインクリメントすると失敗する(全角の場合)" in {
    val stateMachine = Suuji1024IncrementationStateMachine(
      initialNumberDigits = suuji1024FullWidth,
      maxNumberDigits = maxFullWidth,
      state = Suuji1024IncrementationStateMachine.State.Monitoring(
        incrementationNumberDigits("１０２５")
      )
    )
    val event = incrementation("１０２５")

    val nextState = stateMachine.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Failed
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字と同じ大きさかつ最初の数字と同じ大きさではない数字でインクリメントすると失敗する(半角の場合)" in {
    val stateMachine = Suuji1024IncrementationStateMachine(
      initialNumberDigits = suuji1024HalfWidth,
      maxNumberDigits = maxHalfWidth,
      state = Suuji1024IncrementationStateMachine.State.Monitoring(
        incrementationNumberDigits("1025")
      )
    )
    val event = incrementation("1025")

    val nextState = stateMachine.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Failed
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、既に最初の数字からインクリメントが進んでいる状態で、最初の数字と同じ大きさの数字でインクリメントしても失敗せず監視を続ける(全角の場合)" in {
    val stateMachine = Suuji1024IncrementationStateMachine(
      initialNumberDigits = suuji1024FullWidth,
      maxNumberDigits = maxFullWidth,
      state = Suuji1024IncrementationStateMachine.State.Monitoring(
        incrementationNumberDigits("１０２５")
      )
    )
    val event = incrementation("１０２４")

    val nextState = stateMachine.send(event).value.state
    val expected = stateMachine.state
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、既に最初の数字からインクリメントが進んでいる状態で、最初の数字と同じ大きさの数字でインクリメントしても失敗せず監視を続ける(半角の場合)" in {
    val stateMachine = Suuji1024IncrementationStateMachine(
      initialNumberDigits = suuji1024HalfWidth,
      maxNumberDigits = maxHalfWidth,
      state = Suuji1024IncrementationStateMachine.State.Monitoring(
        incrementationNumberDigits("1025")
      )
    )
    val event = incrementation("1024")

    val nextState = stateMachine.send(event).value.state
    val expected = stateMachine.state
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より小さい、最初の数字と同じ大きさではない数字でインクリメントすると失敗する(全角の場合)" in {
    val event = incrementation("１０２３")

    val nextState = monitoringFullWidth.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Failed
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より小さい、最初の数字と同じ大きさではない数字でインクリメントすると失敗する(半角の場合)" in {
    val event = incrementation("1023")

    val nextState = monitoringHalfWidth.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Failed
    nextState shouldEqual expected
  }

  val monitoringBeforeMaxFullWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024FullWidth,
    maxNumberDigits = maxFullWidth,
    state = Suuji1024IncrementationStateMachine.State.Monitoring(
      incrementationNumberDigits("２０４７")
    )
  )

  it should "インクリメント監視を開始しているとき、現在の数字より2以上大きい数字でインクリメントすると失敗する(全角の場合)" in {
    val event = incrementation("１０２６")

    val nextState = monitoringFullWidth.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Failed
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より2以上大きい数字でインクリメントすると失敗する(半角の場合)" in {
    val event = incrementation("1026")

    val nextState = monitoringHalfWidth.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Failed
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より1大きく半角/全角が同じ数字でインクリメントし、上限の数字になった場合、インクリメント監視を完了する(全角の場合)" in {
    val event = incrementation("２０４８")

    val nextState = monitoringBeforeMaxFullWidth.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Completed
    nextState shouldEqual expected
  }

  val monitoringBeforeMaxHalfWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024HalfWidth,
    maxNumberDigits = maxHalfWidth,
    state = Suuji1024IncrementationStateMachine.State.Monitoring(
      incrementationNumberDigits("2047")
    )
  )

  it should "インクリメント監視を開始しているとき、現在の数字より1大きく半角/全角が同じ数字でインクリメントし、上限の数字になった場合、インクリメント監視を完了する(半角の場合)" in {
    val event = incrementation("2048")

    val nextState = monitoringBeforeMaxHalfWidth.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Completed
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より1大きい数字でインクリメントしそれが上限の数字でも、半角/全角が異なる場合失敗する(半角の場合)" in {
    val event = incrementation("2048")

    val nextState = monitoringBeforeMaxFullWidth.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Failed
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より1大きい数字でインクリメントしそれが上限の数字でも、半角/全角が異なる場合失敗する(全角の場合)" in {
    val event = incrementation("２０４８")

    val nextState = monitoringBeforeMaxHalfWidth.send(event).value.state
    val expected = Suuji1024IncrementationStateMachine.State.Failed
    nextState shouldEqual expected
  }

  val failedFullWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024FullWidth,
    maxNumberDigits = maxFullWidth,
    state = Suuji1024IncrementationStateMachine.State.Failed
  )

  val failedHalfWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024HalfWidth,
    maxNumberDigits = maxHalfWidth,
    state = Suuji1024IncrementationStateMachine.State.Failed
  )

  it should "インクリメント監視に失敗した状態でIncrementationを受け取るとAlreadyTerminatedを返す(全角の場合)" in {
    val event = incrementation("１０２４")

    val actual = failedFullWidth.send(event).left.value
    actual shouldBe Suuji1024IncrementationStateMachine.Rejection.AlreadyTerminated
  }

  it should "インクリメント監視に失敗した状態でIncrementationを受け取るとAlreadyTerminatedを返す(半角の場合)" in {
    val event = incrementation("1024")

    val actual = failedHalfWidth.send(event).left.value
    actual shouldBe Suuji1024IncrementationStateMachine.Rejection.AlreadyTerminated
  }

  it should "インクリメント監視に失敗した状態でNoopを受け取るとAlreadyTerminatedを返す" in {
    val event = Suuji1024IncrementationStateMachine.Event.Noop

    val actual = failedFullWidth.send(event).left.value
    actual shouldBe Suuji1024IncrementationStateMachine.Rejection.AlreadyTerminated
  }

  val completedFullWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024FullWidth,
    maxNumberDigits = maxFullWidth,
    state = Suuji1024IncrementationStateMachine.State.Completed
  )

  val completedHalfWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024HalfWidth,
    maxNumberDigits = maxHalfWidth,
    state = Suuji1024IncrementationStateMachine.State.Completed
  )

  it should "インクリメント監視が完了した状態でIncrementationを受け取るとAlreadyTerminatedを返す(全角の場合)" in {
    val event = incrementation("１０２４")

    val actual = completedFullWidth.send(event).left.value
    actual shouldBe Suuji1024IncrementationStateMachine.Rejection.AlreadyTerminated
  }

  it should "インクリメント監視が完了した状態でIncrementationを受け取るとAlreadyTerminatedを返す(半角の場合)" in {
    val event = incrementation("1024")

    val actual = completedHalfWidth.send(event).left.value
    actual shouldBe Suuji1024IncrementationStateMachine.Rejection.AlreadyTerminated
  }

  it should "インクリメント監視が完了した状態でNoopを受け取るとAlreadyTerminatedを返す" in {
    val event = Suuji1024IncrementationStateMachine.Event.Noop

    val actual = completedFullWidth.send(event).left.value
    actual shouldBe Suuji1024IncrementationStateMachine.Rejection.AlreadyTerminated
  }
}
