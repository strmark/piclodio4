package nl.oradev.piclodio.util;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A player which is actually an interface to the famous Vlc player.
 */
public class VlcPlayer {

    private static Logger logger = Logger.getLogger(VlcPlayer.class.getName());

    /** A thread that reads from an input stream and outputs to another line by line. */
    private static class LineRedirecter extends Thread {
        /** The input stream to read from. */
        private InputStream in;
        /** The output stream to write to. */
        private OutputStream out;
        /** The prefix used to prefix the lines when outputting to the logger. */
        private String prefix;

        /**
         * @param in the input stream to read from.
         * @param out the output stream to write to.
         * @param prefix the prefix used to prefix the lines when outputting to the logger.
         */
        LineRedirecter(InputStream in, OutputStream out, String prefix) {
            this.in = in;
            this.out = out;
            this.prefix = prefix;
        }

        public void run()
        {
            try {
                // creates the decorating reader and writer
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                PrintStream printStream = new PrintStream(out);
                String line;

                // read line by line
                while ( (line = reader.readLine()) != null) {
                    //no logging it overloads the log file
                    //logger.info((prefix != null ? prefix : "") + line);
                    //printStream.println(line);
                }
            } catch (IOException exc) {
                logger.log(Level.WARNING, "An error has occured while grabbing lines", exc);
            }
        }

    }

    /** The path to the VlcPlayer executable. */
    private String vlcplayerPath = "/usr/bin/cvlc";

    /** The process corresponding to VlcPlayer. */
    private Process vlcplayerProcess;
    /** The standard input for VlcPlayer where you can send commands. */
    private PrintStream vlcplayerIn;
    /** A combined reader for the the standard output and error of VlcPlayer. Used to read VlcPlayer responses. */
    private BufferedReader vlcplayerOutErr;

    public VlcPlayer() {
    }

    public void open(String url, Long autoStopMinutes) throws IOException, InterruptedException {
        if (vlcplayerProcess == null) {
            // start VlcPlayer as an external process
            String command = vlcplayerPath + " " + url;
            logger.info("Starting VlcPlayer process: " + command);
            vlcplayerProcess = Runtime.getRuntime().exec(command);

            // create the piped streams where to redirect the standard output and error of VlcPlayer
            // specify a bigger pipesize
            PipedInputStream  readFrom = new PipedInputStream(1024*1024);
            PipedOutputStream writeTo = new PipedOutputStream(readFrom);
            vlcplayerOutErr = new BufferedReader(new InputStreamReader(readFrom));

            // create the threads to redirect the standard output and error of VlcPlayer
            new LineRedirecter(vlcplayerProcess.getInputStream(), writeTo, "VlcPlayer says: ").start();
            new LineRedirecter(vlcplayerProcess.getErrorStream(), writeTo, "VlcPlayer encountered an error: ").start();

            // the standard input of VlcPlayer
            vlcplayerIn = new PrintStream(vlcplayerProcess.getOutputStream());
            if (autoStopMinutes > 0){
                if(!vlcplayerProcess.waitFor(autoStopMinutes, TimeUnit.MINUTES)){
                    vlcplayerProcess.destroy();
                    vlcplayerProcess=null;
                }
            }
        } else {
            vlcplayerProcess.destroy();
            vlcplayerProcess=null;
            open(url, autoStopMinutes); 
        }
        // wait to start playing
        waitForAnswer("Starting playback...");
        logger.info("Started playing file " + url);
    }

    public void close() throws IOException{
        // stop the vlcplayer
        if (vlcplayerProcess != null) {
           vlcplayerProcess.destroy();
           vlcplayerProcess = null;
        }
    }

    public boolean isPlaying() {
        return vlcplayerProcess != null;
    }

    /** Read from the VlcPlayer standard output and error a line that starts with the given parameter and return it.
     * @param expected the expected starting string for the line
     * @return the entire line from the standard output or error of VlcPlayer
     */
    private String waitForAnswer(String expected) {
        String line = null;
        if (expected != null) {
            try {
                while ((line = vlcplayerOutErr.readLine()) != null) {
                    logger.info("Reading line: " + line);
                    if (line.startsWith(expected)) {
                        return line;
                    }
                }
            }
            catch (IOException e) {
            }
        }
        return line;
    }

}
