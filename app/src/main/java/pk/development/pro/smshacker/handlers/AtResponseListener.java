package pk.development.pro.smshacker.handlers;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import pk.development.pro.smshacker.Constants;

public class AtResponseListener implements Runnable {
    private Handler handler;
    private String serialPort;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean keepRunning;
    private Context context;
    public AtResponseListener(Context context, Handler handler, String serialPort) {
        this.handler = handler;
        this.serialPort = serialPort;
        this.context = context;
        keepRunning = true;
    }

    public void stopThread() {
        keepRunning = false;
    }

    public void run() {
        String atcmm = String.format("cat %s\n", serialPort);
        try {
            Runtime r = Runtime.getRuntime();
            Process process = r.exec("su");
            dos = new DataOutputStream(process.getOutputStream());

            dos.writeBytes(atcmm);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dos.flush();
            dis = new DataInputStream(process.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            handler.obtainMessage(Constants.ERROR_OPENING_PORT, "Error opening port. Is your phone rooted?").sendToTarget();
            return;
        }

        while (keepRunning) {
            try {
                StringBuilder sb = new StringBuilder();

                int bufferLen = 0;
                while ((bufferLen = dis.available()) != 0) {
                    byte[] b = new byte[bufferLen];
                    dis.read(b);
                    sb.append(new String(b) + "\n");
                }
                if(sb.length() > 1) {
                    handler.obtainMessage(Constants.MESSAGE_RECEIVED, sb.toString()).sendToTarget();
                }


            } catch (IOException e) {
                if (e.getMessage() != null)
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }



        try {
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}