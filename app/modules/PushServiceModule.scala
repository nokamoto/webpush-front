package modules
import com.github.nokamoto.webpush.protobuf.PushServiceGrpc
import com.github.nokamoto.webpush.protobuf.PushServiceGrpc.PushServiceStub
import com.google.inject.AbstractModule
import io.grpc.netty.NettyChannelBuilder
import play.api.Configuration
import play.api.Environment

class PushServiceModule(environment: Environment, configuration: Configuration)
    extends AbstractModule {
  override def configure(): Unit = {
    val host = configuration.get[String]("grpc.host")
    val port = configuration.get[Int]("grpc.port")

    val channel =
      NettyChannelBuilder.forAddress(host, port).usePlaintext().build()
    val stub = PushServiceGrpc.stub(channel)
    bind(classOf[PushServiceStub]).toInstance(stub)
  }
}
