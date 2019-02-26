package com.tidx

final case class UserProfile(lastProductsSeen: Vector[String]) extends AnyVal {
  def update(product: String, maxProducts: Int): UserProfile =
    UserProfile((product +: lastProductsSeen).take(maxProducts))
}

object UserProfile {
  val Empty = UserProfile(Vector.empty)
}
