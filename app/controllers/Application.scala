package controllers

import java.time.LocalDateTime

import com.github.nokamoto.webpush.protobuf.Message
import com.github.nokamoto.webpush.protobuf.PushServiceGrpc.PushServiceStub
import io.grpc.StatusRuntimeException
import javax.inject.Inject
import models.PushSubscription
import modules.ApplicationServerKeyModule.ApplicationServerKey
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class Application @Inject()(applicationServerKey: ApplicationServerKey,
                            stub: PushServiceStub,
                            cc: ControllerComponents)
    extends AbstractController(cc) {
  implicit private[this] val ctx: ExecutionContext = cc.executionContext

  def index: Action[AnyContent] =
    Action(_ => Ok(views.html.index(applicationServerKey.value)))

  def test: Action[PushSubscription] =
    Action.async(parse.json[PushSubscription]) { req =>
      println(s"test ${Json.toJson(req.body)}")
      val msg = Message().update(
        _.subscription := req.body.asProto,
        _.ttl := 60,
        _.plaintext := Json
          .obj("body" -> s"test message at ${LocalDateTime.now()}")
          .toString())
      stub.send(msg).map(_ => Ok(Json.obj())).recover {
        case e: StatusRuntimeException
            if e.getStatus == io.grpc.Status.UNIMPLEMENTED =>
          println(s"unimplemented functionality")
          e.printStackTrace()
          NotImplemented(Json.obj("error" -> e.getMessage))

        case NonFatal(e) =>
          e.printStackTrace()
          InternalServerError(Json.obj("error" -> e.getMessage))
      }
    }
}
