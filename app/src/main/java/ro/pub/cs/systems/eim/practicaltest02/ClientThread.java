package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{

    private final String address;
    private final int port;
    private final String pokemon_name;
    private final TextView pokemonAbilitiesTextView;
    private final TextView pokemonTypesTextView;

    private Socket socket;

    public ClientThread(String address, int port, String pokemon_name, TextView pokemonAbilitiesTextView, TextView pokemonTypesTextView) {
        this.address = address;
        this.port = port;
        this.pokemon_name = pokemon_name;
        this.pokemonAbilitiesTextView = pokemonAbilitiesTextView;
        this.pokemonTypesTextView = pokemonTypesTextView;
    }

    @Override
    public void run() {
        try {
            // tries to establish a socket connection to the server
            socket = new Socket(address, port);

            // gets the reader and writer for the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // sends the city and information type to the server
            printWriter.println(pokemon_name);
            printWriter.flush();
            String pokemon_information;

            // reads the weather information from the server
            while ((pokemon_information = bufferedReader.readLine()) != null) {
                final String finalizedWeateherInformation = pokemon_information;

                // updates the UI with the weather information. This is done using postt() method to ensure it is executed on UI thread
                pokemonAbilitiesTextView.post(() -> pokemonAbilitiesTextView.setText(finalizedWeateherInformation));
            }
        } // if an exception occurs, it is logged
        catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
