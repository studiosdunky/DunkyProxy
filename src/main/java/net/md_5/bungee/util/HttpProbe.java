package net.md_5.bungee.util;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

/** Recognizes HTTP text after the Minecraft decoder consumed its first byte. */
public final class HttpProbe {
    private HttpProbe() {}
    public static boolean matches(ByteBuf buffer) {
        String text=buffer.toString(buffer.readerIndex(),Math.min(buffer.readableBytes(),96),StandardCharsets.US_ASCII);
        if(!text.contains(" HTTP/1."))return false;
        for(String method:new String[]{"CONNECT ","ONNECT ","GET ","ET ","POST ","OST ","HEAD ","EAD ","OPTIONS ","PTIONS ","PUT ","UT ","DELETE ","ELETE ","PATCH ","ATCH "})
            if(text.startsWith(method))return true;
        return false;
    }
}
