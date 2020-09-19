import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.imageio.ImageIO;

public class ZebraKR203test {
  static public void main(String args[]) throws Exception {
    try {

      OutputStream oStreamKplFile = new FileOutputStream("zebratest_kpl_output", false);
      PrintWriter printerKplFile = new PrintWriter(new OutputStreamWriter(oStreamKplFile, "ISO_8859_1"));

      OutputStream oStreamScriptFile = new FileOutputStream("zebratest_kpl_command.sh", false);
      PrintWriter printerScriptFile = new PrintWriter(new OutputStreamWriter(oStreamScriptFile));

      OutputStream oStream = new FileOutputStream("/dev/usb/lp0");
      PrintWriter printer = new PrintWriter(new OutputStreamWriter(oStream, "ISO_8859_1"));

      int width = 640, height = 300;

      BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


      Graphics2D ig2 = bi.createGraphics();

      ig2.setColor(Color.white);
      ig2.fillRect(0, 0, width, height);

      double x_translate = 20;
      double y_translate = 0;
      int bitnum = 7;
      int byteval = 0;
      int bytes_for_row_counts = 0;
      String kpl_echo_command_str_full = "";
      String kpl_echo_command_str_tmp = "";
      String kpl_str_tmp = "";
      String kpl_str_full = "";
      int font_size = 40;
      int start_y = 0;

      ig2.translate(x_translate, y_translate);
      ig2.setColor(Color.black);
      ig2.setFont(new Font("TimesRoman", Font.PLAIN, font_size));
      start_y = 50;
      ig2.drawString("Ticket", 105, start_y);
      start_y += font_size;
      ig2.drawString("Date: ", 0, start_y);
      start_y += font_size;
      ig2.drawString("Hour: ", 0, start_y);
      start_y += font_size;
      ig2.drawString("N: " , 0, start_y);
      start_y += font_size;
      ig2.drawString("Product: " , 0, start_y);
      start_y += font_size;
      ig2.drawString("Qta: ", 0, start_y);

      int[][] array2D = new int[bi.getWidth()][bi.getHeight()];

      for (int yPixel = 0; yPixel < bi.getHeight(); yPixel++) {

        bytes_for_row_counts = 0;
        kpl_echo_command_str_tmp = "";
        kpl_str_tmp = "";

        for (int xPixel = 0; xPixel < bi.getWidth(); xPixel++) {

          int color = bi.getRGB(xPixel, yPixel);
          if (color==Color.BLACK.getRGB()) {
            array2D[xPixel][yPixel] = 1;
            byteval |= 1 << bitnum;      // sets bit 'n' to 1
          } else {
            array2D[xPixel][yPixel] = 0; // ?
            //i &= ~(1 << n);   // sets bit 'n' to 0
          }

          if (bitnum == 0) {
            //System.out.println(byteval);
            kpl_echo_command_str_tmp += "\\\\x" + Integer.toHexString(byteval);

            kpl_str_tmp += (char) byteval;
            byteval = 0;
            bitnum = 8;
            bytes_for_row_counts++;
          }

          bitnum--;

        }

        // genera il comando KPL <ESC>s<n><B ..... > per il bash script di prova
        kpl_echo_command_str_full += "echo -n -e \\\\x1b\\\\x73" + "\\\\x" + Integer.toHexString(bytes_for_row_counts) + kpl_echo_command_str_tmp + " > /dev/usb/lp0\n";
        //System.out.println(kpl_echo_command_str);

        //System.out.println("bytes for row counts" + bytes_for_row_counts);

        kpl_str_full += new StringBuilder().append((char) 27).append((char) 115).append((char) bytes_for_row_counts).append(kpl_str_tmp).toString(); 

      }

      // genera comando KPL <RS>255 per bash script di prova
      kpl_echo_command_str_full += "echo -n -e \\\\x1e\\\\xff > /dev/usb/lp0";
      //System.out.println(kpl_echo_command_str_full);

      kpl_str_full += new StringBuilder().append((char) 30).append((char) 255).toString(); 


      printerKplFile.print(kpl_str_full);
      printerKplFile.flush();
      printerKplFile.close();
      oStreamKplFile.close();

      printerScriptFile.print(kpl_echo_command_str_full);
      printerScriptFile.flush();
      printerScriptFile.close();
      oStreamScriptFile.close();

      printer.print(kpl_str_full);
      printer.flush();
      printer.close();
      oStream.close();


      ImageIO.write(bi, "BMP", new File("zebratest.bmp"));


    } catch (IOException ie) {
      ie.printStackTrace();
    }

  }

}
