package challenge9

import challenge0._, EqualSyntax._

object Challenge9ResultSpec extends test.Spec {
  import Laws._
  import ResultArbitraries._

  "Result" should {
    "satisfy equal laws with Int" ! equal.laws[Result[Int]]

    "satisfy equal laws with String" ! equal.laws[Result[String]]

    "satisfy monad laws" ! monad.laws[Result]

    "fold with fail" ! prop((e: Error) =>
      Result.fail[Int](e).fold(_ === e, _ => false))

    "fold with ok" ! prop((i: Int) =>
      Result.ok[Int](i).fold(_ => false, _ === i))
  }
}
