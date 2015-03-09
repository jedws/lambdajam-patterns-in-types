package challenge9

import org.scalacheck.{ Arbitrary, Gen }, Arbitrary._, Gen._

object ResultArbitraries {
  import Error._

  implicit def ErrorArbitrary: Arbitrary[Error] =
    Arbitrary(frequency(
      (1, arbitrary[String] map NotANumber),
      (1, arbitrary[String] map InvalidOperation),
      (1, arbitrary[String] map UnexpectedInput),
      (1, NotEnoughInput)
    ))

  import Result._

  implicit def ResultArbitrary[A: Arbitrary]: Arbitrary[Result[A]] =
    Arbitrary(frequency(
      (1, arbitrary[Error] map fail),
      (4, arbitrary[A] map ok)
    ))
}
