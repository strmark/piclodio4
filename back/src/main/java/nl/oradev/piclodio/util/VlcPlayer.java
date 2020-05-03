package nl.oradev.piclodio.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

public class VlcPlayer {

    private static final Logger logger = LoggerFactory.getLogger(VlcPlayer.class);
    private String vlcplayerPath = "/usr/bin/cvlc";
    private Process vlcplayerProcess;
    private BufferedReader vlcplayerOutErr;

    public void open(String url, Long autoStopMinutes) throws IOException, InterruptedException {
        if (vlcplayerProcess == null) {
            // start VlcPlayer as an external process
            String command = vlcplayerPath + " " + url;
            logger.info("Starting VlcPlayer process:{}", command);
            vlcplayerProcess = Runtime.getRuntime().exec(command);

            PipedInputStream readFrom = new PipedInputStream(1024 * 1024);
            PipedOutputStream writeTo = new PipedOutputStream(readFrom);
            vlcplayerOutErr = new BufferedReader(new InputStreamReader(readFrom));

            new LineRedirecter(vlcplayerProcess.getInputStream(), writeTo, "VlcPlayer says: ").start();
            new LineRedirecter(vlcplayerProcess.getErrorStream(), writeTo, "VlcPlayer encountered an error: ").start();

            if (autoStopMinutes > 0 &&
                    !vlcplayerProcess.waitFor(autoStopMinutes, TimeUnit.MINUTES)) {
                vlcplayerProcess.destroy();
                vlcplayerProcess = null;
            }
        } else {
            vlcplayerProcess.destroy();
            vlcplayerProcess = null;
            open(url, autoStopMinutes);
        }
        // wait to start playing
        waitForAnswer("Starting playback...");
        logger.info("Started playing file {} ", url);
    }

    public void close() {
        // stop the vlcplayer
        if (vlcplayerProcess != null) {
            vlcplayerProcess.destroy();
            vlcplayerProcess = null;
        }
    }

    public boolean isPlaying() {
        return vlcplayerProcess != null;
    }

    private String waitForAnswer(String expected) {
        String line = null;
        if (expected != null) {
            try {
                while ((line = vlcplayerOutErr.readLine()) != null) {
                    logger.info("Reading line: {}", line);
                    if (line.startsWith(expected)) {
                        return line;
                    }
                }
            } catch (IOException e) {
                logger.error("Exceptio in Wait for answer: {}", e.getMessage());
            }
        }
        return line;
    }

    private static class LineRedirecter extends Thread {
        private InputStream in;
        private OutputStream out;
        private String prefix;

        LineRedirecter(InputStream in, OutputStream out, String prefix) {
            this.in = in;
            this.out = out;
            this.prefix = prefix;
        }

        @Override
        public void run() {
            try {
                // creates the decorating reader and writer
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                PrintStream printStream;
                String line;

                // read line by line
                while ((line = reader.readLine()) != null) {
                    //no logging it overloads the log file
                    //logger.info((prefix != null ? prefix : "") + line);
                    //printStream.println(line);
                }
            } catch (IOException exc) {
                logger.error("An error has occured while grabbing lines {}", exc.getMessage());
            }
        }

    }

}
