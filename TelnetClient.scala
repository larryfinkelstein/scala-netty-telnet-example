import java.io.{BufferedReader, InputStreamReader}

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

import scala.util.control.Breaks.{break, breakable}

/**
  * Created by lfinke200 on 12/19/16.
  */
/**
  * Simplistic telnet client.
  */
object TelnetClient {
  def main(args: Array[String]) {
    // Print usage if no argument is specified.
    if (args.length != 2) {
      System.err.println(
        "Usage: " + TelnetClient.getClass.getSimpleName +
          " <host> <port>")
      return
    }

    // Parse options.
    val host = args(0)
    val port = args(1).toInt

    val group: EventLoopGroup = new NioEventLoopGroup()

    try {
      val b: Bootstrap = new Bootstrap()
      b.group(group)
        .channel(classOf[NioSocketChannel])
        .handler(new TelnetClientInitializer)

      // Start the connection attempt.
      val ch: Channel = b.connect(host, port).sync().channel()

      // Read commands from the stdin.
      var lastWriteFuture: ChannelFuture = null
      val in: BufferedReader = new BufferedReader(new InputStreamReader(System.in))

      //TODO rewrite the loop as a recursive function. (Refer to Programming in Scala, 7.6 Living without break and continue)
      breakable {
        while (true) {
          val line = in.readLine
          if (line == null) break

          // Sends the received line to the server.
          lastWriteFuture = ch.writeAndFlush(line + "\r\n")

          // If user typed the 'bye' command, wait until the server closes
          // the connection.
          if ("bye".equals(line.toLowerCase())) {
            ch.closeFuture().sync()
            break
          }
        }
      }

      // Wait until all messages are flushed before closing the channel.
      if (lastWriteFuture != null) {
        lastWriteFuture.sync()
      }
    } finally {
      group.shutdownGracefully()
    }
  }
}
