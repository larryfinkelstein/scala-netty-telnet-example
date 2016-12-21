import java.net.InetAddress
import java.util.Date
import java.util.concurrent.SynchronousQueue

import com.typesafe.scalalogging.Logger
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}

/**
  * Created by larryf on 12/18/2016.
  *
  * Handles a client-side channel.
  *
  */
object TelnetClientHandler {
  var msgs = new SynchronousQueue[String]
}

class TelnetClientHandler extends SimpleChannelInboundHandler[String] {
  import TelnetClientHandler.msgs

  val log = Logger("TelnetClientHandler")

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    log info "channel active"
    // Send greeting for a new connection.
//    ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
//    ctx.write("It is " + new Date() + " now.\r\n");
//    ctx.flush();
    super.channelActive(ctx)
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
    log info s"Received $msg"
//    msgs.add(msg)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: AnyRef): Unit = {
    log info msg.toString
    super.channelRead(ctx, msg)
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
//    super.channelReadComplete(ctx)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
//    super.exceptionCaught(ctx, cause)
  }
}
