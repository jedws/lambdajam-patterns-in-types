package challenge9

import challenge0._, EqualSyntax._

object Challenge9ParserSpec extends test.Spec {
  import Laws._
  import ParserArbitraries._
  import ResultArbitraries._

  "Parser" should {
    "satisfy monad laws" ! monad.laws[Parser]

    "parse a value" ! prop { (i: Int, s: String) =>
      Parser.value(i).run(s).successLike { case ParseState(`s`, `i`) => true }
    }
    "parse a failed" ! prop { (e: Error, s: String) =>
      Parser.failed[Int](e).run(s).failedLike { case `e` => true }
    }
    "parse a list of chars" ! prop { s: String =>
      Parser.list(Parser.character).run(s).success(
        state => state.input === "" && state.value === s.toList
      )
    }
    "parse a list of chars failing if empty" ! prop { s: String =>
      Parser.list1(Parser.character).run(s).fold {
        _ === Error.NotEnoughInput && s.isEmpty
      } {
        case ParseState(input, value) => input === "" && value === s.toList
      }
    }
    "parse a char using a predicate" ! prop { s: String =>
      Parser.satisfy { c => !s.isEmpty && c == s.head }.run(s).parsedChar(s)
    }
    "parse a char correctly" ! prop { s: String =>
      Parser.is(if (s.isEmpty) 'a' else s.head).run(s).parsedChar(s)
    }
    "parse a char failing" ! prop { s: String =>
      Parser.is(if (s.isEmpty) 'a' else (s.head + 1).toChar).run(s).failedLike {
        case Error.NotEnoughInput if s.isEmpty                 => true
        case Error.UnexpectedInput(u) if u === s.head.toString => true
      }
    }
    "digit parses a number correctly" ! prop { i: Int =>
      val s = (if (i < 0) -i else i).toString
      Parser.digit.run(s).parsedChar(s)
    }
    "digit fails to parse if not a number" ! prop { s: String =>
      Parser.digit.run(s).fold {
        case Error.NotEnoughInput if s.isEmpty                 => true
        case Error.UnexpectedInput(u) if u === s.head.toString => u.length === 1 && u.head.isDigit
      } {
        case ParseState(input, value) => input === s.tail && value === s.head
      }
    }

    "parse an alpha char" ! prop { (in: String) =>
      (in, Parser.alpha.run("")) match {
        case ("", r)                     => r.fold { _ === Error.NotEnoughInput } { _ => false }
        case (s, r) if (s.head.isLetter) => r.fold { _ => false } { case ParseState(ss, a) => ss === s.tail && a === s.head }
        case (s, r)                      => r.fold { _ === Error.UnexpectedInput(s.head.toString) } { _ => false }
      }
    }
  }

  implicit class ParseStateOps[A](p: Result[ParseState[A]]) {
    def failed(f: Error => Boolean): Boolean =
      p.fold { f } { _ => false }
    def failedLike(f: PartialFunction[Error, Boolean]): Boolean =
      p.fold { e => if (f.isDefinedAt(e)) f(e) else false } { _ => false }
    def success(f: ParseState[A] => Boolean): Boolean =
      p.fold { _ => false } { f }
    def successLike(f: PartialFunction[ParseState[A], Boolean]): Boolean =
      p.fold { _ => false } { s => if (f.isDefinedAt(s)) f(s) else false }
    def parsedChar(s: String)(implicit isChar: A <:< Char): Boolean =
      p.fold {
        _ === Error.NotEnoughInput && s.isEmpty
      } {
        state => state.input === s.tail && isChar(state.value) === s.head
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
