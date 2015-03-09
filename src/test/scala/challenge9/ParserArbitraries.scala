package challenge9

import org.scalacheck.{ Arbitrary, Gen }, Arbitrary._, Gen._

object ParserArbitraries {
  implicit def ParserArbitrary[A: Arbitrary]: Arbitrary[Parser[A]] =
    Arbitrary { arbitrary[A] map Parser.value }
}