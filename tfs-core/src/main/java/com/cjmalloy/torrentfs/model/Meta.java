package com.cjmalloy.torrentfs.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cjmalloy.torrentfs.extension.Extension;
import com.cjmalloy.torrentfs.extension.Extension.ExtensionFactory;
import com.cjmalloy.torrentfs.util.JsonUtil;
import com.cjmalloy.torrentfs.util.JsonUtil.HasJson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.turn.ttorrent.common.Torrent;


public class Meta implements HasJson
{
    private static JsonParser JSON_PARSER = new JsonParser();

    /**
     * Optional author string. Possibly used by future extensions.
     */
    public String author;

    /**
     * The URL/URI this torrent was downloaded from. Used for checking new versions.
     */
    public String origin;

    /**
     * Download a new torrent from <code>origin</code> for each request.
     */
    public boolean doNotCache;

    /**
     * Marks this torrent as an advertisement. Distributing ads that are not marked
     * may result in banning.
     */
    public boolean solicitation;

    /**
     * Mark this as adult content. Distributing adult content that is not marked
     * may result in banning.
     */
    public boolean adult;


    public List<Nested> nested;
    public Map<String, Extension> extensions;

    public List<Torrent> getNestedTorrents() throws IOException
    {
        List<Torrent> ret = new ArrayList<Torrent>();
        for (Nested n : nested)
        {
            ret.add(n.getTorrent());
        }
        return ret;
    }

    @Override
    public Meta parseJson(JsonObject json) throws IllegalStateException
    {
        if (json.has("author"))
        {
            author = json.get("author").getAsString();
        }
        if (json.has("origin"))
        {
            origin = json.get("origin").getAsString();
        }
        if (json.has("solicitation"))
        {
            solicitation = json.get("solicitation").getAsBoolean();
        }
        if (json.has("adult"))
        {
            adult = json.get("adult").getAsBoolean();
        }
        if (json.has("doNotCache"))
        {
            doNotCache = json.get("doNotCache").getAsBoolean();
        }
        if (json.has("nested"))
        {
            nested = JsonUtil.parseList(json.get("nested").getAsJsonArray(), Nested.FACTORY);
        }
        if (json.has("extensions"))
        {
            extensions = ExtensionFactory.load(json, JsonUtil.parseStringList(json.get("extensions").getAsJsonArray()));
        }
        return this;
    }

    public static Meta load(File metaFile) throws IOException, IllegalStateException
    {
        JsonElement json = JSON_PARSER.parse(new FileReader(metaFile));
        return new Meta().parseJson(json.getAsJsonObject());
    }
}