package org.theparanoidtimes.noteworthy.noteworthywrapper.upload;

import org.junit.jupiter.api.Test;
import org.theparanoidtimes.noteworthy.noteworthywrapper.upload.domain.UploadResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.theparanoidtimes.noteworthy.noteworthywrapper.upload.ObjectMapperProvider.getObjectMapper;

class UploadResponseTest {

    @Test
    void uploadResponseWillCorrectlyBeParsed() throws Exception {
        var uploadResponseJsonString = """
                {
                    "id": 20402
                }
                """;
        var uploadResponse = getObjectMapper().readValue(uploadResponseJsonString, UploadResponse.class);

        assertThat(uploadResponse.getFileId()).isEqualTo(20402L);
    }
}
