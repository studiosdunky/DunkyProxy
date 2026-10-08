package net.md_5.bungee.log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogDispatcher extends Thread
{
    private static final LogRecord STOP = new LogRecord(Level.OFF, "shutdown");
    private final BungeeLogger logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();
    private boolean accepting = true;

    public LogDispatcher(BungeeLogger logger)
    {
        super("BungeeCord Logger Thread");
        this.logger = logger;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                LogRecord record = queue.take();
                if (record == STOP) return;
                logger.doLog(record);
            }
        } catch (InterruptedException interrupted)
        {
            Thread.currentThread().interrupt();
        } finally
        {
            logger.closeHandlers();
        }
    }

    public synchronized void queue(LogRecord record)
    {
        if (accepting) queue.add(record);
    }

    public synchronized void shutdown()
    {
        if (!accepting) return;
        accepting = false;
        queue.add(STOP);
    }
}
