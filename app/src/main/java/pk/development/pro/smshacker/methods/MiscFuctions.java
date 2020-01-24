package pk.development.pro.smshacker.methods;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MiscFuctions {
    public static Boolean getIsProcessRunning() {
        return isProcessRunning;
    }

    public static void setIsProcessRunning(Boolean setbool) {
        isProcessRunning = setbool;
    }

    public static Boolean isProcessRunning = false;
    public static void writeSuCmd(String cmd) {
        Process p;

        try {
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(cmd);
            //os.writeByte(15);
            os.flush();
            System.out.println("Cmd Sent");
            os.writeBytes("exit\n");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("writeSuCmd Error "+e.toString());
        }
    }
}

