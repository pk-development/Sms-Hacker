package pk.development.pro.smshacker.frags;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pk.development.pro.smshacker.Constants;
import pk.development.pro.smshacker.R;
import pk.development.pro.smshacker.handlers.AtResponseListener;
import pk.development.pro.smshacker.methods.AtFuctions;

public class AtCommander extends Fragment {
    private Button atCmdSend, atCmdClear, atCmdOpen, atCmdGsm;
    private AtResponseListener responseListener;
    private Handler mHandler;
    private boolean  isPortOpen = false;
    private TextView atCmdLog;
    private  EditText atCmdPort,atCmdInput;
    private Spinner atCmdSpinner;
    private CheckBox atCmdCheck;
    private TableRow atCmdCommand, atCmdTableInput;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_atcommand, container, false);


        atCmdCommand = root.findViewById(R.id.atCmdCommand);
        atCmdTableInput = root.findViewById(R.id.atCmdTableInput);
        atCmdSend = root.findViewById(R.id.atCmdSend);
        atCmdClear = root.findViewById(R.id.atCmdClear);
        atCmdOpen = root.findViewById(R.id.atCmdOpen);
        atCmdGsm = root.findViewById(R.id.atCmdGsm);
        atCmdLog = root.findViewById(R.id.atCmdLog);
        atCmdPort = root.findViewById(R.id.atCmdPort);
        atCmdInput = root.findViewById(R.id.atCmdInput);
        atCmdSpinner = root.findViewById(R.id.atCmdSpinner);
        atCmdCheck = root.findViewById(R.id.atCmdCheck);

        atCmdLog.setMovementMethod(new ScrollingMovementMethod());


        atCmdCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                atCmdTableInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                atCmdCommand.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });

        atCmdGsm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPortOpen) {
                    AtFuctions.getGsmData(atCmdPort.getText().toString());
                }else {
                    Toast.makeText(getActivity(), "Please open port", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final List<String> listCommands = new ArrayList<>();

        for(String s : Constants.COMMANDS) {
            String[] ele = s.split("#");
            listCommands.add(ele[0]);
        }

        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,listCommands);

        atCmdSpinner.setAdapter(adapter_state);

        atCmdClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atCmdLog.setText("");
            }
        });

        atCmdSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isPortOpen) {
                    AtFuctions.sendCmd(
                            atCmdPort.getText().toString(),
                            atCmdCheck.isChecked()
                                    ? atCmdInput.getText().toString()
                                    : atCmdSpinner.getSelectedItem().toString());
                }else {
                    Toast.makeText(getActivity(), "Please open port", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mHandler = new Handler(new Handler.Callback(){

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.ERROR_OPENING_PORT:
                        atCmdLog.append(msg.obj.toString() + "\n");
                        isPortOpen = false;
                        atCmdOpen.setText("Open");
                        break;
                    case Constants.MESSAGE_RECEIVED:
                        atCmdLog.append(msg.obj.toString() + "\n");

                        break;
                }
                return false;
            }
        });


        atCmdOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPortOpen) {
                    isPortOpen = true;
                    atCmdOpen.setText("Close");
                    responseListener = new AtResponseListener(getActivity(), mHandler, atCmdPort.getText().toString());
                    new Thread(responseListener).start();

                } else {
                    isPortOpen = false;
                    atCmdOpen.setText("Open");
                    responseListener.stopThread();
                }

            }
        });

        return root;
    }


}
