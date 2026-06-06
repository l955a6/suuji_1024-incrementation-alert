package blue.l955a6.incrementationAlert.domain.state

import blue.l955a6.incrementationAlert.domain.model.IncrementationMessage
import blue.l955a6.incrementationAlert.domain.model.Message
import blue.l955a6.incrementationAlert.domain.model.User
import blue.l955a6.incrementationAlert.domain.value.message.{MessageContent, MessageId, MessageUrl}
import blue.l955a6.incrementationAlert.domain.value.number.IncrementationNumberDigits
import blue.l955a6.incrementationAlert.domain.value.user.UserHost
import blue.l955a6.incrementationAlert.domain.value.user.UserId
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Suuji1024IncrementationStateMachineTest extends AnyFlatSpec with Matchers {
  val suuji1024FullWidth = IncrementationNumberDigits("１０２４").get
  val suuji1024HalfWidth = IncrementationNumberDigits("1024").get
  val maxFullWidth = IncrementationNumberDigits("２０４８").get
  val maxHalfWidth = IncrementationNumberDigits("2048").get
  val idle = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024FullWidth,
    maxNumberDigits = maxFullWidth,
    state = Suuji1024IncrementationStateMachine.State.Idle
  )
  val normalMessage = Message(
    id = MessageId("test"),
    content = MessageContent("テスト"),
    url = MessageUrl("https://example.com"),
    user = User(
      id = UserId("test"),
      host = UserHost("example.com")
    )
  )

  private def incrementationMessage(digits: String): IncrementationMessage = IncrementationMessage(
    id = MessageId("test"),
    content = MessageContent(
      s"""あずきインターネットにおける、正式な表記方法は下記となります。
         |- suuji_1024
         |- すうじ$digits""".stripMargin
    ),
    numberDigits = IncrementationNumberDigits(digits).getOrElse(
      fail(s"`$digits`はIncrementationNumberDigitsとして解釈できません")
    ),
    url = MessageUrl("https://example.com"),
    user = User(
      UserId("test"),
      UserHost("example.com")
    )
  )

  it should "インクリメント監視を行っていないとき、最初の数字と同じ数字でインクリメントするメッセージを受け取るとインクリメント監視を開始する" in {
    val message = incrementationMessage("１０２４")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = idle.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Monitoring(message)
    nextState shouldEqual expected
  }

  it should "インクリメント監視を行っていないとき、最初の数字より小さい数字でインクリメントするメッセージを受け取っても何もしない" in {
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(
      incrementationMessage("１０２３")
    )

    val nextState = idle.send(event).state
    nextState shouldEqual Suuji1024IncrementationStateMachine.State.Idle
  }

  it should "インクリメント監視を行っていないとき、最初の数字より大きい数字でインクリメントするメッセージを受け取っても何もしない" in {
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(
      incrementationMessage("１０２５")
    )

    val nextState = idle.send(event).state
    nextState shouldEqual Suuji1024IncrementationStateMachine.State.Idle
  }

  it should "インクリメント監視を行っていないとき、最初の数字と同じ大きさだが半角/全角が異なる数字でインクリメントするメッセージを受け取っても何もしない(メッセージの数字が半角の場合)" in {
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(
      incrementationMessage("1024")
    )

    val nextState = idle.send(event).state
    nextState shouldEqual Suuji1024IncrementationStateMachine.State.Idle
  }

  it should "インクリメント監視を行っていないとき、最初の数字と同じ大きさだが半角/全角が異なる数字でインクリメントするメッセージを受け取っても何もしない(メッセージの数字が全角の場合)" in {
    val stateMachine = Suuji1024IncrementationStateMachine(
      initialNumberDigits = IncrementationNumberDigits("1024").get,
      maxNumberDigits = IncrementationNumberDigits("2048").get,
      state = Suuji1024IncrementationStateMachine.State.Idle
    )
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(
      incrementationMessage("１０２４")
    )

    val nextState = stateMachine.send(event).state
    nextState shouldEqual Suuji1024IncrementationStateMachine.State.Idle
  }

  it should "インクリメント監視を行っていないとき、インクリメントを行っていないメッセージを受け取っても何もしない" in {
    val event = Suuji1024IncrementationStateMachine.Event.NormalMessage(normalMessage)

    val nextState = idle.send(event).state
    nextState shouldEqual Suuji1024IncrementationStateMachine.State.Idle
  }

  val monitoringFullWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024FullWidth,
    maxNumberDigits = maxFullWidth,
    state = Suuji1024IncrementationStateMachine.State.Monitoring(
      incrementationMessage("１０２４")
    )
  )
  val monitoringHalfWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024HalfWidth,
    maxNumberDigits = maxHalfWidth,
    state = Suuji1024IncrementationStateMachine.State.Monitoring(
      incrementationMessage("1024")
    )
  )

  it should "インクリメント監視を開始しているとき、現在の数字より1大きく半角/全角が同じ数字でインクリメントするメッセージを受け取り、まだ上限の数字でない場合、最後に受理したメッセージを更新しインクリメント監視を継続する(全角の場合)" in {
    val message = incrementationMessage("１０２５")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Monitoring(
      message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より1大きく半角/全角が同じ数字でインクリメントするメッセージを受け取り、まだ上限の数字でない場合、最後に受理したメッセージを更新しインクリメント監視を継続する(半角の場合)" in {
    val message = incrementationMessage("1025")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Monitoring(
      message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字が最初の数字で、かつインクリメントしようとしたメッセージの数字も同じ大きさだった場合、何もせず監視を続ける(全角の場合)" in {
    val message = incrementationMessage("１０２４")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringFullWidth.send(event).state
    val expected = monitoringFullWidth.state
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字が最初の数字で、かつインクリメントしようとしたメッセージの数字も同じ大きさだった場合、何もせず監視を続ける(半角の場合)" in {
    val message = incrementationMessage("1024")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringHalfWidth.send(event).state
    val expected = monitoringHalfWidth.state
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字と同じ大きさかつ最初の数字と同じ大きさではない数字でインクリメントするメッセージを受け取ると失敗する(全角の場合)" in {
    val lastAcceptedIncrementationMessage = incrementationMessage("１０２５")
    val stateMachine = Suuji1024IncrementationStateMachine(
      initialNumberDigits = suuji1024FullWidth,
      maxNumberDigits = maxFullWidth,
      state = Suuji1024IncrementationStateMachine.State.Monitoring(
        lastAcceptedIncrementationMessage
      )
    )
    val message = incrementationMessage("１０２５")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = stateMachine.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Failed(
      lastAcceptedIncrementationMessage = lastAcceptedIncrementationMessage,
      invalidIncrementationMessage = message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字と同じ大きさかつ最初の数字と同じ大きさではない数字でインクリメントするメッセージを受け取ると失敗する(半角の場合)" in {
    val lastAcceptedIncrementationMessage = incrementationMessage("1025")
    val stateMachine = Suuji1024IncrementationStateMachine(
      initialNumberDigits = suuji1024HalfWidth,
      maxNumberDigits = maxHalfWidth,
      state = Suuji1024IncrementationStateMachine.State.Monitoring(
        lastAcceptedIncrementationMessage
      )
    )
    val message = incrementationMessage("1025")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = stateMachine.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Failed(
      lastAcceptedIncrementationMessage = lastAcceptedIncrementationMessage,
      invalidIncrementationMessage = message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、既に最初の数字からインクリメントが進んでいる状態で、最初の数字と同じ大きさの数字でインクリメントするメッセージを受け取っても失敗せず監視を続ける(全角の場合)" in {
    val stateMachine = Suuji1024IncrementationStateMachine(
      initialNumberDigits = suuji1024FullWidth,
      maxNumberDigits = maxFullWidth,
      state = Suuji1024IncrementationStateMachine.State.Monitoring(
        incrementationMessage("１０２５")
      )
    )
    val message = incrementationMessage("１０２４")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(
      message
    )

    val nextState = stateMachine.send(event).state
    val expected = stateMachine.state
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、既に最初の数字からインクリメントが進んでいる状態で、最初の数字と同じ大きさの数字でインクリメントするメッセージを受け取っても失敗せず監視を続ける(半角の場合)" in {
    val stateMachine = Suuji1024IncrementationStateMachine(
      initialNumberDigits = suuji1024HalfWidth,
      maxNumberDigits = maxHalfWidth,
      state = Suuji1024IncrementationStateMachine.State.Monitoring(
        incrementationMessage("1025")
      )
    )
    val message = incrementationMessage("1024")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(
      message
    )

    val nextState = stateMachine.send(event).state
    val expected = stateMachine.state
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より小さい、最初の数字と同じ大きさではない数字でインクリメントするメッセージを受け取ると失敗する(全角の場合)" in {
    val message = incrementationMessage("１０２３")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Failed(
      lastAcceptedIncrementationMessage = incrementationMessage("１０２４"),
      invalidIncrementationMessage = message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より小さい、最初の数字と同じ大きさではない数字でインクリメントするメッセージを受け取ると失敗する(半角の場合)" in {
    val message = incrementationMessage("1023")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Failed(
      lastAcceptedIncrementationMessage = incrementationMessage("1024"),
      invalidIncrementationMessage = message
    )
    nextState shouldEqual expected
  }

  val monitoringBeforeMaxFullWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024FullWidth,
    maxNumberDigits = maxFullWidth,
    state = Suuji1024IncrementationStateMachine.State.Monitoring(
      incrementationMessage("２０４７")
    )
  )

  it should "インクリメント監視を開始しているとき、現在の数字より2以上大きい数字でインクリメントするメッセージを受け取ると失敗する(全角の場合)" in {
    val message = incrementationMessage("１０２６")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Failed(
      lastAcceptedIncrementationMessage = incrementationMessage("１０２４"),
      invalidIncrementationMessage = message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より2以上大きい数字でインクリメントするメッセージを受け取ると失敗する(半角の場合)" in {
    val message = incrementationMessage("1026")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Failed(
      lastAcceptedIncrementationMessage = incrementationMessage("1024"),
      invalidIncrementationMessage = message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より1大きく半角/全角が同じ数字でインクリメントするメッセージを受け取り、上限の数字になった場合、インクリメント監視を完了する(全角の場合)" in {
    val message = incrementationMessage("２０４８")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringBeforeMaxFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Completed(
      message
    )
    nextState shouldEqual expected
  }

  val monitoringBeforeMaxHalfWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024HalfWidth,
    maxNumberDigits = maxHalfWidth,
    state = Suuji1024IncrementationStateMachine.State.Monitoring(
      incrementationMessage("2047")
    )
  )

  it should "インクリメント監視を開始しているとき、現在の数字より1大きく半角/全角が同じ数字でインクリメントするメッセージを受け取り、上限の数字になった場合、インクリメント監視を完了する(半角の場合)" in {
    val message = incrementationMessage("2048")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringBeforeMaxHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Completed(
      message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より1大きい数字でインクリメントするメッセージを受け取りそれが上限の数字でも、半角/全角が異なる場合失敗する(メッセージが半角の場合)" in {
    val message = incrementationMessage("2048")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringBeforeMaxFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Failed(
      lastAcceptedIncrementationMessage = incrementationMessage("２０４７"),
      invalidIncrementationMessage = message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視を開始しているとき、現在の数字より1大きい数字でインクリメントするメッセージを受け取りそれが上限の数字でも、半角/全角が異なる場合失敗する(メッセージが全角の場合)" in {
    val message = incrementationMessage("２０４８")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = monitoringBeforeMaxHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Failed(
      lastAcceptedIncrementationMessage = incrementationMessage("2047"),
      invalidIncrementationMessage = message
    )
    nextState shouldEqual expected
  }

  val failedFullWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024FullWidth,
    maxNumberDigits = maxFullWidth,
    state = Suuji1024IncrementationStateMachine.State.Failed(
      lastAcceptedIncrementationMessage = incrementationMessage("１０２４"),
      invalidIncrementationMessage = incrementationMessage("１０２６")
    )
  )

  it should "インクリメント監視に失敗した直後、最初の数字でインクリメントするメッセージを受け取ると監視を開始する(全角の場合)" in {
    val message = incrementationMessage("１０２４")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = failedFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Monitoring(
      message
    )
    nextState shouldEqual expected
  }

  val failedHalfWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024HalfWidth,
    maxNumberDigits = maxHalfWidth,
    state = Suuji1024IncrementationStateMachine.State.Failed(
      lastAcceptedIncrementationMessage = incrementationMessage("1024"),
      invalidIncrementationMessage = incrementationMessage("1026")
    )
  )

  it should "インクリメント監視に失敗した直後、最初の数字でインクリメントするメッセージを受け取ると監視を開始する(半角の場合)" in {
    val message = incrementationMessage("1024")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = failedHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Monitoring(
      message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視に失敗した直後、最初の数字と同じ大きさだが半角/全角が異なる数字でインクリメントするメッセージを受け取ると待機状態に遷移する(最初の数字が全角の場合)" in {
    val message = incrementationMessage("1024")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = failedFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Idle

    nextState shouldEqual expected
  }

  it should "インクリメント監視に失敗した直後、最初の数字と同じ大きさだが半角/全角が異なる数字でインクリメントするメッセージを受け取ると待機状態に遷移する(最初の数字が半角の場合)" in {
    val message = incrementationMessage("１０２４")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = failedHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Idle

    nextState shouldEqual expected
  }

  it should "インクリメント監視に失敗した直後、最初の数字と大きさが異なる数字でインクリメントするメッセージを受け取ると待機状態に遷移する(全角の場合)" in {
    val message = incrementationMessage("１０２５")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = failedFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Idle

    nextState shouldEqual expected
  }

  it should "インクリメント監視に失敗した直後、最初の数字と大きさが異なる数字でインクリメントするメッセージを受け取ると待機状態に遷移する(半角の場合)" in {
    val message = incrementationMessage("1025")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = failedHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Idle

    nextState shouldEqual expected
  }

  it should "インクリメント監視に失敗した直後、インクリメントを行わないメッセージを受け取ると待機状態に遷移する" in {
    val event = Suuji1024IncrementationStateMachine.Event.NormalMessage(normalMessage)

    val nextState = failedFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Idle

    nextState shouldEqual expected
  }

  val completedFullWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024FullWidth,
    maxNumberDigits = maxFullWidth,
    state = Suuji1024IncrementationStateMachine.State.Completed(
      incrementationMessage("２０４８")
    )
  )

  it should "インクリメント監視が完了した直後、最初の数字でインクリメントするメッセージを受け取ると監視を開始する(全角の場合)" in {
    val message = incrementationMessage("１０２４")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = completedFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Monitoring(
      message
    )
    nextState shouldEqual expected
  }

  val completedHalfWidth = Suuji1024IncrementationStateMachine(
    initialNumberDigits = suuji1024HalfWidth,
    maxNumberDigits = maxHalfWidth,
    state = Suuji1024IncrementationStateMachine.State.Completed(
      incrementationMessage("2048")
    )
  )

  it should "インクリメント監視が完了した直後、最初の数字でインクリメントするメッセージを受け取ると監視を開始する(半角の場合)" in {
    val message = incrementationMessage("1024")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = completedHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Monitoring(
      message
    )
    nextState shouldEqual expected
  }

  it should "インクリメント監視が完了した直後、最初の数字と同じ大きさだが半角/全角が異なる数字でインクリメントするメッセージを受け取ると待機状態に遷移する(最初の数字が全角の場合)" in {
    val message = incrementationMessage("1024")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = completedFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Idle

    nextState shouldEqual expected
  }

  it should "インクリメント監視が完了した直後、最初の数字と同じ大きさだが半角/全角が異なる数字でインクリメントするメッセージを受け取ると待機状態に遷移する(最初の数字が半角の場合)" in {
    val message = incrementationMessage("１０２４")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = completedHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Idle

    nextState shouldEqual expected
  }

  it should "インクリメント監視が完了した直後、最初の数字と大きさが異なる数字でインクリメントするメッセージを受け取ると待機状態に遷移する(全角の場合)" in {
    val message = incrementationMessage("１０２５")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = completedFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Idle

    nextState shouldEqual expected
  }

  it should "インクリメント監視が完了した直後、最初の数字と大きさが異なる数字でインクリメントするメッセージを受け取ると待機状態に遷移する(半角の場合)" in {
    val message = incrementationMessage("1025")
    val event = Suuji1024IncrementationStateMachine.Event.Incrementation(message)

    val nextState = completedHalfWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Idle

    nextState shouldEqual expected
  }

  it should "インクリメント監視が完了した直後、インクリメントを行わないメッセージを受け取ると待機状態に遷移する" in {
    val event = Suuji1024IncrementationStateMachine.Event.NormalMessage(normalMessage)

    val nextState = completedFullWidth.send(event).state
    val expected = Suuji1024IncrementationStateMachine.State.Idle

    nextState shouldEqual expected
  }
}
