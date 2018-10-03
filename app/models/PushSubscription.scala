package models

import java.util.Base64

import play.api.libs.json.Json
import play.api.libs.json.OFormat
import com.github.nokamoto.webpush.protobuf
import com.google.protobuf.ByteString

case class PushSubscription(endpoint: String, auth: String, p256dh: String) {
  private[this] def decode(s: String): ByteString =
    ByteString.copyFrom(Base64.getDecoder.decode(s))

  def asProto: protobuf.PushSubscription =
    protobuf
      .PushSubscription()
      .update(_.endpoint := endpoint,
              _.auth := decode(auth),
              _.p256Dh := decode(p256dh))
}

object PushSubscription {
  implicit val format: OFormat[PushSubscription] = Json.format[PushSubscription]
}
