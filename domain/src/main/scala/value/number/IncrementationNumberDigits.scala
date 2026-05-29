package blue.l955a6.incrementationAlert.domain.value.number

/**
 * 全角、または半角の文字列で表されたインクリメントの数字
 */
final case class IncrementationNumberDigits(
  number: IncrementationNumber,
  width: IncrementationNumberWidth
) {
  def isIncrementedFrom(that: IncrementationNumberDigits): Boolean =
    width == that.width && number.isIncrementedFrom(that.number)
}

object IncrementationNumberDigits {
  private val HalfWidthRegex = "^(0|[1-9]\\d*)$".r
  private val FullWidthRegex = "^(０|[１２３４５６７８９][０１２３４５６７８９]*)$".r
  private val fullWidthToHalfWidth = '0' - '０'

  def apply(s: String): Option[IncrementationNumberDigits] = {
    val half = Option.when(HalfWidthRegex.matches(s))(
      IncrementationNumberDigits(
        IncrementationNumber(s),
        IncrementationNumberWidth.Half
      )
    )
    lazy val full = Option.when(FullWidthRegex.matches(s))(
      IncrementationNumberDigits(
        IncrementationNumber(s.map(c => (c + fullWidthToHalfWidth).toChar)),
        IncrementationNumberWidth.Full
      )
    )
    half.orElse(full)
  }
}
