package com.example.brailleconverter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Logger;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.SupportedLanguagesListener;
import net.gotev.speech.TextToSpeechCallback;
import net.gotev.speech.UnsupportedReason;
import net.gotev.speech.ui.SpeechProgressView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    // Zone for variable
    int count = 0;
    boolean ok = false;
    public List<String> listData = new ArrayList<String>();
    public boolean isClickPost = false;
    public int trang_thai = 0 , vitri = -1;
    public int Speed = 2;
    private final int PERMISSIONS_REQUEST = 1;
    private static final String LOG_TAG = "a";
    private SpeechProgressView progress;
    String address = "20:16:11:07:32:61";
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    ListView devicelist;
    BluetoothAdapter btadapter;
    private ProgressDialog progress_bluetooth;
    BluetoothAdapter myBluetooth2 = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    List<Post> tmp = new ArrayList<Post>();
    // End ZONE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VideoView videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video_background);
        videoview.setVideoURI(uri);
        videoview.start();
        videoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Touch_Screen();
            }
        });
        Speech.init(this, getPackageName(), mTttsInitListener);
        copyAssets();
        Read_newspaper_tts();
//        requestBlePermissions(this,1);
//        myBluetooth = BluetoothAdapter.getDefaultAdapter();
//        new MainActivity.ConnectBT().execute();
//        if ( myBluetooth==null ) {
//            Toast.makeText(getApplicationContext(), "Bluetooth device not available", Toast.LENGTH_LONG).show();
//            finish();
//        }
//        else if ( !myBluetooth.isEnabled() ) {
//            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(turnBTon, 1);
//        }

    }
    // Zone for T2S and S2T
    private TextToSpeech.OnInitListener mTttsInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(final int status) {
            switch (status) {
                case TextToSpeech.SUCCESS:
                    Logger.info(LOG_TAG, "TextToSpeech engine successfully started");
                    break;

                case TextToSpeech.ERROR:
                    Logger.error(LOG_TAG, "Error while initializing TextToSpeech engine!");
                    break;

                default:
                    Logger.error(LOG_TAG, "Unknown TextToSpeech status: " + status);
                    break;
            }
        }
    };
    private void onSetSpeechToTextLanguage() {
        Speech.getInstance().getSupportedSpeechToTextLanguages(new SupportedLanguagesListener() {
            @Override
            public void onSupportedLanguages(List<String> supportedLanguages) {
                CharSequence[] items = new CharSequence[supportedLanguages.size()];
                supportedLanguages.toArray(items);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Current language: " + Speech.getInstance().getSpeechToTextLanguage())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Locale locale;

                                if (Build.VERSION.SDK_INT >= 21) {
                                    locale = Locale.forLanguageTag(supportedLanguages.get(i));
                                } else {
                                    String[] langParts = supportedLanguages.get(i).split("-");

                                    if (langParts.length >= 2) {
                                        locale = new Locale(langParts[0], langParts[1]);
                                    } else {
                                        locale = new Locale(langParts[0]);
                                    }
                                }

                                Speech.getInstance().setLocale(locale);
                                Toast.makeText(MainActivity.this, "Selected: " + items[i], Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("Cancel", null)
                        .create()
                        .show();
            }

            @Override
            public void onNotSupported(UnsupportedReason reason) {
                switch (reason) {
                    case GOOGLE_APP_NOT_FOUND:
                        showSpeechNotSupportedDialog();
                        break;

                    case EMPTY_SUPPORTED_LANGUAGES:
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.set_stt_langs)
                                .setMessage(R.string.no_langs)
                                .setPositiveButton("OK", null)
                                .show();
                        break;
                }
            }
        });
    }
    private void onSetTextToSpeechVoice() {
        List<Voice> supportedVoices = Speech.getInstance().getSupportedTextToSpeechVoices();

        if (supportedVoices.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.set_tts_voices)
                    .setMessage(R.string.no_tts_voices)
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        CharSequence[] items = new CharSequence[supportedVoices.size()];
        Iterator<Voice> iterator = supportedVoices.iterator();
        int i = 0;

        while (iterator.hasNext()) {
            Voice voice = iterator.next();

            items[i] = voice.toString();
            i++;
        }

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Current: " + Speech.getInstance().getTextToSpeechVoice())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Speech.getInstance().setVoice(supportedVoices.get(i));
                        Toast.makeText(MainActivity.this, "Selected: " + items[i], Toast.LENGTH_LONG).show();
                    }
                })
                .setPositiveButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Speech.getInstance().shutdown();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!

            } else {
                // permission denied, boo!
                Toast.makeText(MainActivity.this, R.string.permission_required, Toast.LENGTH_LONG).show();
            }
        }
    }
    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(MainActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.speech_not_available)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enable_google_voice_typing)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }
    // END Zone for T2S and S2T
    public void Touch_Screen(){
        if(trang_thai == 0){
            if(Speech.getInstance().isSpeaking()) Speech.getInstance().stopTextToSpeech();
            if(Speech.getInstance().isListening()) Speech.getInstance().stopListening();
            Speech.getInstance().say("đang lắng nghe", new TextToSpeechCallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onCompleted() {
                    try {
                        // you must have android.permission.RECORD_AUDIO granted at this point
                        Speech.getInstance().startListening(new SpeechDelegate() {
                            @Override
                            public void onStartOfSpeech() {
                                Log.i("speech", "speech recognition is now active");
                            }

                            @Override
                            public void onSpeechRmsChanged(float value) {
                                //Log.d("speech", "rms is now: " + value);
                            }

                            @Override
                            public void onSpeechPartialResults(List<String> results) {
                                StringBuilder str = new StringBuilder();
                                for (String res : results) {
                                    str.append(res).append(" ");
                                }

                                Log.i("speech", "partial result: " + str.toString().trim());
                            }

                            @Override
                            public void onSpeechResult(String result) {
                                Log.i("speech", "result: " + result);

                                try {
                                    analyst_querry(result);
                                } catch (Exception e){
                                    Log.e("Er" , e.toString());
                                }


                            }
                        });

                    } catch (SpeechRecognitionNotAvailable exc) {
                        Log.e("speech", "Speech recognition is not available on this device!");
                    } catch (GoogleVoiceTypingDisabledException exc) {
                        showEnableGoogleVoiceTyping();
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(MainActivity.this, "TTS onError", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(trang_thai == 2){
            isClickPost = true;
        }

    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        String link = data.getData().getPath();
                        ReadPDF readPDF = new ReadPDF();
                        listData = readPDF.Read_From_Storage(link);
                        say(0 , listData);
                        if (Environment.isExternalStorageManager()){
                        }else {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri2 = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                            intent.setData(uri2);
                            startActivity(intent);
                        }
                    }
                }
            });
    public void openFile(){
        Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //chooseFile.setType("application/pdf");
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        someActivityResultLauncher.launch(chooseFile);
    }
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);

                String outDir =  MainActivity.this.getApplicationInfo().dataDir + "/databases/";

                File outFile = new File(outDir, filename);

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            if(Speech.getInstance().isSpeaking()) Speech.getInstance().stopTextToSpeech();
            if(Speech.getInstance().isListening()) Speech.getInstance().stopListening();
            Speech.getInstance().say("đang lắng nghe", new TextToSpeechCallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onCompleted() {
                }

                @Override
                public void onError() {
                    Toast.makeText(MainActivity.this, "TTS onError", Toast.LENGTH_SHORT).show();
                }
            });
            progress_bluetooth = ProgressDialog.show(MainActivity.this, "Đang kết nối thiết bị", "Xin vui lòng đợi");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                if(Speech.getInstance().isSpeaking()) Speech.getInstance().stopTextToSpeech();
                if(Speech.getInstance().isListening()) Speech.getInstance().stopListening();
                Speech.getInstance().say("Đã kết nối thành công với thiết bị");
                msg("Connected");
                isBtConnected = true;
            }

            progress_bluetooth.dismiss();
        }
    }
    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }
    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }
    public String change_speed(Boolean i){
        String txt;
        if(i){
            Speed++;
            if(Speed > 3){
                Speed = 3;
                txt = "Tốc độ đã nhanh nhất";
            } else txt = "Đã tăng tốc độ";
        } else{
            Speed--;
            if(Speed <= 0){
                Speed = 1;
                txt = "Tốc độ đã chậm nhất";
            } else txt = "Đả giảm tốc độ";
        }
        Speech.getInstance().say(txt, new TextToSpeechCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError() {
                Toast.makeText(MainActivity.this, "TTS onError", Toast.LENGTH_SHORT).show();
            }
        });
        return Integer.toString(Speed);
    }
    public void Read_newspaper_tts(){
        try{
            Newspaper newspaper = new Newspaper();
            tmp = newspaper.execute().get();
            for(Post i: tmp){
                Log.i("Main " , i.url);
                Log.i("Main " , i.title);
            }
        } catch (Exception e){
            e.toString();
        }
    }
    public int Read_newspaper(){
        ok = false;
        isClickPost = false;

        for(Post i : tmp){
            Speech.getInstance().say(i.title, new TextToSpeechCallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError() {
                    Toast.makeText(MainActivity.this, "TTS onError", Toast.LENGTH_SHORT).show();
                }
            });
            try{
                Thread.sleep(10000);
            }catch (Exception e){
                e.toString();
            }
        }

        try{
            List<Post> hoan = tmp;
            if(Speech.getInstance().isSpeaking()) Speech.getInstance().stopTextToSpeech();
            if(Speech.getInstance().isListening()) Speech.getInstance().stopListening();
            Speech.getInstance().say("Bạn muốn đọc bài báo nào", new TextToSpeechCallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onCompleted() {
                    try {
                        // you must have android.permission.RECORD_AUDIO granted at this point
                        Speech.getInstance().startListening(new SpeechDelegate() {
                            @Override
                            public void onStartOfSpeech() {
                                Log.i("speech", "speech recognition is now active");
                            }

                            @Override
                            public void onSpeechRmsChanged(float value) {
                                //Log.d("speech", "rms is now: " + value);
                            }

                            @Override
                            public void onSpeechPartialResults(List<String> results) {
                                StringBuilder str = new StringBuilder();
                                for (String res : results) {
                                    str.append(res).append(" ");
                                }

                                Log.i("speech", "partial result: " + str.toString().trim());
                            }

                            @Override
                            public void onSpeechResult(String result) {
                                Log.i("speech", "result: " + result);

                                try {
                                    Matching_Number matching_number = new Matching_Number();
                                    vitri  = matching_number.Matching(result);
                                } catch (Exception e){
                                    Log.e("Er" , e.toString());
                                }


                            }
                        });

                    } catch (SpeechRecognitionNotAvailable exc) {
                        Log.e("speech", "Speech recognition is not available on this device!");
                    } catch (GoogleVoiceTypingDisabledException exc) {
                        showEnableGoogleVoiceTyping();
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(MainActivity.this, "TTS onError", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            e.toString();
        }
        return -1;
    }
    public void analyst_querry(String s){
        try{
            Matching_Request matching_request = new Matching_Request();
            s = s.toUpperCase();
            int num = matching_request.Matching(s);
            Log.e("num" , Integer.toString(num));
            switch (num){
                case 0:
                    Speech.getInstance().say("Mình không hiểu ý bạn");
                    break;
                case 1:
                    //Speech.getInstance().say("Đã rõ");
                    //sendSignal(change_speed(true));
                    break;
                case 2:
                    //Speech.getInstance().say("Đã rõ");
                    //sendSignal(change_speed(false));
                    break;
                case 3:
                    Speech.getInstance().say("Đã rõ");
                    vitri = 0;
                    Read_newspaper();
                    if(vitri!=0){
                        ReadPostFromNewsPaper read = new ReadPostFromNewsPaper(tmp.get(vitri).url);
                        List<String> list2 = new ArrayList<String>();
                        list2 = read.execute().get();
                        say(0,list2);
                    }
                    break;
                case 4:
                    Speech.getInstance().say("Đã rõ");
                    openFile();
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                default:
                    Speech.getInstance().say("Mình không hiểu ý bạn");
                    break;
            }
        } catch (Exception e){
            Log.e("er" , e.toString());
        }

    }
    void say(int i , List<String> data){
        if(i>=data.size()) return;
        Speech.getInstance().say(data.get(i), new TextToSpeechCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onCompleted() {
                say(i+1,data);
            }

            @Override
            public void onError() {
                Toast.makeText(MainActivity.this, "TTS onError", Toast.LENGTH_SHORT).show();
            }
        });
    }
}