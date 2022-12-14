import org.theparanoidtimes.noteworthy.noteworthywrapper.ChangelogDeltaGenerator;
import org.theparanoidtimes.noteworthy.noteworthywrapper.upload.CurseForgeUploader;
import org.theparanoidtimes.noteworthy.noteworthywrapper.upload.domain.ReleaseType;
import org.theparanoidtimes.noteworthy.noteworthywrapper.upload.domain.UploadRequest;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.theparanoidtimes.noteworthy.noteworthywrapper.Constants.CHANGELOG_PATH;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@Command(name = "upload",
        description = "Uploads a Noteworthy II package and its associated metadata to CurseForge as a release.",
        version = "Version 2.1 Noteworthy Wrapper",
        mixinStandardHelpOptions = true)
public class upload implements Callable<Integer> {

    @Option(names = {"-p", "--pathToPackage"},
            description = "Path to the release package.",
            required = true)
    private String pathToPackage;

    @Option(names = {"-rv", "--releaseVersion"},
            description = "Release version of the Noteworthy II package.",
            required = true)
    private String releaseVersion;

    @Option(names = {"-gv", "--gameVersions"},
            description = "Coma-separated list of supported WoW versions. For example <retail>,<wrath>,<vanilla>.",
            required = true)
    private String gameVersions;

    @Option(names = {"-v", "--verbose"},
            description = "Print verbose output of the upload process.")
    private boolean verboseOutput = false;

    @Option(names = {"-rt", "--releaseType"},
            description = "Defines the CurseForge release type. Can be 'alpha', 'beta' and 'release', case sensitive, default is 'release'.")
    private String releaseType = ReleaseType.RELEASE.getId();

    @Override
    public Integer call() {
        try {
            var packagePath = Path.of(pathToPackage);
            if (!packagePath.toFile().exists()) {
                throw new IllegalArgumentException(String.format("Specified release package '%s' does not exist!", pathToPackage));
            }

            var releaseType = ReleaseType.fromId(this.releaseType);
            ReleaseType rt;
            if (releaseType.isPresent()) {
                rt = releaseType.get();
            } else
                throw new IllegalArgumentException(String.format("Incorrect release type (-rt) value: '%s'!", this.releaseType));

            verbosePrint("Extracting changelog delta...");
            var changelogDelta = ChangelogDeltaGenerator.generateChangelogDelta(Path.of(CHANGELOG_PATH), releaseVersion);
            verbosePrint(String.format("Extracted changelog delta:%n%s%n", changelogDelta));

            var uploadRequest = new UploadRequest();
            uploadRequest.setChangelog(changelogDelta);
            uploadRequest.setDisplayName(releaseVersion);
            uploadRequest.setReleaseType(rt);
            verbosePrint("Constructed upload request: " + uploadRequest);

            var gameVersionList = Arrays.stream(gameVersions.split(",")).toList();
            verbosePrint("Detected supported WoW versions: " + gameVersionList);

            var fileId = CurseForgeUploader.uploadReleaseToCurseForge(packagePath, gameVersionList, uploadRequest, verboseOutput);
            System.out.println("Release uploaded to CurseForge, file ID: " + fileId);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -28;
        }
    }

    public static void main(String[] args) {
        var resultCode = new CommandLine(new upload()).execute(args);
        System.exit(resultCode);
    }

    private void verbosePrint(String message) {
        if (verboseOutput)
            System.out.println(message);
    }
}
