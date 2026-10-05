package net.md_5.bungee;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * DunkyProxy: escolhe para qual lobby vai quem ficou sem servidor (o servidor reiniciou, caiu ou expulsou o jogador).
 * Os lobbies são os servidores de "priorities" do listener. De tempos em tempos o proxy confere quais estão no ar,
 * para não mandar ninguém para um lobby desligado, e entre os que estão no ar escolhe o mais vazio.
 */
public class Fallback
{

    private static final long CHECK_MILLIS = 2000L;

    private final BungeeCord bungee;
    private final Set<String> offline = ConcurrentHashMap.newKeySet();
    private final Timer timer = new Timer( "Fallback Monitor", true );

    public Fallback(BungeeCord bungee)
    {
        this.bungee = bungee;
    }

    public void start()
    {
        timer.scheduleAtFixedRate( new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    check();
                } catch ( RuntimeException ex )
                {
                    // Uma conferência que falhou não pode parar as próximas.
                }
            }
        }, CHECK_MILLIS, CHECK_MILLIS );
    }

    public void stop()
    {
        timer.cancel();
    }

    /**
     * O servidor acabou de derrubar a conexão de alguém: fica fora das escolhas até responder de novo.
     */
    public void markOffline(ServerInfo server)
    {
        offline.add( server.getName() );
    }

    /**
     * Lobby que está no ar e com menos jogadores, ou null quando não há nenhum disponível.
     *
     * @param listener listener por onde o jogador entrou
     * @param exclude servidores que não servem (o que caiu e os que já falharam nesta tentativa)
     * @return o lobby escolhido ou null
     */
    public ServerInfo next(ListenerInfo listener, Collection<String> exclude)
    {
        ServerInfo best = null;
        for ( String name : priorities( listener ) )
        {
            ServerInfo candidate = bungee.getServerInfo( name );
            if ( candidate == null || offline.contains( candidate.getName() ) || contains( exclude, candidate.getName() ) )
            {
                continue;
            }
            if ( best == null || candidate.getPlayers().size() < best.getPlayers().size() )
            {
                best = candidate;
            }
        }
        return best;
    }

    /**
     * Quem é expulso de um servidor vai para um lobby, menos quando o motivo é de punição (fallback_ignore_kicks).
     *
     * @param reason motivo da expulsão
     * @return true quando o jogador deve ir para um lobby
     */
    public boolean redirectsKick(String reason)
    {
        String text = ( reason == null ) ? "" : reason.toLowerCase( Locale.ROOT );
        for ( String ignored : bungee.config.getFallbackIgnoreKicks() )
        {
            if ( ignored != null && !ignored.isEmpty() && text.contains( ignored.toLowerCase( Locale.ROOT ) ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * As prioridades do config.yml de agora (um reload pode ter mudado as do listener em que o jogador entrou).
     */
    private Collection<String> priorities(ListenerInfo listener)
    {
        for ( ListenerInfo current : bungee.config.getListeners() )
        {
            if ( current.getSocketAddress().equals( listener.getSocketAddress() ) )
            {
                return current.getServerPriority();
            }
        }
        return listener.getServerPriority();
    }

    private void check()
    {
        Set<String> lobbies = new LinkedHashSet<>();
        for ( ListenerInfo listener : bungee.config.getListeners() )
        {
            lobbies.addAll( listener.getServerPriority() );
        }
        for ( String name : lobbies )
        {
            ServerInfo server = bungee.getServerInfo( name );
            if ( server == null )
            {
                continue;
            }
            server.ping( (result, error) ->
            {
                if ( error != null || result == null )
                {
                    offline.add( server.getName() );
                } else
                {
                    offline.remove( server.getName() );
                }
            } );
        }
    }

    private static boolean contains(Collection<String> names, String name)
    {
        for ( String other : names )
        {
            if ( other.equalsIgnoreCase( name ) )
            {
                return true;
            }
        }
        return false;
    }
}
