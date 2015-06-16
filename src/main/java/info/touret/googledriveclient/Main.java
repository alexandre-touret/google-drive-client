package info.touret.googledriveclient;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import info.touret.googledriveclient.proxy.ProxyHelper;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 * Main Application
 */
public class Main {
    public static final String GOOGLE_DRIVE_FOLDER = "GoogleDrive";
    public static final String FOLDER_DIRECTORY = "f";
    private final static String ACCESS_TOKEN = "ya29.ewEyfPUP13Oljkew-XU023xvSZDinH_W4AcfFzc3DAk6O81g7meyL25CZZsAOAC0FaQHonSjnif-HA";
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Creates options
     * @return Command line options
     */
    private static Options createOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg(false).withLongOpt("authorize").withDescription("Google Authorization").create('a'));
        options.addOption(OptionBuilder.hasArg(false).withLongOpt("use-proxy").withDescription("Use Proxy").create('p'));
        options.addOption(OptionBuilder.hasArg(true).withArgName("localFolder").isRequired().withLongOpt("local-folder").withDescription("Google Drive Local Folder").create('f'));
        options.addOption(new Option("help", "Print this message"));
        options.addOption(OptionBuilder.withArgName("host").hasArg(true).withDescription("Proxy Host").create("proxy_host"));
        options.addOption(OptionBuilder.withArgName("port").hasArg(true).withDescription("Proxy Port").create("proxy_port"));
        options.addOption(OptionBuilder.withArgName("user").hasArg(true).withDescription("Proxy User").create("proxy_user"));
        options.addOption(OptionBuilder.withArgName("password").hasArg(true).withDescription("Proxy password").create("proxy_password"));
        return options;
    }

    public static void main(String[] args) {
        init();
        Options options = createOptions();
        CommandLineParser commandLineParser = new BasicParser();
        GoogleDriveHelper googleDriveHelper = new GoogleDriveHelper();
        try {
            final CommandLine commandLine = commandLineParser.parse(options, args);
            Path gdriveFolder = Paths.get(commandLine.getOptionValue(FOLDER_DIRECTORY), GOOGLE_DRIVE_FOLDER);

            checkGoogleDriveFolder(commandLine, gdriveFolder);
            checkAndConfigureProxy(commandLine);

            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            // gestion du token
            GoogleCredential credential = null;
            GoogleOAuthHelper googleOAuthHelper = new GoogleOAuthHelper(gdriveFolder);
            if (commandLine.hasOption("a")) {
                credential = googleOAuthHelper.getGoogleCredential(httpTransport, jsonFactory);
                googleOAuthHelper.storeCredentialInConfigFile(credential.getAccessToken());
            } else {
                Optional<String> token = googleOAuthHelper.getAccessToken();
                if (!token.isPresent()) {
                    LOGGER.severe("Unable to load Google Credentials");
                    System.exit(-1);
                }
                credential = new GoogleCredential().setAccessToken(token.get());
            }
            // sync
            Drive service = googleDriveHelper.buildDrive(httpTransport, jsonFactory, credential);
            GoogleDriveClient googleDriveClient = new GoogleDriveClient();
            googleDriveClient.synchronize(service, gdriveFolder);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GoogleDriveException(e);
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", options);
            System.exit(-2);
        }

    }

    /**
     * Checks and configure proxy from arguments passed in the command line
     *
     * @param commandLine
     */
    private static void checkAndConfigureProxy(CommandLine commandLine) {
        if (commandLine.hasOption("p")) {
            // proxy
            final String proxyHost = commandLine.getOptionValue("proxy_host");
            final String proxyUser = commandLine.getOptionValue("proxy_user");
            final String proxyPort = commandLine.getOptionValue("proxy_port");
            LOGGER.fine("Proxy Configuration  [" + proxyUser + ":*****@" + proxyHost + ":" + proxyPort + "]");
            Boolean useHTPPAuth = proxyUser != null || !proxyUser.isEmpty();
            ProxyHelper proxyHelper = new ProxyHelper(true, useHTPPAuth, proxyPort,
                    proxyHost,
                    proxyUser,
                    commandLine.getOptionValue("proxy_password"));
            proxyHelper.setUpProxy();
        }
    }

    /**
     * Checks the folder passed in the main method
     * Beware, this method could do a System.exit() if the folder doesn't exist
     *
     * @param commandLine
     * @param gdriveFolder
     * @throws IOException
     */
    private static void checkGoogleDriveFolder(CommandLine commandLine, Path gdriveFolder) throws IOException {
        if (!Files.exists(Paths.get(commandLine.getOptionValue(FOLDER_DIRECTORY)))) {
            LOGGER.warning("This folder [" + commandLine.getOptionValue(FOLDER_DIRECTORY) + "] doesn't exist ");
            System.exit(-1);
        }
        if (!Files.exists(gdriveFolder)) {
            LOGGER.warning("This folder [" + commandLine.getOptionValue(FOLDER_DIRECTORY) + "] doesn't exist or the folder [" + gdriveFolder + "]is already exists");
            LOGGER.warning("Creating folder [" + gdriveFolder + "]");
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-x---");
            Files.createDirectory(gdriveFolder);
            gdriveFolder.toFile().setWritable(true);
        }
    }

    /**
     * Initialize LogManager
     */
    private static void init() {
        try (InputStream input = Main.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(input);
        } catch (IOException e) {
            // Ne devrait pas arriver !!
            e.printStackTrace();
        }
    }
}
