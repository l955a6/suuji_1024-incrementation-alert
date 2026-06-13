package blue.l955a6.incrementationMonitor.domain.value.number

import blue.l955a6.incrementationMonitor.domain.testkit.gen.IncrementationNumberTestSupportGen
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class IncrementationNumberDigitsTest
    extends AnyFlatSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with OptionValues {

  private val fullWidthHalfWidthOffset = '０' - '0'
  private def toFullWidth(x: Long | BigInt): String =
    x.toString.map(c => (c + fullWidthHalfWidthOffset).toChar)

  private val nonNegativeBigIntGen = Gen.oneOf(
    IncrementationNumberTestSupportGen.nonNegativeLong.map(BigInt.apply),
    IncrementationNumberTestSupportGen.positiveHugeInt
  )

  "IncrementationNumberDigits.apply()" should "符号なしのLong値の半角文字列からIncrementationNumberDigitsを作れる" in {
    forAll(IncrementationNumberTestSupportGen.nonNegativeLong) { n =>
      val x = IncrementationNumberDigits(n.toString).value

      x.width shouldBe IncrementationNumberWidth.Half

      val expectedNumber = IncrementationNumber(n.toString)
      x.number shouldEqual expectedNumber
    }
  }

  "IncrementationNumberDigits.apply()" should "符号なしのLong値の全角文字列からIncrementationNumberDigitsを作れる" in {
    forAll(IncrementationNumberTestSupportGen.nonNegativeLong) { n =>
      val x = IncrementationNumberDigits(toFullWidth(n)).value

      x.width shouldBe IncrementationNumberWidth.Full

      val expectedNumber = IncrementationNumber(n.toString)
      x.number shouldEqual expectedNumber
    }
  }

  "IncrementationNumberDigits.apply()" should "符号なしのLong値の範囲を超えるBigInt値の半角文字列からIncrementationNumberDigitsを作れる" in {
    forAll(IncrementationNumberTestSupportGen.positiveHugeInt) { n =>
      val x = IncrementationNumberDigits(n.toString).value

      x.width shouldBe IncrementationNumberWidth.Half

      val expectedNumber = IncrementationNumber(n.toString)
      x.number shouldEqual expectedNumber
    }
  }

  "IncrementationNumberDigits.apply()" should "符号なしのLong値の範囲を超えるBigInt値の全角文字列からIncrementationNumberDigitsを作れる" in {
    forAll(IncrementationNumberTestSupportGen.positiveHugeInt) { n =>
      val x = IncrementationNumberDigits(toFullWidth(n)).value

      x.width shouldBe IncrementationNumberWidth.Full

      val expectedNumber = IncrementationNumber(n.toString)
      x.number shouldEqual expectedNumber
    }
  }

  "IncrementationNumberDigits.isIncrementedFrom()" should "半角のIncrementationNumberDigitsは全角のIncrementationNumberDigitsをインクリメントしたものではない" in {
    forAll(
      nonNegativeBigIntGen
    ) { n =>
      val half = IncrementationNumberDigits(
        number = IncrementationNumber((n + 1).toString),
        width = IncrementationNumberWidth.Half
      )
      val full = IncrementationNumberDigits(
        number = IncrementationNumber(n.toString),
        width = IncrementationNumberWidth.Full
      )
      half.isIncrementedFrom(full) shouldBe false
    }
  }

  "IncrementationNumberDigits.isIncrementedFrom()" should "全角のIncrementationNumberDigitsは半角のIncrementationNumberDigitsをインクリメントしたものではない" in {
    forAll(
      nonNegativeBigIntGen
    ) { n =>
      val full = IncrementationNumberDigits(
        number = IncrementationNumber((n + 1).toString),
        width = IncrementationNumberWidth.Full
      )
      val half = IncrementationNumberDigits(
        number = IncrementationNumber(n.toString),
        width = IncrementationNumberWidth.Half
      )
      full.isIncrementedFrom(half) shouldBe false
    }
  }

  "IncrementationNumberDigits.isIncrementedFrom()" should "半角のIncrementationNumberDigits同士でも引数がレシーバより1だけ小さいものでなければfalseを返す" in {
    val g = for {
      x <- nonNegativeBigIntGen
      y <- nonNegativeBigIntGen
      if y != x - 1
    } yield (IncrementationNumber(x.toString), IncrementationNumber(y.toString))

    forAll(g) { (_x, _y) =>
      val x = IncrementationNumberDigits(
        number = _x,
        width = IncrementationNumberWidth.Half
      )
      val y = IncrementationNumberDigits(
        number = _y,
        width = IncrementationNumberWidth.Half
      )
      x.isIncrementedFrom(y) shouldBe false
    }
  }

  "IncrementationNumberDigits.isIncrementedFrom()" should "全角のIncrementationNumberDigits同士でも引数がレシーバより1だけ小さいものでなければfalseを返す" in {
    val g = for {
      x <- nonNegativeBigIntGen
      y <- nonNegativeBigIntGen
      if y != x - 1
    } yield (IncrementationNumber(x.toString), IncrementationNumber(y.toString))

    forAll(g) { (_x, _y) =>
      val x = IncrementationNumberDigits(
        number = _x,
        width = IncrementationNumberWidth.Half
      )
      val y = IncrementationNumberDigits(
        number = _y,
        width = IncrementationNumberWidth.Half
      )
      x.isIncrementedFrom(y) shouldBe false
    }
  }

  "IncrementationNumberDigits.isIncrementedFrom()" should "半角のIncrementationNumberDigits同士かつ、引数がレシーバよりも1小さい場合trueを返す" in {
    val g = for {
      x <- nonNegativeBigIntGen
      if x != 0
      y = x - 1
    } yield (IncrementationNumber(x.toString), IncrementationNumber(y.toString))

    forAll(g) { (_x, _y) =>
      val x = IncrementationNumberDigits(
        number = _x,
        width = IncrementationNumberWidth.Half
      )
      val y = IncrementationNumberDigits(
        number = _y,
        width = IncrementationNumberWidth.Half
      )
      x.isIncrementedFrom(y) shouldBe true
    }
  }

  "IncrementationNumberDigits.isIncrementedFrom()" should "全角のIncrementationNumberDigits同士かつ、引数がレシーバよりも1小さい場合trueを返す" in {
    val g = for {
      x <- nonNegativeBigIntGen
      if x != 0
      y = x - 1
    } yield (IncrementationNumber(x.toString), IncrementationNumber(y.toString))

    forAll(g) { (_x, _y) =>
      val x = IncrementationNumberDigits(
        number = _x,
        width = IncrementationNumberWidth.Full
      )
      val y = IncrementationNumberDigits(
        number = _y,
        width = IncrementationNumberWidth.Full
      )
      x.isIncrementedFrom(y) shouldBe true
    }
  }
}
