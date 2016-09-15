import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static Logger logger = LogManager.getLogger(Main.class);

    public static final String CMD_DOCUMENT_ROOT    = "d";
    public static final String CMD_THREADS_PER_CORE = "t";
    public static final String CMD_LISTEN_PORT      = "p";

    public static final int    DEFAULT_LISTEN_PORT = 80;
    public static final String DEFAULT_DOCUMENT_ROOT    = "/var/www";
    public static final int    DEFAULT_THREADS_PER_CORE = 25;


    public Main(String[] args) {
        // Set to defaults
        int port            = Main.DEFAULT_LISTEN_PORT;
        int threadsPerCore  = Main.DEFAULT_THREADS_PER_CORE;
        String documentRoot = Main.DEFAULT_DOCUMENT_ROOT;

        // Process command line input
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(getCliOptions(), args);

            if (cmd.hasOption(Main.CMD_THREADS_PER_CORE)) {
                try {
                    threadsPerCore = Integer.parseInt(cmd.getOptionValue(Main.CMD_THREADS_PER_CORE));
                } catch (Exception e) {
                    // Silently ignore and use default.
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Start server
        logger.trace("Starting server on port " + port
                + ", serving " + documentRoot
                + " with " + threadsPerCore + " threads per core"
        );
        //new SimpleWebserver(port, documentRoot, threadsPerCore).acceptConnections();
    }


    private Options getCliOptions() {
        Options options = new Options();
        options.addOption(Main.CMD_THREADS_PER_CORE, true, "Number of threads per core (default: " + DEFAULT_THREADS_PER_CORE + ")");
        options.addOption(Main.CMD_DOCUMENT_ROOT, true, "This webserver's document root (default: " + DEFAULT_DOCUMENT_ROOT + ")");
        options.addOption(Main.CMD_LISTEN_PORT, true, "Port that this server is listening for connections on (default: " + DEFAULT_LISTEN_PORT + ")");

        return options;
    }


    public static void main(String[] args) {
        new Main(args);
    }
}
