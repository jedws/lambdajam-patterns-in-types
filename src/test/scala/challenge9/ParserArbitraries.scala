package challenge9

import org.scalacheck.{ Arbitrary, Gen }, Arbitrary._, Gen._
import challenge0._, Equal._

object ParserArbitraries {
  implicit def ParserArbitrary[A: Arbitrary]: Arbitrary[Parser[A]] =
    Arbitrary { arbitrary[A] map Parser.value }

  def positive(i: Int) =
    if (i == Int.MinValue) Int.MaxValue
    else Math.abs(i)

  implicit class ParseStateOps[A](res: Result[ParseState[A]]) {
    def failed(f: Error => Boolean): Boolean =
      res.fold { f } { _ => false }
    def failedLike(f: PartialFunction[Error, Boolean]): Boolean =
      res.fold { e => if (f.isDefinedAt(e)) f(e) else false } { _ => false }
    def success(f: ParseState[A] => Boolean): Boolean =
      res.fold { _ => false } { f }
    def successLike(f: PartialFunction[ParseState[A], Boolean]): Boolean =
      res.fold { _ => false } { s => if (f.isDefinedAt(s)) f(s) else false }
    def parsedChar(s: String)(implicit isChar: A <:< Char): Boolean =
      parsedCharLike(s)(_ => true)
    def parsedCharLike(s: String)(p: Char => Boolean)(implicit char: A <:< Char): Boolean =
      res.fold {
        case Error.NotEnoughInput if s.isEmpty      => true
        case Error.UnexpectedInput(c) if !p(c.head) => true
      } {
        case ParseState(rest, value) => rest === s.tail && char(value) === s.head && p(char(value))
      }
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