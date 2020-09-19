import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

public class ZebraKR203ReadStatus  {
   public static void main(String[] args) throws Exception {

      OutputStream oStream = new FileOutputStream("/dev/usb/lp0");
      PrintWriter printer = new PrintWriter(new OutputStreamWriter(oStream, "ISO_8859_1"));

      String kpl_str_full = "";
      kpl_str_full += new StringBuilder().append((char) 0x1b).append((char) 0x26).append((char) 0x50).append((char) 0x41).append((char) 0x00).toString();
      kpl_str_full += new StringBuilder().append((char) 0x1b).append((char) 0x26).append((char) 0x50).append((char) 0x42).append((char) 0x00).toString();
      kpl_str_full += new StringBuilder().append((char) 0x1b).append((char) 0x05).append((char) 0x01).toString();


      printer.print(kpl_str_full);
      printer.flush();
      printer.close();
      oStream.close();


      AsynchronousFileChannel afc = AsynchronousFileChannel.open(Paths.get("/dev/usb/lp0"), StandardOpenOption.READ);
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      buffer.clear();
      byte[] receive;
      Future future;

      try  {

        future = afc.read(buffer, 0);
	future.get(500, TimeUnit.MILLISECONDS);

	// flip from filling to emptying
	buffer.flip();
 	receive = new byte[ buffer.remaining() ];
	buffer.get(receive);


        for (byte b : receive) {
            String st = String.format("%02X", b);
            System.out.print(st);
        }


	buffer.clear();

      } catch (TimeoutException e) {
         System.out.println("Timeout");


    }
  }
}
