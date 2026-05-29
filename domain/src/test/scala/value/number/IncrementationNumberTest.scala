package blue.l955a6.incrementationAlert.domain.value.number

import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class IncrementationNumberTest extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks {
  private val nonNegativeLongGen = Gen.choose(0L, Long.MaxValue)
  private val positiveHugeIntGen = Arbitrary.arbitrary[BigInt].map { n =>
    val abs = n.abs
    val offset = if (abs == 0) BigInt(Long.MaxValue) + 1 else BigInt(Long.MaxValue)
    abs + offset
  }

  "IncrementationNumber" should "非負のLong値として解釈可能な半角文字列から作成できる" in {
    forAll(nonNegativeLongGen) { n =>
      IncrementationNumber(n.toString)
    }
  }

  "IncrementationNumber" should "Long値の範囲を超えていても正の整数として解釈可能な半角文字列から作成できる" in {
    forAll(positiveHugeIntGen) { n =>
      IncrementationNumber(n.toString)
    }
  }

  "IncrementationNumber.isIncrementedFrom()" should "全ての正のIncrementationNumberはそれより1つ小さいIncrementationNumberをインクリメントしたものである" in {
    val positiveLongGen = nonNegativeLongGen.map(n => if (n == 0) 1L else n)
    forAll(positiveLongGen) { n =>
      val x = IncrementationNumber(n.toString)
      val y = IncrementationNumber((n - 1).toString)
      x.isIncrementedFrom(y) shouldBe true
    }

    forAll(positiveHugeIntGen) { n =>
      val x = IncrementationNumber(n.toString)
      val y = IncrementationNumber((n - 1).toString)
      x.isIncrementedFrom(y) shouldBe true
    }
  }

  "IncrementationNumber.isIncrementedFrom()" should "最大のLong値より1だけ大きいIncrementationNumberは最大のLong値のIncrementationNumberをインクリメントしたものである" in {
    val x = IncrementationNumber((BigInt(Long.MaxValue) + 1).toString)
    val y = IncrementationNumber(Long.MaxValue.toString)
    x.isIncrementedFrom(y) shouldBe true
  }

  "IncrementationNumber.isIncrementedFrom()" should "0のIncrementationNumberはどのIncrementationNumberをインクリメントしたものでもない" in {
    val zero = IncrementationNumber("0")

    forAll(nonNegativeLongGen) { n =>
      val that = IncrementationNumber(n.toString)
      zero.isIncrementedFrom(that) shouldBe false
    }

    forAll(positiveHugeIntGen) { n =>
      val that = IncrementationNumber(n.toString)
      zero.isIncrementedFrom(that) shouldBe false
    }
  }

  "IncrementationNumber.isIncrementedFrom()" should "全てのIncrementationNumberはそれより2以上小さいIncrementationNumberをインクリメントしたものではない" in {
    val gen1 = for {
      _x <- nonNegativeLongGen
      x = if (_x < 2) _x + 2 else _x
      y <- Gen.choose(0L, x - 2L)
    } yield (
      IncrementationNumber(x.toString),
      IncrementationNumber(y.toString)
    )

    forAll(gen1) { (x, y) =>
      x.isIncrementedFrom(y) shouldBe false
    }

    // 2番目の要素はLongの可能性がある
    val gen2 = for {
      x <- positiveHugeIntGen
      _y <- positiveHugeIntGen
      y = _y % (x - 1)
    } yield (
      IncrementationNumber(x.toString),
      IncrementationNumber(y.toString)
    )

    forAll(gen2) { (x, y) =>
      x.isIncrementedFrom(y) shouldBe false
    }
  }

  "IncrementationNumber.isIncrementedFrom()" should "全てのIncrementationNumberは自身をインクリメントしたものではない" in {
    forAll(nonNegativeLongGen) { _n =>
      val n = IncrementationNumber(_n.toString)
      n.isIncrementedFrom(n) shouldBe false
    }

    forAll(positiveHugeIntGen) { _n =>
      val n = IncrementationNumber(_n.toString)
      n.isIncrementedFrom(n) shouldBe false
    }
  }

  "IncrementationNumber.isIncrementedFrom()" should "全てのIncrementationNumberは自身より大きなIncrementationNumberをインクリメントしたものではない" in {
    val gen1 = for {
      x <- nonNegativeLongGen
      y <- Gen.choose(x + 1, Long.MaxValue)
    } yield (
      IncrementationNumber(x.toString),
      IncrementationNumber(y.toString)
    )

    forAll(gen1) { (x, y) =>
      x.isIncrementedFrom(y) shouldBe false
    }

    val gen2 = for {
      x <- positiveHugeIntGen
      y <- Gen.oneOf(nonNegativeLongGen.map(BigInt.apply), positiveHugeIntGen)
    } yield (
      IncrementationNumber(x.toString),
      IncrementationNumber(y.toString)
    )

    forAll(gen2) { (x, y) =>
      x.isIncrementedFrom(y) shouldBe false
    }
  }
}
