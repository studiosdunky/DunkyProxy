package net.md_5.bungee;

import java.util.ArrayDeque;
import java.util.Queue;
import net.md_5.bungee.ServerConnection.KeepAliveData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KeepAliveQueueTest
{
    @Test
    void laterValidReplyRecoversQueue()
    {
        Queue<KeepAliveData> queue = new ArrayDeque<>();
        KeepAliveData first = new KeepAliveData( -123, 100 );
        KeepAliveData second = new KeepAliveData( 456, 200 );
        KeepAliveData third = new KeepAliveData( 789, 300 );
        queue.add( first ); queue.add( second ); queue.add( third );
        assertSame( second, ServerConnection.acknowledgeKeepAlive( queue, 456 ) );
        assertEquals( 1, queue.size() );
        assertSame( third, queue.peek() );
        assertNull( ServerConnection.acknowledgeKeepAlive( queue, -123 ) );
        assertSame( third, queue.peek() );
    }

    @Test
    void unknownRepliesDoNotConsumeChallenges()
    {
        Queue<KeepAliveData> queue = new ArrayDeque<>();
        KeepAliveData challenge = new KeepAliveData( -123, 100 );
        queue.add( challenge );
        assertNull( ServerConnection.acknowledgeKeepAlive( queue, 0 ) );
        assertSame( challenge, queue.peek() );
        assertSame( challenge, ServerConnection.acknowledgeKeepAlive( queue, -123 ) );
        assertTrue( queue.isEmpty() );
        assertNull( ServerConnection.acknowledgeKeepAlive( queue, -123 ) );
    }
}
