package nl.oradev.piclodio.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.util.concurrent.TimeUnit;


public class VlcPlayer {

    private static final Logger logger = LoggerFactory.getLogger(VlcPlayer.class);

    private String vlcPlayerPath = "/usr/bin/cvlc";
    private Process vlcplayerProcess;
    private BufferedReader vlcplayerOutErr;


    public void open(String url, Long autoStopMinutes) throws IOException, InterruptedException {
        if (vlcplayerProcess == null) {
            // start VlcPlayer as an external process
            String command = vlcPlayerPath + " " + url;
            logger.info("Starting VlcPlayer process:{}", command);
            vlcplayerProcess = Runtime.getRuntime().exec(command);

            PipedInputStream readFrom = new PipedInputStream(1024 * 1024);
            vlcplayerOutErr = new BufferedReader(new InputStreamReader(readFrom));

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
                logger.error("Exception in Wait for answer: {}", e.getMessage());
            }
        }
        return line;
    }
}
