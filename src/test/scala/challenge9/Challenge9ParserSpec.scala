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
      Parser.list(Parser.character).run(s).success {
        case ParseState("", list) => list === s.toList
      }
    }
    "parse a list of chars failing if empty" ! prop { s: String =>
      Parser.list1(Parser.character).run(s).fold {
        case Error.NotEnoughInput if s.isEmpty => true
      } {
        case ParseState(rest, list) => rest === "" && list === s.toList
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
      val s = positive(i).toString
      Parser.digit.run(s).parsedChar(s)
    }
    "digit parses a numeric char" ! prop { s: String =>
      Parser.digit.run(s).parsedCharLike(s) { _.isDigit }
    }
    "natural parses a number correctly" ! prop { (i: Int, ss: String) =>
      val s = positive(i) + ss
      Parser.natural.run(s).success {
        case ParseState(rest, n) => (n >= 0) && rest === ss
      }
    }
    "natural parser" ! prop { s: String =>
      Parser.natural.run(s).fold {
        case Error.NotEnoughInput if s.isEmpty                 => true
        case Error.UnexpectedInput(u) if u === s.head.toString => u.length === 1 && !u.head.isDigit
      } {
        case ParseState(rest, c) => rest === s.drop(c.toString.length) && c === s.take(c.toString.length).toInt
      }
    }
    "space parses a space correctly" ! prop { s: String =>
      Parser.space.run(" " + s).success {
        case ParseState(rest, n) => (n === ' ') && rest === s
      }
    }
    "space parser" ! prop { s: String =>
      Parser.space.run(s).parsedCharLike(s) { _ === ' ' }
    }
    "spaces1 parses spaces correctly" ! prop { (ii: Int, s: String) =>
      val i = Math.min(Math.abs(ii) + 1, 100)
      val spaces = Vector.fill(i)(' ').mkString
      Parser.spaces1.run(spaces + s).success {
        case ParseState(rest, n) => (n === spaces) && rest === s
      }
    }
    "spaces1 parser" ! prop { s: String =>
      Parser.spaces1.run(s).fold {
        case Error.NotEnoughInput if s.isEmpty                 => true
        case Error.UnexpectedInput(u) if u === s.head.toString => u.length === 1 && u.head != ' '
      } {
        case ParseState(rest, c) => rest === s.dropWhile { _ === ' ' } && c.forall { _ === ' ' }
      }
    }
    "lower parser" ! prop { s: String =>
      Parser.lower.run(s).parsedCharLike(s) { _.isLower }
    }
    "upper parser" ! prop { s: String =>
      Parser.upper.run(s).parsedCharLike(s) { _.isUpper }
    }
    "parse an alpha char" ! prop { s: String =>
      Parser.alpha.run(s).parsedCharLike(s) { _.isLetter }
    }
    "sequence succeeds" ! prop { (ps: List[Parser[Int]], s: String) =>
      Parser.sequence(ps).run(s).success {
        case ParseState(rest, is) => rest === s && is.length === ps.length
      }
    }
    "thisMany succeeds" ! prop { s: String =>
      Parser.thisMany(s.length, Parser.character).run(s).success {
        case ParseState(rest, is) => rest === "" && is === s.toList
      }
    }
    "thisMany fails" ! prop { s: String =>
      Parser.thisMany(s.length + 1, Parser.character).run(s).failed {
        _ === Error.NotEnoughInput
      }
    }
  }

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
