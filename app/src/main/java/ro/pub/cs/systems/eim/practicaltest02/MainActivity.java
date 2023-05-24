package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private EditText serverEditText;
    private EditText clientAddressText;
    private EditText clientPortText;
    private EditText pokemonNameText;
    private Button connectBtn;
    private Button getPokemonBtn;
    private ServerThread serverThread;
    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private TextView pokemonAbilitiesTextView;
    private TextView pokemonTypesTextView;

    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            // Retrieves the server port. Checks if it is empty or not
            // Creates a new server thread with the port and starts it
            String serverPort = serverEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private final GetPokemonInfoListener getPokemonInfoListener = new GetPokemonInfoListener();

    private class GetPokemonInfoListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {

            // Retrieves the client address and port. Checks if they are empty or not
            //  Checks if the server thread is alive. Then creates a new client thread with the address, port, city and information type
            //  and starts it
            String clientAddress = clientAddressText.getText().toString();
            String clientPort = clientPortText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = pokemonNameText.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (name) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            pokemonAbilitiesTextView.setText(Constants.EMPTY_STRING);

            ClientThread clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), name, pokemonAbilitiesTextView, pokemonTypesTextView);
            clientThread.start();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test_02_main);

        serverEditText = findViewById(R.id.server_port_edit_text);
        Button connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        clientAddressText = findViewById(R.id.client_address_edit_text);
        clientPortText = findViewById(R.id.client_port_edit_text);
        pokemonNameText = findViewById(R.id.pokemon);
        Button getPokemonButton = findViewById(R.id.get_pokemon_button);
        getPokemonButton.setOnClickListener(getPokemonInfoListener);
        pokemonAbilitiesTextView = findViewById(R.id.pokemon_abilities);
        pokemonTypesTextView = findViewById(R.id.pokemon_types);
    }
}