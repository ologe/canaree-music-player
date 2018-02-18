
package dev.olog.msc.api.last.fm.track.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpensearchQuery {

    @SerializedName("#text")
    @Expose
    public String text;
    @SerializedName("role")
    @Expose
    public String role;
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
     * @param role
     * @param startPage
     */
    public OpensearchQuery(String text, String role, String startPage) {
        super();
        this.text = text;
        this.role = role;
        this.startPage = startPage;
    }

    @Override
    public String toString() {
        return "OpensearchQuery{" +
                "text='" + text + '\'' +
                ", role='" + role + '\'' +
                ", startPage='" + startPage + '\'' +
                '}';
    }
}
