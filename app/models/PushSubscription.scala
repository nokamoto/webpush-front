package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class PushSubscription(endpoint: String, auth: String, p256dh: String)

object PushSubscription {
  implicit val format: OFormat[PushSubscription] = Json.format[PushSubscription]
}
