package net.md_5.bungee.log;

import java.util.logging.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class LoggerShutdownTest {
    @TempDir Path directory;
    @Test void drainsQueueBeforeClosingAndIgnoresLateMessages() {
        BungeeLogger logger = new BungeeLogger("shutdown-test", directory.resolve("proxy.log").toString(), null);
        for (Handler handler : logger.getHandlers()) { logger.removeHandler(handler); handler.close(); }
        int[] count = {0};
        boolean[] closed = {false};
        logger.addHandler(new Handler() {
            public void publish(LogRecord record) { assertFalse(closed[0]); count[0]++; }
            public void flush() {}
            public void close() { closed[0] = true; }
        });
        for (int i = 0; i < 1000; i++) logger.info("queued");
        logger.shutdown();
        logger.info("late");
        logger.shutdown();
        assertEquals(1000, count[0]);
        assertTrue(closed[0]);
        assertEquals(0, logger.getHandlers().length);
    }
}
