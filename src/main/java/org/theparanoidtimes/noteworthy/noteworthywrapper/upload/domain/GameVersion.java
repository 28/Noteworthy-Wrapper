package org.theparanoidtimes.noteworthy.noteworthywrapper.upload.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "gameVersionTypeId", "name", "slug", "apiVersion"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameVersion {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("gameVersionTypeID")
    private Long gameVersionTypeId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("apiVersion")
    private String apiVersion;

    @Override
    public String toString() {
        return "GameVersion{" +
                "id=" + id +
                ", gameVersionTypeId=" + gameVersionTypeId +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameVersionTypeId() {
        return gameVersionTypeId;
    }

    public void setGameVersionTypeId(Long gameVersionTypeId) {
        this.gameVersionTypeId = gameVersionTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
