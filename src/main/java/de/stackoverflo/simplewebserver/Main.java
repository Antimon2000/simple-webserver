package de.stackoverflo.simplewebserver;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static Logger logger = LogManager.getLogger(Main.class);

    public static final String OPTION_DOCUMENT_ROOT     = "d";
    public static final String OPTION_LISTEN_PORT       = "p";
    public static final String OPTION_THREADS_PER_CORE  = "t";

    public static final int    DEFAULT_LISTEN_PORT      = 8080;
    public static final int    DEFAULT_THREADS_PER_CORE = 25;
    public static final String DEFAULT_DOCUMENT_ROOT    = "/var/www";


    public Main(String[] args) {
        // Process command line input
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(createOptions(), args);

            int listenPort      = readListenPort(cmd);
            int threadsPerCore  = readThreadsPerCore(cmd);
            String documentRoot = readDocumentRoot(cmd);

            // Start server
            logger.info("Starting server on port " + listenPort
                    + ", serving " + documentRoot
                    + " with max. " + threadsPerCore + " threads per core"
            );
            new SimpleWebserver(listenPort, documentRoot, threadsPerCore).startServer();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private String readDocumentRoot(CommandLine cmd) {
        return cmd.getOptionValue(Main.OPTION_DOCUMENT_ROOT, Main.DEFAULT_DOCUMENT_ROOT);
    }


    private int readListenPort(CommandLine cmd) {
        return readIntegerOption(cmd, Main.OPTION_LISTEN_PORT, Main.DEFAULT_LISTEN_PORT);
    }


    private int readThreadsPerCore(CommandLine cmd) {
        return readIntegerOption(cmd, Main.OPTION_THREADS_PER_CORE, Main.DEFAULT_THREADS_PER_CORE);
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
            Main.OPTION_DOCUMENT_ROOT,
            true,
            "This webserver's document root (default: " + DEFAULT_DOCUMENT_ROOT + ")"
        );

        options.addOption(
            Main.OPTION_LISTEN_PORT,
            true,
            "Port that this server is listening for connections on (default: " + DEFAULT_LISTEN_PORT + ")"
        );

        options.addOption(
            Main.OPTION_THREADS_PER_CORE,
            true,
            "Maximum number of threads per core (default: " + DEFAULT_THREADS_PER_CORE + ")"
        );

        return options;
    }


    public static void main(String[] args) {
        new Main(args);
    }
}
