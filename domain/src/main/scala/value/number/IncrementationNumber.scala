package blue.l955a6.incrementationMonitor.domain.value.number

/**
 * インクリメントを行う際に宣言される数値。
 *
 * Misskeyでのインクリメントを想定する場合3000桁近い数値を扱う場合がある。
 *
 * パフォーマンスの都合で実装が変わる可能性があるので、外部には[[IncrementationNumber]]という型とそのメソッドのみ公開するようにしてください。
 */
sealed trait IncrementationNumber

object IncrementationNumber {
  // ほとんどの場合はLong値で足りると思われるので、現状は必要な場合のみBigIntで扱う実装にする
  private case class Small(value: Long) extends IncrementationNumber {
    require(value >= 0)
  }

  private case class Large(value: BigInt) extends IncrementationNumber {
    require(value > Long.MaxValue)
  }

  extension (i: IncrementationNumber) {
    def isIncrementedFrom(that: IncrementationNumber): Boolean =
      (i, that) match {
        // 負の値も扱う場合はoverflowを考慮する実装に変更すること
        case (Small(v1), Small(v2)) => v1 - v2 == 1
        case (Large(v1), Large(v2)) => v1 - v2 == 1
        case (Large(v1), Small(Long.MaxValue)) => v1 - Long.MaxValue == 1
        case _ => false
      }
  }

  private val longMaxValue: String = Long.MaxValue.toString

  /**
   * @param s
   *   符号なし整数として解釈可能な半角の文字列であることを事前条件とします
   */
  private[number] def apply(s: String): IncrementationNumber =
    if (s.length < longMaxValue.length) Small(s.toLong)
    else if (s.length == longMaxValue.length && s <= longMaxValue) Small(s.toLong)
    else Large(BigInt(s))
}
