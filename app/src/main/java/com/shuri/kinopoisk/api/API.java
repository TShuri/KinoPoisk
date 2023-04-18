package com.shuri.kinopoisk.api;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public final class API extends AsyncTask <Void, Void, BufferedReader>{
    private static String apiKey = "88916739-94f0-46b3-bdac-b96201304527";

    private static String urlReleases = "https://kinopoiskapiunofficial.tech/api/v2.1/films/releases?year=2023&month=MARCH&page=1";

    private static URL url = null;
    private static HttpsURLConnection connection = null;


    private static BufferedReader bfR = null;
    private static InputStreamReader isR = null;

    private static void readResponse() {
        try { // Reading response
            isR = new InputStreamReader(connection.getInputStream());
            bfR = new BufferedReader(isR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedReader getRelease() {
        try { // HTTPS connection
            url = new URL(urlReleases);

            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("X-API-KEY", apiKey);
            connection.addRequestProperty("Content-Type", "application/json");

            connection.connect();
            System.out.println(connection.getResponseCode());
            readResponse();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            connection.disconnect();
        }

        return bfR;
    }


    @Override
    protected BufferedReader doInBackground(Void... voids) {
        try { // HTTPS connection
            url = new URL(urlReleases);

            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("X-API-KEY", apiKey);
            connection.addRequestProperty("Content-Type", "application/json");

            connection.connect();
            System.out.println(connection.getResponseCode());
            readResponse();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            connection.disconnect();
        }

        return bfR;
    }

    @Override
    protected void onPostExecute(BufferedReader bufferedReader) {

    }
}
