package challenge9

import challenge0._, EqualSyntax._

object Challenge9ParserSpec extends test.Spec {
  import Laws._
  import ParserArbitraries._

  "Parser" should {
    "satisfy monad laws" ! monad.laws[Parser]

    //    "fold with fail" ! prop((e: Error) =>
    //      Result.fail[Int](e).fold(_ === e, _ => false))
    //
    //    "fold with ok" ! prop((i: Int) =>
    //      Result.ok[Int](i).fold(_ => false, _ === i))
  }

  implicit def ParseStateEqual[A: Equal]: Equal[ParseState[A]] =
    Equal.from[ParseState[A]] {
      case (ParseState(s1, a1), ParseState(s2, a2)) if (s1 === s2 && a1 === a2) => true
      case _ => false
    }

  implicit def ParserEqual[A: Equal]: Equal[Parser[A]] =
    Equal.from[Parser[A]]((a, b) =>
      a.run("") === b.run(""))
}
