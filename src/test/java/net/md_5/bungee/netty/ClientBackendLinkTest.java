package net.md_5.bungee.netty;

import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientBackendLinkTest
{
    @Test void clientLeavingAbortsPendingLogin()
    {
        EmbeddedChannel client = new EmbeddedChannel(), backend = new EmbeddedChannel();
        int[] closed = {0};
        ClientBackendLink.attach( client, backend, () -> closed[0]++ );
        client.close();
        assertFalse( backend.isActive() );
        assertEquals( 1, closed[0] );
        client.finishAndReleaseAll(); backend.finishAndReleaseAll();
    }
    @Test void serverSwitchRemovesOldListenerAndPreservesClient()
    {
        EmbeddedChannel client = new EmbeddedChannel(), old = new EmbeddedChannel(), next = new EmbeddedChannel();
        int[] oldCallbacks = {0}, newCallbacks = {0};
        ClientBackendLink.attach( client, old, () -> oldCallbacks[0]++ );
        old.close();
        assertTrue( client.isActive() );
        ClientBackendLink.attach( client, next, () -> newCallbacks[0]++ );
        client.close();
        assertEquals( 0, oldCallbacks[0] ); assertEquals( 1, newCallbacks[0] );
        assertFalse( next.isActive() );
        client.finishAndReleaseAll(); old.finishAndReleaseAll(); next.finishAndReleaseAll();
    }
    @Test void alreadyDisconnectedClientCannotStartBackendLogin()
    {
        EmbeddedChannel client = new EmbeddedChannel(), backend = new EmbeddedChannel();
        client.close();
        ClientBackendLink.attach( client, backend, () -> {} );
        assertFalse( backend.isActive() );
        client.finishAndReleaseAll(); backend.finishAndReleaseAll();
    }
}
