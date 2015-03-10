package challenge9

/**
 * *Challenge* Parse a naive personnel record.
 *
 * We have a set of personnel records with a "special" format.
 *
 * Produce a person parser for a record.
 */
object PersonParser {
  /*
   * A data structure representing a person with the following attributes:
   *  - name: non empty string that starts with a capital letter
   *  - age: positive integer
   *  - address: non empty string that starts with a capital letter
   *  - phone: string of digits, dots or hyphens that must start with a
   *           digit and end with a hash (#)
   */
  case class Person(name: String, age: Int, phone: String, address: Address)

  /*
   * A data structure representing an address with the following attributes:
   *  - number: positive integer
   *  - street: non empty string
   */
  case class Address(number: Int, street: String)

  /**
   * Parse a name, which is a non-empty string that starts with a capital letter.
   */
  def nameParser: Parser[String] =
    ???

  /**
   * Parse a phone number, which is a string of digits, dots or hyphens that
   * starts with a digit and ends with a hash (#).
   */
  def phoneParser: Parser[String] =
    ???

  /**
   * An address is a positive street number and a non empty string for the
   * street name.
   */
  def addressParser: Parser[Address] =
    ???

  /**
   * An person record is the following parts, each seperated by one or more spaces.
   *
   *  <name> <age> <phone> <address>
   *
   * scala> PersonParser.personParser.run("Homer 39 555.123.939# 742 evergreen")
   *  = Ok(ParseState(,Person(Homer,39,555.123.939#,Address(742,evergreen))))
   */
  def personParser: Parser[Person] =
    ???

  /**
   * Parse all records.
   *
   * Example usage:
   *
   *   PersonParser.parseAll(PersonParser.Data)
   *
   * Hint: Use Parser.sequence
   */
  def parseAll(data: List[String]): Result[List[Person]] =
    ???

  def Data = List(
    "Fred 32 123.456-1213# 301 cobblestone", "Barney 31 123.456.1214# 303 cobblestone", "Homer 39 555.123.939# 742 evergreen", "Flanders 39 555.123.939# 744 evergreen"
  )
}