package pk.development.pro.smshacker.methods;

import java.io.DataOutputStream;
import java.io.IOException;

import static pk.development.pro.smshacker.Constants.AT_GSM_COMMANDS;

public class AtFuctions {

    public static void  getGsmData(String serial_port){

        Process p;
        try {
            for (int x =0; x < AT_GSM_COMMANDS.length;x++) {
                p = Runtime.getRuntime().exec("su");


                DataOutputStream os = new DataOutputStream(p.getOutputStream());
                os.flush();
                String AT_CMD = String.format("echo -e \"%s\\r\" > %s\n",
                        AT_GSM_COMMANDS[x],
                        serial_port);

                os.writeBytes(AT_CMD);
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                os.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void  sendCmd(String serial_port,String gsmcmd){
        Process p;
        try {
            p = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            String AT_CMD = String.format("echo -e \"%s\\r\" > %s\n",gsmcmd,serial_port);
            os.writeBytes(AT_CMD);
            try {
                Thread.sleep(400);
            }catch (Exception ee){

            }
            System.out.println("sent " + AT_CMD);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

