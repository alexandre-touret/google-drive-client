package info.touret.googledriveclient;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static info.touret.googledriveclient.GoogleOAuthHelper.getGoogleCredential;

;

/**
 * Hello world!
 */
public class App {
    private final static String ACCESS_TOKEN = "ya29.ewEyfPUP13Oljkew-XU023xvSZDinH_W4AcfFzc3DAk6O81g7meyL25CZZsAOAC0FaQHonSjnif-HA";
    public static final String GOOGLE_DRIVE_FOLDER = "GoogleDrive";
    public static final String FOLDER_DIRECTORY = "f";

    private static Options createOptions() {
        Options options = new Options();
        options.addOption("a",false,"authorization");
        options.addOption("f",true,"folder");
//        options.addOption(OptionBuilder.withwithArgName("a").hasArg().withDescription("authorization").create("authorization"));
//        options.addOption(OptionBuilder.withLongOpt()withArgName("FOLDER").hasArg().isRequired().withDescription("Google Drive Local Folder").create("folder"));
        return options;
    }


    public static void main(String[] args) {
        Options options = createOptions();

        CommandLineParser commandLineParser = new BasicParser();

        GoogleDriveHelper googleDriveHelper = new GoogleDriveHelper();
        try {
            final CommandLine commandLine = commandLineParser.parse(options, args);
            Path gdriveFolder = Paths.get(commandLine.getOptionValue(FOLDER_DIRECTORY), GOOGLE_DRIVE_FOLDER);
            if(!Files.exists(gdriveFolder)){
                System.err.println("Le repertoire "+gdriveFolder.toString()+" existe");
                System.exit(-1);
            }

            // proxy
            ProxyHelper proxyHelper = new ProxyHelper(true, true, "8080", "pcc37cti1", "userappli1", "abcd.1234");
            proxyHelper.setUpProxy();
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            // gestion du token
            GoogleCredential credential = null;
            if (commandLine.hasOption("a")) {
                credential = getGoogleCredential(httpTransport, jsonFactory);
            } else {
                credential = new GoogleCredential().setAccessToken(ACCESS_TOKEN);
            }
            // sync
            Drive service = googleDriveHelper.buildDrive(httpTransport,jsonFactory,credential);
            GoogleDriveClient googleDriveClient = new GoogleDriveClient();
            googleDriveClient.synchronize(service, gdriveFolder);



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
