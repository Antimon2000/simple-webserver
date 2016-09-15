import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static Logger logger = LogManager.getLogger(Main.class);

    public static final String OPTION_DOCUMENT_ROOT     = "d";
    public static final String OPTION_THREADS_PER_CORE  = "t";
    public static final String OPTION_LISTEN_PORT       = "p";

    public static final int    DEFAULT_LISTEN_PORT      = 80;
    public static final String DEFAULT_DOCUMENT_ROOT    = "/var/www";
    public static final int    DEFAULT_THREADS_PER_CORE = 25;


    public Main(String[] args) {
        // Process command line input
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(createOptions(), args);

            int listenPort      = readListenPort(cmd, Main.DEFAULT_LISTEN_PORT);
            int threadsPerCore  = readThreadsPerCore(cmd, Main.DEFAULT_THREADS_PER_CORE);
            String documentRoot = readDocumentRoot(cmd, Main.DEFAULT_DOCUMENT_ROOT);

            // Start server
            logger.trace("Starting server on port " + listenPort
                    + ", serving " + documentRoot
                    + " with "     + threadsPerCore + " threads per core"
            );
            //new SimpleWebserver(port, documentRoot, threadsPerCore).acceptConnections();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private String readDocumentRoot(CommandLine cmd, String defaultValue) {
        return cmd.getOptionValue(Main.OPTION_DOCUMENT_ROOT, defaultValue);
    }


    private int readListenPort(CommandLine cmd, int defaultValue) {
        return readIntegerOption(cmd, Main.OPTION_LISTEN_PORT, defaultValue);
    }


    private int readThreadsPerCore(CommandLine cmd, int defaultValue) {
        return readIntegerOption(cmd, Main.OPTION_THREADS_PER_CORE, defaultValue);
    }


    private int readIntegerOption(CommandLine cmd, String optionShort, int defaultValue) {
        int intOption = defaultValue;

        if (cmd.hasOption(optionShort)) {
            try {
                intOption = Integer.parseInt(cmd.getOptionValue(optionShort));
            } catch (Exception e) {
                // Silently ignore and use default.
            }
        }

        return intOption;
    }


    private Options createOptions() {
        Options options = new Options();

        options.addOption(
            Main.OPTION_THREADS_PER_CORE,
            true,
            "Number of threads per core (default: " + DEFAULT_THREADS_PER_CORE + ")"
        );

        options.addOption(
            Main.OPTION_DOCUMENT_ROOT,
            true,
            "This webserver's document root (default: " + DEFAULT_DOCUMENT_ROOT + ")"
        );

        options.addOption(
            Main.OPTION_LISTEN_PORT,
            true,
            "Port that this server is listening for connections on (default: " + DEFAULT_LISTEN_PORT + ")"
        );

        return options;
    }


    public static void main(String[] args) {
        new Main(args);
    }
}
