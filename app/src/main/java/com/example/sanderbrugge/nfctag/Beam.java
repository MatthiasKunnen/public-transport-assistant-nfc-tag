package com.example.sanderbrugge.nfctag;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.nfc.NdefRecord.createMime;

/**
 * Created by sanderbrugge on 22/02/17.
 */

public class Beam extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
    private static final String TAG = "Beam";
    private NfcAdapter mNfcAdapter;
    private Unbinder unbinder;
    private TextView tvLijn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        tvLijn = (TextView) findViewById(R.id.lblOutput);
        tvLijn.setText("sending ID: 1");
        Log.i(TAG,"butterknife OK");
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Log.i(TAG,"geen nfc beschikbaar");
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Log.i(TAG,"NFC beschikbaar");


        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Log.i(TAG,"createBeamMessage");

        String text = ("1");
        Log.i(TAG,text);
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime(
                        text, text.getBytes())
                });
        return msg;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Log.i(TAG,"in processIntent");
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String message = new String(msg.getRecords()[0].getPayload());
        Log.i(TAG,message);
        tvLijn.setText(new String(msg.getRecords()[0].getPayload()));
    }
}
