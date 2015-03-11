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
    "parse a character" ! prop { s: String =>
      Parser.character.run(s).parsedChar(s)
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
    "parse a specific char correctly" ! prop { s: String =>
      Parser.is(if (s.isEmpty) 'a' else s.head).run(s).parsedChar(s)
    }
    "parse a specific char failing" ! prop { s: String =>
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
      val pos = positive(i)
      val s = pos + "!" + ss
      Parser.natural.run(s).success {
        case ParseState(rest, `pos`) => rest.tail === ss
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
}
