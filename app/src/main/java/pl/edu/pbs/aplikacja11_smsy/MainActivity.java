package pl.edu.pbs.aplikacja11_smsy;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private TextView messageTextView;
    private EditText numer;
    private EditText wiadomosc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numer = findViewById(R.id.editTextNumer);
        wiadomosc = findViewById(R.id.editTextWiadomosc);
        Button sendButton = findViewById(R.id.send_button);
        Button receiveButton = findViewById(R.id.receive_button);
        messageTextView = findViewById(R.id.message_textview);

        // Sprawdzanie i uzyskiwanie uprawnień do wysyłania SMS
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        // Obsługa kliknięcia przycisku "Wyślij"
        sendButton.setOnClickListener(v -> {
            String numer_tel = numer.getText().toString();
            String tresc_wiadomosci = wiadomosc.getText().toString();

            // Sprawdzenie, czy treść wiadomości i numer telefonu są niepuste
            if (numer_tel.trim().length() > 0 && tresc_wiadomosci.trim().length() > 0) {
                // Sprawdzenie, czy zostały przyznane uprawnienia do wysyłania SMS
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(numer_tel, null, tresc_wiadomosci, null, null);

                    Toast.makeText(getApplicationContext(), "Wiadomość wysłana!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Brak uprawnień do wysyłania SMS.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Wpisz numer i treść wiadomości.", Toast.LENGTH_LONG).show();
            }
        });
        receiveButton.setOnClickListener(v -> {
            // Kod do obsługi odbierania SMS-ów
            IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        Object[] pdus = (Object[]) extras.get("pdus");
                        if (pdus != null && pdus.length > 0) {
                            SmsMessage[] messages = new SmsMessage[pdus.length];
                            for (int i = 0; i < pdus.length; i++) {
                                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            }
                            StringBuilder messageBuilder = new StringBuilder();
                            for (SmsMessage message : messages) {
                                messageBuilder.append(message.getMessageBody());
                            }
                            messageTextView.setText(messageBuilder.toString());
                        }
                    }
                }
            }, filter);
        });
    }
}
