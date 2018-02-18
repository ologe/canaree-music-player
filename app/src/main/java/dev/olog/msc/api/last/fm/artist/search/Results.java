
package dev.olog.msc.api.last.fm.artist.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Results {

    @SerializedName("opensearch:Query")
    @Expose
    public OpensearchQuery opensearchQuery;
    @SerializedName("opensearch:totalResults")
    @Expose
    public String opensearchTotalResults;
    @SerializedName("opensearch:startIndex")
    @Expose
    public String opensearchStartIndex;
    @SerializedName("opensearch:itemsPerPage")
    @Expose
    public String opensearchItemsPerPage;
    @SerializedName("artistmatches")
    @Expose
    public Artistmatches artistmatches;
    @SerializedName("@attr")
    @Expose
    public Attr attr;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Results() {
    }

    /**
     * 
     * @param opensearchItemsPerPage
     * @param opensearchQuery
     * @param artistmatches
     * @param opensearchStartIndex
     * @param attr
     * @param opensearchTotalResults
     */
    public Results(OpensearchQuery opensearchQuery, String opensearchTotalResults, String opensearchStartIndex, String opensearchItemsPerPage, Artistmatches artistmatches, Attr attr) {
        super();
        this.opensearchQuery = opensearchQuery;
        this.opensearchTotalResults = opensearchTotalResults;
        this.opensearchStartIndex = opensearchStartIndex;
        this.opensearchItemsPerPage = opensearchItemsPerPage;
        this.artistmatches = artistmatches;
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "Results{" +
                "opensearchQuery=" + opensearchQuery +
                ", opensearchTotalResults='" + opensearchTotalResults + '\'' +
                ", opensearchStartIndex='" + opensearchStartIndex + '\'' +
                ", opensearchItemsPerPage='" + opensearchItemsPerPage + '\'' +
                ", artistmatches=" + artistmatches +
                ", attr=" + attr +
                '}';
    }
}
