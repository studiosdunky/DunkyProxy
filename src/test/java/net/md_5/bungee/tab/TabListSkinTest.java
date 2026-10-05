package net.md_5.bungee.tab;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.protocol.data.Property;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TabListSkinTest
{
    @Test
    void forwardsBackendSkinWhileRewritingPlayerIdentity()
    {
        PlayerListItem.Item item = new PlayerListItem.Item();
        item.setUuid( UUID.randomUUID() );
        UUID clientId = UUID.randomUUID();
        Property[] skin = { new Property( "textures", "new-texture", "new-signature" ) };
        item.setProperties( skin );
        item.setGamemode( 1 );
        item.setPing( 42 );
        AtomicInteger gamemode = new AtomicInteger();
        AtomicInteger ping = new AtomicInteger();
        TabList.rewritePlayer( item, clientId, gamemode::set, ping::set );
        assertEquals( clientId, item.getUuid() );
        assertSame( skin, item.getProperties() );
        assertEquals( "new-signature", item.getProperties()[0].getSignature() );
        assertEquals( 1, gamemode.get() );
        assertEquals( 42, ping.get() );
    }

    @Test
    void preservesResetAndPartialPlayerUpdates()
    {
        PlayerListItem.Item item = new PlayerListItem.Item();
        Property[] reset = new Property[0];
        item.setProperties( reset );
        TabList.rewritePlayer( item, UUID.randomUUID(), value -> fail(), value -> fail() );
        assertSame( reset, item.getProperties() );
        item.setProperties( null );
        TabList.rewritePlayer( item, UUID.randomUUID(), value -> fail(), value -> fail() );
        assertNull( item.getProperties() );
    }
}
