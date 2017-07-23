package com.example.aviv.wikirandom.Model;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Team Hurrange on 3/22/2017.
 */

// An AsyncTask class that responsible for making a json object from a url
public class FetchArticleTask extends AsyncTask<String, Void, JSONObject>
{
    private Context context;
    private JSONObject randomJSON;
    private static String randomArticleURL;

    public FetchArticleTask(Context context, String randomArticleURL)
    {
        this.context = context;
        this.randomArticleURL = randomArticleURL;
    }

    private static String readAll(Reader rd) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1)
        {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    // Get JSON Object from URL
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException
    {
        InputStream is = new URL(url).openStream();
        try
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        }
        finally
        {
            is.close();
        }
    }

    @Override
    protected JSONObject doInBackground(String... params)
    {
        try
        {

            randomJSON = readJsonFromUrl(randomArticleURL);
            return randomJSON;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}