package org.theparanoidtimes.noteworthy.noteworthywrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

import static org.theparanoidtimes.noteworthy.noteworthywrapper.Constants.*;

public final class PackageInstallation {

    public static void installLocally(Path sourcePath, Path gameInstallationDirectory) throws Exception {
        var installationDir = gameInstallationDirectory.resolve(INSTALL_LOCATION);
        System.out.println("Installing from: " + sourcePath + " to: " + installationDir);

        var luaSourcePath = sourcePath.resolve(LUA_SOURCE_PATH);
        var xmlSourcePath = sourcePath.resolve(XML_SOURCE_PATH);
        var librariesPath = sourcePath.resolve(LIBRARIES_PATH);
        var tocPath = sourcePath.resolve(TOC_PATH);
        var tocWrathPath = sourcePath.resolve(TOC_WRATH_PATH);
        var tocVanillaPath = sourcePath.resolve(TOC_VANILLA_PATH);
        cleanInstallationDirectory(installationDir);
        doInstallLocally(luaSourcePath, installationDir);
        doInstallLocally(xmlSourcePath, installationDir);
        doInstallLocally(librariesPath, installationDir, "lib/");
        doInstallLocally(tocPath, installationDir);
        doInstallLocally(tocWrathPath, installationDir);
        doInstallLocally(tocVanillaPath, installationDir);
        System.out.println("Noteworthy II installed to: " + installationDir);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void cleanInstallationDirectory(Path installationDir) throws Exception {
        if (installationDir.toFile().exists()) {
            try (var fileStream = Files.walk(installationDir)) {
                fileStream.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
        Files.createDirectory(installationDir);
    }

    private static void doInstallLocally(Path sourcePath, Path installationDir) throws Exception {
        doInstallLocally(sourcePath, installationDir, "");
    }

    private static void doInstallLocally(Path sourcePath, Path installationDir, String subdirectory) throws Exception {
        Files.walkFileTree(sourcePath, new SimpleFileVisitor<>() {
            @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                var subdirectoryPath = installationDir.resolve(subdirectory);
                if (!installationDir.endsWith(subdirectoryPath) && !subdirectoryPath.toFile().exists()) {
                    Files.createDirectory(subdirectoryPath);
                }

                var installPath = subdirectoryPath.resolve(file.getFileName());
                System.out.println("Installing: " + file + " to " + installPath);
                if (!file.toFile().isDirectory()) {
                    Files.copy(file, installPath);
                }
                return super.visitFile(file, attrs);
            }
        });
    }
}
