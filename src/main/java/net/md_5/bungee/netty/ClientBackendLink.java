package net.md_5.bungee.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

/** Cancels backend connections when their client leaves, without retaining old server channels. */
public final class ClientBackendLink
{
    private ClientBackendLink() {}

    public static void attach(Channel client, Channel backend, Runnable clientClosed)
    {
        if ( !client.isActive() )
        {
            clientClosed.run();
            backend.close();
            return;
        }
        ChannelFutureListener listener = ignored -> {
            clientClosed.run();
            backend.close();
        };
        client.closeFuture().addListener( listener );
        backend.closeFuture().addListener( ignored -> client.closeFuture().removeListener( listener ) );
    }
}
