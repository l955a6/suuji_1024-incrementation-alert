package blue.l955a6.incrementationMonitor.context.misskey.value

enum Timeline {
  case Global, Home, Social, Local

  def chanelName: String = this match {
    case Global => "globalTimeline"
    case Home => "homeTimeline"
    case Social => "hybridTimeline"
    case Local => "localTimeline"
  }
}
