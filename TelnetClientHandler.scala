import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}

/**
  * Created by larryf on 12/18/2016.
  *
  * Handles a client-side channel.
  *
  */
class TelnetClientHandler extends SimpleChannelInboundHandler[String] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
    println(msg)
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
