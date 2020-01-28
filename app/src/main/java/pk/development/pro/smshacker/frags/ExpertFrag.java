package pk.development.pro.smshacker.frags;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import pk.development.pro.smshacker.R;
import pk.development.pro.smshacker.methods.PduSms;

import static android.app.Activity.RESULT_OK;

public class ExpertFrag extends Fragment {
    private EditText editExpMsg,editTextExpNumber,editExpPort;
    private Spinner spinnerSmsType;
    private Button btnSendGsm, btnClearGsm, btnExpContact;
    private Switch switchRawPdu;
    private TableRow expertTableType,expertTablePort, experTableContact;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_expert, container, false);

        editExpMsg = root.findViewById(R.id.editExpMsg);
        editTextExpNumber = root.findViewById(R.id.editTextExpNumber);
        editExpPort = root.findViewById(R.id.editExpPort);
        spinnerSmsType = root.findViewById(R.id.spinnerSmsType);

        expertTableType = root.findViewById(R.id.expertTableType);
        expertTablePort = root.findViewById(R.id.expertTablePort);
        experTableContact = root.findViewById(R.id.experTableContact);

        btnSendGsm = root.findViewById(R.id.btnSendGsm);
        btnClearGsm = root.findViewById(R.id.btnClearGsm);
        btnExpContact = root.findViewById(R.id.btnExpContact);
        switchRawPdu = root.findViewById(R.id.switchRawPdu);

        switchRawPdu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                expertTableType.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                expertTablePort.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                experTableContact.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                editExpMsg.setHint(isChecked ? "00112233445566" : "Type your message here....");
            }
        });

        btnExpContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });


        btnClearGsm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editExpMsg.setText("");
            }
        });

        btnSendGsm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String PDU_SMS = switchRawPdu.isChecked()
                        ?editExpMsg.getText().toString()
                        : PduSms.createPduString(
                        editTextExpNumber.getText().toString(),
                        editExpMsg.getText().toString(),
                        spinnerSmsType.getSelectedItemPosition() == 3 ? "40" : "00", //TP_PID
                        PduSms.MSG_TYPES[spinnerSmsType.getSelectedItemPosition()], //TP_DCS
                        false);

                    Log.i("ExpertFrag:", PDU_SMS);
                    if(PduSms.sendSms(editExpPort.getText().toString(), PDU_SMS)) {
                        Snackbar.make(ExpertFrag.this.getView(), "Sending: " + PDU_SMS, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(ExpertFrag.this.getView(), "Error Sending: " + PDU_SMS, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
            }
        });

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
                editTextExpNumber.setText(cursor.getString(numberIndex));
            }
        }
    }
}
