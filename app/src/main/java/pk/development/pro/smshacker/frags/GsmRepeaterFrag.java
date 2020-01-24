package pk.development.pro.smshacker.frags;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import pk.development.pro.smshacker.Constants;
import pk.development.pro.smshacker.R;

public class GsmRepeaterFrag extends Fragment {
    private EditText editRepeatIP, editRepeatPort, editRepeatFile, editRepeatPSize, editTargetByte;
    private Button btnOpenLogFile, btnSendPackets;


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gsmrepeater, container, false);

        editRepeatIP = root.findViewById(R.id.editRepeatIP);
        editRepeatPort = root.findViewById(R.id.editRepeatPort);
        editRepeatFile = root.findViewById(R.id.editRepeatFile);
        editTargetByte = root.findViewById(R.id.editTargetByte);
        editRepeatPSize = root.findViewById(R.id.editRepeatPSize);

        btnOpenLogFile = root.findViewById(R.id.btnOpenLogFile);
        btnSendPackets = root.findViewById(R.id.btnSendPackets);


        btnSendPackets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            File logFile = new File(editRepeatFile.getText().toString());
                            byte[] bytes = new byte[(int)logFile.length()];
                            int pSize = Integer.parseInt(editRepeatPSize.getText().toString());
                            InetAddress local = InetAddress.getByName(editRepeatIP.getText().toString());
                            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(logFile));

                            DatagramSocket s = new DatagramSocket();
                            int readAll = buf.read(bytes, 0, bytes.length);
                            String[] tbs = editTargetByte.getText().toString().split(",");
                            byte[] filter = new byte[tbs.length];
                            for(int i = 0; i < tbs.length;i++) {
                                filter[i] = (byte)Integer.parseInt(tbs[i], 16);
                            }

                            for(int i = 0; i < bytes.length;i++) {

                                    if(i + pSize < bytes.length) {
                                        if(matchingBytes(filter, bytes ,i)){
                                            byte[] modBytes = getPacket(bytes, i);
                                            s.send(new DatagramPacket(modBytes, 0, modBytes.length,
                                                    local, Integer.parseInt(editRepeatPort.getText().toString())));

                                        }
                                    }
                            }

                        } catch (Exception ex) {
                            Log.e("GsmRepeaterFrag", ex.getMessage());
                        }
                    }
                }).start();

            }
        });
        return root;
    }

    private boolean matchingBytes(byte[] filter, byte[] bytes, int startIndex) {

        boolean match = true;
        if(startIndex + filter.length < bytes.length) {

            for(int i = 0; i < filter.length && match;i++) {
                if( filter[i] == (byte)0xFF || (bytes[startIndex] & 255) == filter[i])
                {
                    startIndex++;
                    continue;
                }
                match = false;
            }

        } else  match = false;

        return match;
    }
    private byte[] getPacket(byte[] bytes, int startIndex) {
        byte[] modBytes = new byte[39];//add gsmtap header
        //gsmtap header = 16 + 23 packet = 39
        for(int i = 0; i < Constants.GSMTAP_HEADER.length; i++) modBytes[i] = Constants.GSMTAP_HEADER[i];


        for(int i = Constants.GSMTAP_HEADER.length; i < modBytes.length; i++) {
            modBytes[i] = (byte)(bytes[startIndex++] & 255);
        }
        return modBytes;
    }
}
