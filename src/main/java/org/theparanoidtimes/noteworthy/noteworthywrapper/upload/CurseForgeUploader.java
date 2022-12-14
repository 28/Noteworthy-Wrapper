package org.theparanoidtimes.noteworthy.noteworthywrapper.upload;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.*;
import org.theparanoidtimes.noteworthy.noteworthywrapper.upload.domain.GameVersion;
import org.theparanoidtimes.noteworthy.noteworthywrapper.upload.domain.UploadRequest;
import org.theparanoidtimes.noteworthy.noteworthywrapper.upload.domain.UploadResponse;

import java.nio.file.Path;
import java.util.List;

import static org.theparanoidtimes.noteworthy.noteworthywrapper.Constants.*;
import static org.theparanoidtimes.noteworthy.noteworthywrapper.upload.ObjectMapperProvider.getObjectMapper;

public abstract class CurseForgeUploader {

    private static OkHttpClient client;
    private static List<GameVersion> gameVersions;

    public static Long uploadReleaseToCurseForge(Path pathToRelease, List<String> gameVersions, UploadRequest uploadRequest, boolean verboseOutput) throws Exception {
        var gameVersionIds = gameVersions.stream().map(CurseForgeUploader::getGameVersionId).toList();
        System.out.printf("Found game version ID: '%s'.%n", gameVersionIds);
        uploadRequest.setGameVersions(gameVersionIds);

        String uploadRequestJson = getObjectMapper().writeValueAsString(uploadRequest);
        if (verboseOutput)
            System.out.println("Constructed upload request JSON: " + uploadRequestJson);

        var requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("metadata", uploadRequestJson)
                .addFormDataPart("file", pathToRelease.getFileName().toString(), RequestBody.create(MediaType.parse("application/zip"), pathToRelease.toFile()))
                .build();

        var request = new Request.Builder()
                .url(CF_BASE_URL + CF_UPLOAD_URL.formatted(CF_PROJECT_ID))
                .header(CF_TOKEN_HEADER, CF_API_TOKEN)
                .post(requestBody)
                .build();

        System.out.println("Attempting to upload release to CurseForge...");
        try (var response = getClient().newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new Exception("Request unsuccessful: " + response);
            if (verboseOutput)
                System.out.println("CurseForge response: " + response);

            var body = response.body();
            if (body != null) {
                var uploadResponse = getObjectMapper().readValue(body.string(), UploadResponse.class);
                return uploadResponse.getFileId();
            } else throw new IllegalStateException("Request was successful, but body was empty!");
        }
    }

    private static Long getGameVersionId(String gameVersion) {
        try {
            if (gameVersions == null) {
                var request = new Request.Builder()
                        .url(CF_BASE_URL + CF_VERSION_URL)
                        .header(CF_TOKEN_HEADER, CF_API_TOKEN)
                        .build();

                System.out.printf("Requesting WoW game versions with release version '%s'.%n", gameVersion);
                try (var response = getClient().newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new Exception("Request unsuccessful: " + response);
                    var body = response.body();
                    if (body != null) {
                        gameVersions = getObjectMapper().readValue(body.string(), new TypeReference<>() {});
                    } else throw new IllegalStateException("Request was successful, but body was empty!");
                }
            }
            return gameVersions.stream()
                    .filter(gv -> gv.getName().equals(gameVersion))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("CurseForge does not recognize WoW version: '%s'!", gameVersion)))
                    .getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static OkHttpClient getClient() {
        if (client == null)
            client = new OkHttpClient();
        return client;
    }
}
