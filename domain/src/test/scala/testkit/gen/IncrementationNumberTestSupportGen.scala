package blue.l955a6.incrementationMonitor.domain.testkit.gen

import org.scalacheck.Arbitrary
import org.scalacheck.Gen

object IncrementationNumberTestSupportGen {
  val nonNegativeLong: Gen[Long] =
    Gen.long.map(x => if (x < 0) x - Long.MinValue else x)

  val positiveHugeInt = Arbitrary.arbitrary[BigInt].map { n =>
    val abs = n.abs
    val offset = if (abs == 0) BigInt(Long.MaxValue) + 1 else BigInt(Long.MaxValue)
    abs + offset
  }
}
