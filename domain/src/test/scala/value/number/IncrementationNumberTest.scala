package blue.l955a6.incrementationAlert.domain.value.number

import blue.l955a6.incrementationAlert.domain.testkit.gen.IncrementationNumberTestSupportGen
import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class IncrementationNumberTest extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks {
  "IncrementationNumber" should "非負のLong値として解釈可能な半角文字列から作成できる" in {
    forAll(IncrementationNumberTestSupportGen.nonNegativeLong) { n =>
      IncrementationNumber(n.toString)
    }
  }

  "IncrementationNumber" should "Long値の範囲を超えていても正の整数として解釈可能な半角文字列から作成できる" in {
    forAll(IncrementationNumberTestSupportGen.positiveHugeInt) { n =>
      IncrementationNumber(n.toString)
    }
  }

  "IncrementationNumber.isIncrementedFrom()" should "全ての正のIncrementationNumberはそれより1つ小さいIncrementationNumberをインクリメントしたものである" in {
    val positiveLongGen =
      IncrementationNumberTestSupportGen.nonNegativeLong.map(n => if (n == 0) 1L else n)
    forAll(positiveLongGen) { n =>
      val x = IncrementationNumber(n.toString)
      val y = IncrementationNumber((n - 1).toString)
      x.isIncrementedFrom(y) shouldBe true
    }

    forAll(IncrementationNumberTestSupportGen.positiveHugeInt) { n =>
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

    forAll(IncrementationNumberTestSupportGen.nonNegativeLong) { n =>
      val that = IncrementationNumber(n.toString)
      zero.isIncrementedFrom(that) shouldBe false
    }

    forAll(IncrementationNumberTestSupportGen.positiveHugeInt) { n =>
      val that = IncrementationNumber(n.toString)
      zero.isIncrementedFrom(that) shouldBe false
    }
  }

  "IncrementationNumber.isIncrementedFrom()" should "全てのIncrementationNumberはそれより2以上小さいIncrementationNumberをインクリメントしたものではない" in {
    val gen1 = for {
      _x <- IncrementationNumberTestSupportGen.nonNegativeLong
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
      x <- IncrementationNumberTestSupportGen.positiveHugeInt
      _y <- IncrementationNumberTestSupportGen.positiveHugeInt
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
    forAll(IncrementationNumberTestSupportGen.nonNegativeLong) { _n =>
      val n = IncrementationNumber(_n.toString)
      n.isIncrementedFrom(n) shouldBe false
    }

    forAll(IncrementationNumberTestSupportGen.positiveHugeInt) { _n =>
      val n = IncrementationNumber(_n.toString)
      n.isIncrementedFrom(n) shouldBe false
    }
  }

  "IncrementationNumber.isIncrementedFrom()" should "全てのIncrementationNumberは自身より大きなIncrementationNumberをインクリメントしたものではない" in {
    val gen1 = for {
      x <- IncrementationNumberTestSupportGen.nonNegativeLong
      y <- Gen.choose(x + 1, Long.MaxValue)
    } yield (
      IncrementationNumber(x.toString),
      IncrementationNumber(y.toString)
    )

    forAll(gen1) { (x, y) =>
      x.isIncrementedFrom(y) shouldBe false
    }

    val gen2 = for {
      x <- IncrementationNumberTestSupportGen.positiveHugeInt
      y <- Gen.oneOf(
        IncrementationNumberTestSupportGen.nonNegativeLong.map(BigInt.apply),
        IncrementationNumberTestSupportGen.positiveHugeInt
      )
    } yield (
      IncrementationNumber(x.toString),
      IncrementationNumber(y.toString)
    )

    forAll(gen2) { (x, y) =>
      x.isIncrementedFrom(y) shouldBe false
    }
  }
}
