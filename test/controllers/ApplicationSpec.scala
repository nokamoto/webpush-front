package controllers

import com.github.nokamoto.webpush.protobuf.Message
import com.github.nokamoto.webpush.protobuf.PushServiceGrpc.PushServiceStub
import com.google.protobuf.empty.Empty
import io.grpc.Status
import io.grpc.StatusRuntimeException
import models.PushSubscription
import modules.ApplicationServerKeyModule.ApplicationServerKey
import org.mockito.ArgumentCaptor
import org.scalatest.FlatSpec
import org.scalatest.mockito.MockitoSugar
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

import scala.concurrent.Future

class ApplicationSpec extends FlatSpec with MockitoSugar {
  private[this] val key = ApplicationServerKey("")
  private[this] val subscription = PushSubscription(
    endpoint =
      "https://fcm.googleapis.com/fcm/send/d2IpmZLnC5I:APA91bGCR-vGtFrsyFwRQuQTwdvOj3gZqfOI1h-XYVo-8JNSYJzw9ilSIg0u3wFCtFA1LvSZEEc2m1MXzK8M1pMO-KPbhBti1bWBthJP2KXmwkXdwu20HR6nbe8jpVp_4CYvTViC2jH1",
    auth = "US319q0fIaKsmFVsz+58oA==",
    p256dh =
      "BFSPtZiGljUsF85Jtxs9rrWg+ojLhPz1NZXTPA1+GMbUW/H8sM/iENRKM0YcBNlQLhd5u+wAqVERzAIb/vHZ11Q="
  )

  "Application#test" should "return OK if webpush service returns empty" in {
    val stub = mock[PushServiceStub]
    val sut = new Application(key, stub, stubControllerComponents())

    when(stub.send(any())).thenReturn(Future.successful(Empty()))

    val res = sut.test(FakeRequest().withBody(subscription))
    assert(status(res) === OK)

    val captor: ArgumentCaptor[Message] =
      ArgumentCaptor.forClass(classOf[Message])
    verify(stub, times(1)).send(captor.capture())

    assert(captor.getValue.getSubscription === subscription.asProto)
  }

  "Application#test" should "return OK even if empty subscription" in {
    val stub = mock[PushServiceStub]
    val sut = new Application(key, stub, stubControllerComponents())

    when(stub.send(any())).thenReturn(Future.successful(Empty()))

    val res = sut.test(FakeRequest().withBody(PushSubscription("", "", "")))
    assert(status(res) === OK)

    verify(stub, times(1)).send(any())
  }

  "Application#test" should "return NOT IMPLEMENTED if webpush service returns UNIMPLEMENTED" in {
    val stub = mock[PushServiceStub]
    val sut = new Application(key, stub, stubControllerComponents())

    when(stub.send(any())).thenReturn(
      Future.failed(new StatusRuntimeException(Status.UNIMPLEMENTED)))

    val res = sut.test(FakeRequest().withBody(subscription))
    assert(status(res) === NOT_IMPLEMENTED)
  }

  "Application#test" should "return INTERNAL SERVER ERROR if webpush service returns UNAVAILABLE" in {
    val stub = mock[PushServiceStub]
    val sut = new Application(key, stub, stubControllerComponents())

    when(stub.send(any()))
      .thenReturn(Future.failed(new StatusRuntimeException(Status.UNAVAILABLE)))

    val res = sut.test(FakeRequest().withBody(subscription))
    assert(status(res) === INTERNAL_SERVER_ERROR)
  }
}
