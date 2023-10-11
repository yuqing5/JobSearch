package com.example.jobsearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true) //indicates other fields can be safely ignored
@JsonInclude(JsonInclude.Include.NON_NULL) //indicates null field can be skipped and not included

public class Item {
    private String id;
    private String title;
    private String location;
    private String companyLogo;
    private String url;
    private String description;

    private Set<String> keywords; //provided by GitHub
    private boolean favorite; //user like and collect job positions


    public Item() {
    }

    public Item(String id, String title, String location, String companyLogo, String url, String description, Set<String> keywords, boolean favorite) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.companyLogo = companyLogo;
        this.url = url;
        this.description = description;
        this.keywords = keywords;
        this.favorite = favorite;
    }


    @JsonProperty("id") //indicates mapping, not necessary if it's same as property name
    public String getId() { //set in stone, won't be changed. so no getter method

        return id;
    }
    @JsonProperty("title")
    public String getTitle() {

        return title;
    }
    @JsonProperty("location")
    public String getLocation() {

        return location;
    }
    @JsonProperty("company_logo") //if we don't add_, then it will be different from our expectation from GitHub
    //necessary for camel case conversions, otherwise it won't appear in the response
    public String getCompanyLogo() {

        return companyLogo;
    }
    @JsonProperty("url")
    public String getUrl() {

        return url;
    }
    @JsonProperty("description")
    public String getDescription() {

        return description;
    }

    public Set<String> getKeywords() {

        return keywords;
    }

    public void setKeywords(Set<String> keywords) { //editable

        this.keywords = keywords;
    }
    public boolean getFavorite() {

        return favorite;
    }

    public void setFavorite(boolean favorite) { //editable

        this.favorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return favorite == item.favorite &&
                Objects.equals(id, item.id) &&
                Objects.equals(title, item.title) &&
                Objects.equals(location, item.location) &&
                Objects.equals(companyLogo, item.companyLogo) &&
                Objects.equals(url, item.url) &&
                Objects.equals(description, item.description) &&
                Objects.equals(keywords, item.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, location, companyLogo, url, description, keywords, favorite);
    }

    @Override
    public String toString() {
        return "item{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", companyLogo='" + companyLogo + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", keywords=" + keywords +
                ", favorite=" + favorite +
                '}';
    }
}


