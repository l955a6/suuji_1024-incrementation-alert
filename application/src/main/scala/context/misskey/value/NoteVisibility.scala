package blue.l955a6.incrementationMonitor.application.context.misskey.value

import io.circe.Decoder

enum NoteVisibility {
  case Public, Home, Followers, Specified
}

object NoteVisibility {
  given Decoder[NoteVisibility] = Decoder.decodeString.emap { s =>
    NoteVisibility.values.find(_.toString.toLowerCase == s)
      .toRight(s"unknown visibility: $s")
  }
}
