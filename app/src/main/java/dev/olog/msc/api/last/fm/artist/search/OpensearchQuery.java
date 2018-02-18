
package dev.olog.msc.api.last.fm.artist.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpensearchQuery {

    @SerializedName("#text")
    @Expose
    public String text;
    @SerializedName("role")
    @Expose
    public String role;
    @SerializedName("searchTerms")
    @Expose
    public String searchTerms;
    @SerializedName("startPage")
    @Expose
    public String startPage;

    /**
     * No args constructor for use in serialization
     * 
     */
    public OpensearchQuery() {
    }

    /**
     * 
     * @param text
     * @param searchTerms
     * @param role
     * @param startPage
     */
    public OpensearchQuery(String text, String role, String searchTerms, String startPage) {
        super();
        this.text = text;
        this.role = role;
        this.searchTerms = searchTerms;
        this.startPage = startPage;
    }

    @Override
    public String toString() {
        return "OpensearchQuery{" +
                "text='" + text + '\'' +
                ", role='" + role + '\'' +
                ", searchTerms='" + searchTerms + '\'' +
                ", startPage='" + startPage + '\'' +
                '}';
    }
}
