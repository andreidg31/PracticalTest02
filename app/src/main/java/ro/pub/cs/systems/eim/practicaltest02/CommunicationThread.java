package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    // Constructor of the thread, which takes a ServerThread and a Socket as parameters
    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    // run() method: The run method is the entry point for the thread when it starts executing.
    // It's responsible for reading data from the client, interacting with the server,
    // and sending a response back to the client.
    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // Create BufferedReader and PrintWriter instances for reading from and writing to the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (pokemon name!");

            String pokemon = bufferedReader.readLine();
            if (pokemon == null || pokemon.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (pokemon name!!");
                return;
            }

            // It checks whether the serverThread has already received the weather forecast information for the given city.
            HashMap<String, PokemonInformation> data = serverThread.getData();
            PokemonInformation pokemonInformation;
            if (data.containsKey(pokemon)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                pokemonInformation = data.get(pokemon);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";

                // make the HTTP request to the web service
                HttpGet httpGet = new HttpGet(Constants.POKEMON_SERVICE_ADDRESS + "/" + pokemon);
                Log.i(Constants.TAG, "UTR IS: " + Constants.POKEMON_SERVICE_ADDRESS + "/" + pokemon);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else Log.i(Constants.TAG, pageSourceCode);
                // Parse the page source code into a JSONObject and extract the needed information
                JSONObject content = new JSONObject(pageSourceCode);
                JSONArray abilitesArray = content.getJSONArray(Constants.ABILITIES);
                Log.i(Constants.TAG, abilitesArray.toString());
                JSONObject ability;
                StringBuilder abilityString = new StringBuilder();
                for (int i = 0; i < abilitesArray.length(); i++) {
                    ability = abilitesArray.getJSONObject(i);
                    abilityString.append(ability.toString());
                    Log.i(Constants.TAG, "Ability is: " + ability.toString());
                    if (i < abilitesArray.length() - 1) {
                        abilityString.append(";");
                    }
                }
                JSONArray typesArray = content.getJSONArray(Constants.TYPES);
                JSONObject type;
                StringBuilder typeString = new StringBuilder();
                for (int i = 0; i < typesArray.length(); i++) {
                    type= typesArray.getJSONObject(i);
                    Log.i(Constants.TAG, "Type is: " + type.toString());
                    typeString.append(type.toString());

                    if (i < typesArray.length() - 1) {
                        typeString.append(";");
                    }
                }

                // Create a WeatherForecastInformation object with the information extracted from the JSONObject
                pokemonInformation = new PokemonInformation(typeString.toString(), abilityString.toString());

                // Cache the information for the given city
                serverThread.setData(pokemon, pokemonInformation);
            }

            if (pokemonInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }

            // Send the information back to the client
            String result = pokemonInformation.toString();

            // Send the result back to the client
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException | JSONException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}