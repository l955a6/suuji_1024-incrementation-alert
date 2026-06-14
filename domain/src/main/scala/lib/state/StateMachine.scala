package blue.l955a6.incrementationAlert.domain.lib.state

trait StateMachine[S, E] {
  val state: S
  def send(event: E): StateMachine[S, E]
}

object StateMachine {
  trait State
  trait Event
}
