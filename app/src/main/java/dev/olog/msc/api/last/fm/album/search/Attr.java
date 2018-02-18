
package dev.olog.msc.api.last.fm.album.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attr {

    @SerializedName("for")
    @Expose
    public String _for;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Attr() {
    }

    /**
     * 
     * @param _for
     */
    public Attr(String _for) {
        super();
        this._for = _for;
    }

    @Override
    public String toString() {
        return "Attr{" +
                "_for='" + _for + '\'' +
                '}';
    }
}
