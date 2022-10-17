package org.theparanoidtimes.noteworthy.noteworthywrapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

class SourcePackagingTest {

    private static Path testSourcePath;
    private static Path testPackageDestinationPath;

    @BeforeAll
    static void prepare() throws Exception {
        testSourcePath = Path.of(Objects.requireNonNull(SourcePackagingTest.class.getResource("/source")).toURI());
        testPackageDestinationPath = Path.of(Objects.requireNonNull(SourcePackagingTest.class.getResource("/source")).toURI());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @BeforeEach
    void cleanSourceDirectory() throws Exception {
        try (var fileStream = Files.walk(testSourcePath)) {
            fileStream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .filter(file -> !file.isDirectory())
                    .filter(file -> file.getName().matches(".*\\.zip"))
                    .forEach(File::delete);
        }
    }

    @Test
    void wrapperWillPackageAllSourceFilesInZipWithTimestamp() throws Exception {
        var currentTimeMillis = String.valueOf(System.currentTimeMillis());
        SourcePackaging.zipSourceDirectory(testSourcePath, testPackageDestinationPath, currentTimeMillis);

        var finalName = testPackageDestinationPath.resolve("NoteworthyII_" + currentTimeMillis + ".zip").toString();
        var finalPath = Path.of(finalName);
        assertThat(finalPath).exists();
        assertThat(finalPath.toFile()).isNotEmpty();

        try (var zipFile = new ZipFile(finalName)) {
            var zippedFilesSet = zipFile.stream().map(ZipEntry::getName).collect(Collectors.toSet());
            assertThat(zippedFilesSet).contains("NoteworthyII/test.lua");
            assertThat(zippedFilesSet).contains("NoteworthyII/test.xml");
            assertThat(zippedFilesSet).contains("NoteworthyII/lib/testlib.lua");
            assertThat(zippedFilesSet).contains("NoteworthyII/NoteworthyII.toc");
            assertThat(zippedFilesSet).contains("NoteworthyII/NoteworthyII-Vanilla.toc");
            assertThat(zippedFilesSet).contains("NoteworthyII/NoteworthyII-Wrath.toc");
            assertThat(zippedFilesSet).contains("NoteworthyII/README.md");
            assertThat(zippedFilesSet).contains("NoteworthyII/LICENSE.txt");
        }
    }

    @Test
    void wrapperWillPackageAllSourceFilesInZipWithVersionInName() throws Exception {
        var version = "v28.0";
        SourcePackaging.zipSourceDirectory(testSourcePath, testPackageDestinationPath, version);

        var finalName = testPackageDestinationPath.resolve("NoteworthyII_" + version + ".zip").toString();
        var finalPath = Path.of(finalName);
        assertThat(finalPath).exists();
        assertThat(finalPath.toFile()).isNotEmpty();

        try (var zipFile = new ZipFile(finalName)) {
            var zippedFilesSet = zipFile.stream().map(ZipEntry::getName).collect(Collectors.toSet());
            assertThat(zippedFilesSet).contains("NoteworthyII/test.lua");
            assertThat(zippedFilesSet).contains("NoteworthyII/test.xml");
            assertThat(zippedFilesSet).contains("NoteworthyII/lib/testlib.lua");
            assertThat(zippedFilesSet).contains("NoteworthyII/NoteworthyII.toc");
            assertThat(zippedFilesSet).contains("NoteworthyII/NoteworthyII-Vanilla.toc");
            assertThat(zippedFilesSet).contains("NoteworthyII/NoteworthyII-Wrath.toc");
            assertThat(zippedFilesSet).contains("NoteworthyII/README.md");
            assertThat(zippedFilesSet).contains("NoteworthyII/LICENSE.txt");
        }
    }
}
