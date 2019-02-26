package com.tidx

final case class UserProfile(
    lastProductsSeen: Vector[String] = Vector.empty,
    lastSeenAt: Option[(Float, Float)] = None
) {
  def update(product: String, maxProducts: Int): UserProfile =
    copy(lastProductsSeen = (product +: lastProductsSeen).take(maxProducts))
}

object UserProfile {
  val Empty = UserProfile()
}
