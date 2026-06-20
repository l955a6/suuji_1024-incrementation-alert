package blue.l955a6.incrementationMonitor.di

import blue.l955a6.incrementationMonitor.context.misskey.value.NoteVisibility
import blue.l955a6.incrementationMonitor.context.misskey.value.Timeline
import blue.l955a6.incrementationMonitor.infrastructure.reader.misskey.MisskeyMessageReader
import blue.l955a6.incrementationMonitor.integration.MessageReader
import com.typesafe.config.Config
import wvlet.airframe.*

object MisskeyMessageReaderDesign {
  val design = newDesign
    .bind[MisskeyMessageReader.Config]
    .toProvider(readerConfig)
    .bind[MessageReader]
    .toInstance(
      MisskeyMessageReader(
        MisskeyMessageReader.Config(
          host = "azkey.azuki.blue",
          timeline = Timeline.Global,
          minVisibility = NoteVisibility.Public,
          excludeLocalOnly = false
        )
      )
    )

  private def readerConfig(config: Config): MisskeyMessageReader.Config = {
    val misskeyConfigRoot = config.getConfig("misskey")
    val readerConfigRoot = misskeyConfigRoot.getConfig("reader")
    MisskeyMessageReader.Config(
      host = misskeyConfigRoot.getString("host"),
      timeline = readerConfigRoot.getString("timeline") match {
        case "global" => Timeline.Global
        case "home" => Timeline.Home
        case "social" => Timeline.Social
        case "local" => Timeline.Local
        case s =>
          throw IllegalArgumentException(
            s"`$s` はTimelineの値として適正ではありません。Timelineは global | home | social | local である必要があります。"
          )
      },
      minVisibility = readerConfigRoot.getString("minVisibility") match {
        case "public" => NoteVisibility.Public
        case "home" => NoteVisibility.Home
        case s =>
          throw IllegalArgumentException(
            s"`$s` は監視対象とするnoteの最も狭いvisibilityとして適正ではありません。 public | home である必要があります。"
          )
      },
      excludeLocalOnly = readerConfigRoot.getBoolean("excludeLocalOnly")
    )
  }
}
