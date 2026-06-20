package blue.l955a6.incrementationMonitor.domain.lib.state

trait StateMachine[S, E, R] {
  val state: S
  def send(event: E): Either[R, StateMachine[S, E, R]]
}

object StateMachine {
  trait State
  trait Event
}
