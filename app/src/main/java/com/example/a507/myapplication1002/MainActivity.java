package com.example.a507.myapplication1002;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    protected Button btHomepage, btDial, btCall, btSms, btMap, btRecog, btTts,
            btEcho, btContact, btBitmap, btToastPs, btService;
    protected TextView tvRecog;
    protected EditText etTts, etDelay;
    public ImageView ivBitmap;
    protected TextToSpeech tts;
    private static final int CODE_RECOG = 1215, CODE_ECHO = 1227, CODE_CONTACT = 1529;
    protected boolean bService = false;
    protected String sBitmapUrl = "https://sites.google.com/site/yongheuicho/_/rsrc/1313446792839/config/customLogo.gif";
    protected TelephonyManager telephonyManager;
    protected CommStateListener commStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btHomepage = (Button) findViewById(R.id.btHomepage);
        btHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ice.mokwon.ac.kr"));
                startActivity(intent);
            }
        });
        btDial = (Button) findViewById(R.id.btDial);
        btDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0428297670"));
                startActivity(intent);
            }
        });
        btCall = (Button) findViewById(R.id.btCall);
        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:0428297670"));
                startActivity(intent);
            }
        });
        btSms = (Button) findViewById(R.id.btSms);
        btSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:0428297670"));
                intent.putExtra("sms_body", "Mokwon University");
                startActivity(intent);
            }
        });
        btMap = (Button) findViewById(R.id.btMap);
        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:36.321609,127.337957?z=20"));
                startActivity(intent);
            }
        });
        tvRecog = (TextView) findViewById(R.id.tvRecog);
        btRecog = (Button) findViewById(R.id.btRecog);
        btRecog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceRecog(CODE_RECOG);
            }
        });
        etTts = (EditText) findViewById(R.id.etTts);
        btTts = (Button) findViewById(R.id.btTts);
        btTts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakStr(etTts.getText().toString());
            }
        });
        tts = new TextToSpeech(this, this);
        btEcho = (Button) findViewById(R.id.btEcho);
        btEcho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceRecog(CODE_ECHO);
            }
        });
        etDelay = (EditText) findViewById(R.id.etDelay);
        btContact = (Button) findViewById(R.id.btContact);
        btContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, CODE_CONTACT);
            }
        });
        ivBitmap = (ImageView) findViewById(R.id.ivBitmap);
        btBitmap = (Button) findViewById(R.id.btBitmap);
        btBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new BitmapRunnable(ivBitmap, sBitmapUrl)).start();
            }
        });

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        commStateListener = new CommStateListener(telephonyManager, this);
        btToastPs = (Button) findViewById(R.id.btToastPs);
        btToastPs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastPhoneState();
            }
        });

        btService = (Button) findViewById(R.id.btService);
        btService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateService();
            }
        });
    }

    private void updateService() {
        Intent intent = new Intent(this, PhoneCallService.class);
        if (bService) {
            stopService(intent);
            bService = false;
            btService.setText("Strat Svc");
        } else {
            startService(intent);
            bService = true;
            btService.setText("Stop Svc");
        }

    }

    private void toastPhoneState() {
        int nPhoneType = telephonyManager.getPhoneType();
        int nNetworkType = telephonyManager.getNetworkType();
        String sPhoneType;
        switch (nPhoneType) {
            case TelephonyManager.PHONE_TYPE_GSM:
                sPhoneType = "Voice: GSM";
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                sPhoneType = "Voice: CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                sPhoneType = "Voice: SIP";
                break;
            default:
                sPhoneType = "Voice: 코드 번호 = " + nPhoneType;
        }
        String sNetworkType;
        switch (nNetworkType) {
            case TelephonyManager.NETWORK_TYPE_CDMA:
                sNetworkType = "Data: 2G CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                sNetworkType = "Data: 3G UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                sNetworkType = "Data: 3G HSPA+";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                sNetworkType = "Data: 4G LTE";
                break;
            default:
                sNetworkType = "Data: 코드 번호 = " + nNetworkType;
        }
        Toast.makeText(this, sPhoneType, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, sNetworkType, Toast.LENGTH_SHORT).show();
        int nRssi = commStateListener.nRssi;
        Toast.makeText(this, "RSSI = " + nRssi, Toast.LENGTH_SHORT).show();
    }

    private void voiceRecog(int nCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak.");
        startActivityForResult(intent, nCode);
    }

    private void speakStr(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
        while (tts.isSpeaking()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPhoneNumFromName(String sName) {
        String sPhoneNum = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(sName));
        String[] arProjection = new String[]{ContactsContract.Contacts._ID};
        Cursor cursor = getContentResolver().query(uri, arProjection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String sId = cursor.getString(0);
            String[] arProjNum = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            String sWhereNum = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
            String[] sWhereNumParam = new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, sId};
            Cursor cursorNum = getContentResolver().query(ContactsContract.Data.CONTENT_URI, arProjNum, sWhereNum, sWhereNumParam, null);
            if (cursorNum != null && cursorNum.moveToFirst()) {
                sPhoneNum = cursorNum.getString(0);
            }
            cursorNum.close();
        }
        cursor.close();
        return sPhoneNum;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CODE_RECOG) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0);
                tvRecog.setText(sRecog);
            } else if (requestCode == CODE_ECHO) {
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0);
                String sDelay = etDelay.getText().toString();
                int nDelay = Integer.parseInt(sDelay); // in sec
                try {
                    Thread.sleep(nDelay * 1000); // in msec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                speakStr(sRecog);
            } else if (requestCode == CODE_CONTACT) {
                String[] sFilter = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(data.getData(), sFilter, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    String sName = cursor.getString(0);
                    String sPhoneNum = cursor.getString(1);
                    cursor.close();
                    Toast.makeText(this, sName + " = " + sPhoneNum, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
            tts.setPitch(1.0f);
            tts.setSpeechRate(1.0f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        telephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    protected void onPause() {
        telephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_NONE);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (bService) {
            Intent intent = new Intent(this, PhoneCallService.class);
            stopService(intent);
        }
        super.onDestroy();
    }
}

