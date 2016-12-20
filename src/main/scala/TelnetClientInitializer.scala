import io.netty.channel.socket.SocketChannel
import io.netty.channel.{ChannelInitializer, ChannelPipeline}
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import io.netty.handler.codec.{DelimiterBasedFrameDecoder, Delimiters}

/**
  * Created by lfinke200 on 12/19/16.
  */

object TelnetClientInitializer {
  val decoder:StringDecoder = new StringDecoder
  val encoder:StringEncoder = new StringEncoder

}

class TelnetClientInitializer extends ChannelInitializer[SocketChannel] {

  override def initChannel(ch: SocketChannel): Unit = {

    import TelnetClientInitializer._

    val frameLength:Int = 8192

    val pipeline:ChannelPipeline = ch.pipeline

    val frameDecoder = new DelimiterBasedFrameDecoder(frameLength, (Delimiters.lineDelimiter): _*)
    pipeline.addLast(frameDecoder)
    pipeline.addLast(decoder)
    pipeline.addLast(encoder)

    val clientHandler:TelnetClientHandler = new TelnetClientHandler
    pipeline.addLast(clientHandler)
  }

}
