package net.md_5.bungee.util;
import io.netty.buffer.Unpooled;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class HttpProbeTest {
 @Test public void rejectsHttpProbeWithoutConsumingBuffer() {
  for(String text:new String[]{"ONNECT / HTTP/1.1","CONNECT example:443 HTTP/1.1","GET / HTTP/1.0","ET / HTTP/1.1"}) {
   ByteBuf buf=Unpooled.copiedBuffer(text,StandardCharsets.US_ASCII);
   try{int index=buf.readerIndex();assertTrue(HttpProbe.matches(buf));assertEquals(index,buf.readerIndex());}finally{buf.release();}
  }
 }
 @Test public void preservesMinecraftAndOtherInvalidPacketHandling() {
  for(String text:new String[]{"","CONNECT","ONNECT /","minecraft HTTP/1.1","HTTP/1.1 200 OK"}) {
   ByteBuf buf=Unpooled.copiedBuffer(text,StandardCharsets.US_ASCII);
   try{assertFalse(HttpProbe.matches(buf));}finally{buf.release();}
  }
 }
}
