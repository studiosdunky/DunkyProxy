package net.md_5.bungee.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Command;

public class CommandReload extends Command
{

    public CommandReload()
    {
        super( "reload", "bungeecord.command.reload" );
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        // DunkyProxy: relê o config.yml e o messages.properties sem derrubar ninguém.
        BungeeCord bungee = BungeeCord.getInstance();
        try
        {
            bungee.config.load();
        } catch ( RuntimeException ex )
        {
            // Config com erro: o proxy continua com o que já estava carregado.
            Throwable cause = ( ex.getCause() != null ) ? ex.getCause() : ex;
            sender.sendMessage( bungee.getTranslation( "reload_failed", String.valueOf( cause.getMessage() ) ) );
            return;
        }
        bungee.reloadMessages();
        bungee.stopListeners();
        bungee.startListeners();
        bungee.getPluginManager().callEvent( new ProxyReloadEvent( sender ) );

        sender.sendMessage( bungee.getTranslation( "reload_done" ) );
    }
}
