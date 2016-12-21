import java.io.{BufferedReader, IOException, InputStreamReader}

import com.typesafe.scalalogging.Logger
import io.netty.bootstrap.Bootstrap
import io.netty.channel.{AbstractChannel, Channel, ChannelFuture, EventLoopGroup}
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
  val log = Logger("TelnetClient")

  def main(args: Array[String]) {
    // Print usage if no argument is specified.
    if (args.length != 2) {
      log error s"Usage: ${TelnetClient.getClass.getSimpleName} <host> <port>"
    }
    // Parse options.
    val host = args(0)
    val port = args(1).toInt

    new TelnetClient(host, port).run()
  }

  class TelnetClient(host: String, port: Int) {

    def run() {
      val group: EventLoopGroup = new NioEventLoopGroup()
      try {
        val b: Bootstrap = new Bootstrap()
        b.group(group)
          .channel(classOf[NioSocketChannel])
          .handler(new TelnetClientInitializer)

        // Start the connection attempt.
        //FIXME: Can we get rid of sync?
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
            val start = System.currentTimeMillis()
            log info s"Sending $line"
            lastWriteFuture = ch.writeAndFlush(line + "\r\n")
            //            log info s"${System.nanoTime() - start}")
            lastWriteFuture.await()
            log info s"${System.currentTimeMillis() - start}"

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
      } catch {
        case e: IOException =>
          log error (e.getMessage)
        case e: Exception =>
          log error(e.getMessage, e)
      } finally {
        group.shutdownGracefully()
      }
    }
  }

}