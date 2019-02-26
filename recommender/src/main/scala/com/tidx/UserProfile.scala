package com.tidx

final case class UserProfile(
    lastProductsSeen: Vector[String] = Vector.empty,
    lastSeenAt: Option[(Float, Float)] = None
) {
  def update(product: String, maxProducts: Int): UserProfile =
    copy(lastProductsSeen = (product +: lastProductsSeen).take(maxProducts))

//  def update(location: Option[com.tidx.LatLon]): UserProfile = {
//    val maybeNewLocation = location.map(l => (l.getLat.floatValue(), l.getLon.floatValue()))
//    copy(lastSeenAt = maybeNewLocation orElse lastSeenAt)
//  }
}

object UserProfile {
  val Empty = UserProfile()
}
