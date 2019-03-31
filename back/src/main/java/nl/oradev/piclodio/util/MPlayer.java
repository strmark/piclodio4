package nl.oradev.piclodio.util;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A player which is actually an interface to the famous MPlayer.
 */
public class MPlayer {

    private static Logger logger = Logger.getLogger(MPlayer.class.getName());

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

    /** The path to the MPlayer executable. */
    private String mplayerPath = "/usr/bin/mplayer";
    /** Options passed to MPlayer. */
    private String mplayerOptions = "-slave -idle";

    /** The process corresponding to MPlayer. */
    private Process mplayerProcess;
    /** The standard input for MPlayer where you can send commands. */
    private PrintStream mplayerIn;
    /** A combined reader for the the standard output and error of MPlayer. Used to read MPlayer responses. */
    private BufferedReader mplayerOutErr;

    public MPlayer() {
    }

    public void open(String url, Long autoStopMinutes) throws IOException, InterruptedException {
        if (mplayerProcess == null) {
            // start MPlayer as an external process
            String command = mplayerPath + " " + mplayerOptions + " " + url;
            logger.info("Starting MPlayer process: " + command);
            mplayerProcess = Runtime.getRuntime().exec(command);

            // create the piped streams where to redirect the standard output and error of MPlayer
            // specify a bigger pipesize
            PipedInputStream  readFrom = new PipedInputStream(1024*1024);
            PipedOutputStream writeTo = new PipedOutputStream(readFrom);
            mplayerOutErr = new BufferedReader(new InputStreamReader(readFrom));

            // create the threads to redirect the standard output and error of MPlayer
            new LineRedirecter(mplayerProcess.getInputStream(), writeTo, "MPlayer says: ").start();
            new LineRedirecter(mplayerProcess.getErrorStream(), writeTo, "MPlayer encountered an error: ").start();

            // the standard input of MPlayer
            mplayerIn = new PrintStream(mplayerProcess.getOutputStream());
            if (autoStopMinutes > 0){
                if(!mplayerProcess.waitFor(autoStopMinutes, TimeUnit.MINUTES)){
                    mplayerProcess.destroy();
                }
            }
        } else {
            execute("loadfile \"" + url + "\" 0");
        }
        // wait to start playing
        waitForAnswer("Starting playback...");
        logger.info("Started playing file " + url);
    }

    public void close() throws  IOException{
        // stop the mplayer
        if (mplayerProcess != null) {
            execute("quit");
            try {
                mplayerProcess.waitFor();
            }
            catch (InterruptedException e) {}
            mplayerProcess = null;
        }
    }

    public void togglePlay() {
        execute("pause");
    }

    public boolean isPlaying() {
        return mplayerProcess != null;
    }

    protected String getProperty(String name) {
        if (name == null || mplayerProcess == null) {
            return null;
        }
        String s = "ANS_" + name + "=";
        String x = execute("get_property " + name, s);
        if (x == null)
            return null;
        if (!x.startsWith(s))
            return null;
        return x.substring(s.length());
    }

    protected long getPropertyAsLong(String name) {
        try {
            return Long.parseLong(getProperty(name));
        }
        catch (NumberFormatException exc) {}
        catch (NullPointerException exc) {}
        return 0;
    }

    protected float getPropertyAsFloat(String name) {
        try {
            return Float.parseFloat(getProperty(name));
        }
        catch (NumberFormatException exc) {}
        catch (NullPointerException exc) {}
        return 0f;
    }

    /** Sends a command to MPlayer..
     * @param command the command to be sent
     */
    private void execute(String command) {
        execute(command, null);
    }

    /** Sends a command to MPlayer and waits for an answer.
     * @param command the command to be sent
     * @param expected the string with which has to start the line; if null don't wait for an answer
     * @return the MPlayer answer
     */
    private String execute(String command, String expected) {
        if (mplayerProcess != null) {
            logger.info("Send to MPlayer the command \"" + command + "\" and expecting "
                    + (expected != null ? "\"" + expected + "\"" : "no answer"));
            mplayerIn.print(command);
            mplayerIn.print("\n");
            mplayerIn.flush();
            logger.info("Command sent");
            if (expected != null) {
                String response = waitForAnswer(expected);
                logger.info("MPlayer command response: " + response);
                return response;
            }
        }
        return null;
    }

    /** Read from the MPlayer standard output and error a line that starts with the given parameter and return it.
     * @param expected the expected starting string for the line
     * @return the entire line from the standard output or error of MPlayer
     */
    private String waitForAnswer(String expected) {
        String line = null;
        if (expected != null) {
            try {
                while ((line = mplayerOutErr.readLine()) != null) {
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
