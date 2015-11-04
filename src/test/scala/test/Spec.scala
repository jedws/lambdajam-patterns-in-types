package test

import org.specs2._

abstract class Spec
  extends mutable.Specification
  with ScalaCheck

abstract class Spec2
  extends Specification
  with ScalaCheck
