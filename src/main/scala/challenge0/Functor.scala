package challenge0

trait Functor[F[_]] {
  def map[A, B](a: F[A])(f: A => B): F[B]
}

object Functor {
  def apply[F[_]: Functor]: Functor[F] =
    implicitly[Functor[F]]

}

trait FunctorLaws {
  def identity[F[_], A](fa: F[A])(implicit F: Functor[F], FC: Equal[F[A]]): Boolean =
    FC.equal(fa, F.map(fa)(a => a))

  def associativeMap[F[_], A, B, C](fa: F[A], f: A => B, g: B => C)(implicit FC: Equal[F[C]], F: Functor[F]): Boolean =
    FC.equal(F.map(F.map(fa)(f))(g), F.map(fa)(f andThen g))
}

object FunctorLaws extends FunctorLaws
