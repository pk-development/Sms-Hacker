package pk.development.pro.smshacker.methods;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PduSms {
    public static final String SEND_PDU_CMD = "AT+CMGS=17\r";
    public static final String[] MSG_TYPES = {
            "18", //16 bit encoding
            "C0", // voice mail indication
            "08",
            "40", //type Zero sms
    };

    public static String
            FLASH3 = "18",// 16 bit
            SILENT = "C0",// decimal = 192 silent voice mail indication
            NORMAL = "08",//16 bit encoding
            TYPE0 = "40";//pid 40 hex = type 0 sms

    public static String createWapPushSms(
            String target,
            String START_DATE,
            String END_DATE,
            String header,
            String msg,
            String MODE ){

            String NUMBER = SwapNumber(target);
            String HEX_NUM_LEN = Integer.toHexString(target.length());

            if(target.length() < 15){
                HEX_NUM_LEN = "0"+HEX_NUM_LEN;
            }

            String MSG_LEN_HEX = Integer.toHexString(msg.length()+69);
            String MSG2HEX = StringToHex(msg);
            String HEADER = StringToHex(header);

            String wapPdu = String.format("004100%s91%s0004%s0B05040B84C0020003F001010B060403AE81EA02056A0045C6%s03%s000AC306%s10C306%s0103%s000101",
                    HEX_NUM_LEN,//hex value of the number length
                    NUMBER,//number of target
                    MSG_LEN_HEX,//length of the sms data to be sent +69
                    MODE,//0B = no www or http OC = http:// 0D = http://wwww 0E https:// 0F = https://www
                    HEADER,//header has tp be 18 digits long
                    START_DATE,//start data in format 150312223112
                    END_DATE,//end data in format 150312223112
                    MSG2HEX);//msg data in hex format can be longer than 70 chars

        return wapPdu;
    }

    public static String createPduString(String number,String sms,String tp_pid,String tp_dcs,Boolean del_report){
        number = number.replaceAll("\\s+","").replace("-","");

        String numType = "91";
        if(number.startsWith("+")) {
            number = number.replace("+","");
        } else if(number.startsWith("0")) {
            numType = "81";
        }

        String NUMBER = SwapNumber(number);
        String SMSTEXT = sms;


        int iNUMBER_LEN = number.length();
        int iSMS_LEN = sms.length() * 2;// need to double length for 18 bit encoding

        String xNUMBER_LEN = Integer.toHexString(iNUMBER_LEN);

        if(iNUMBER_LEN <= 15)
        {
            xNUMBER_LEN = "0" + xNUMBER_LEN;
        }

        return String.format( (iSMS_LEN <= 15) ? "00%s00%s" + numType + "%s%s%s0%s%s" : "00%s00%s" + numType +"%s%s%s%s%s",
                del_report ? "21" : "01",
                xNUMBER_LEN,//hex value for length of number
                NUMBER,
                tp_pid,
                tp_dcs,
                Integer.toHexString(iSMS_LEN),
                StringToHex18Bit(SMSTEXT));
    }


    public static Boolean isNumberOdd(int numberlen)
    {
        return numberlen % 2 != 0;
    }

    public static String SwapNumber(String number)
    {
        if (isNumberOdd(number.length()))
        {
            number += "F";
        }
        int loopcount = number.length();
        //char[] charArray = receiver.ToCharArray();
        StringBuilder sb = new StringBuilder();
        StringBuilder temp_reverse = new StringBuilder();

        for (int x = 0; x < loopcount / 2; x++)
        {
            String temp = number.substring(0, 2);
            temp_reverse.append(temp).reverse();
            sb.append(temp_reverse.toString());
            temp_reverse.setLength(0);
            number = number.substring(2);
        } number = sb.toString();

        return number;
    }
    public static String decodePduStatusReport(String rawPdu){
        // 07
        // 91               international format
        // 538375000014     smsc                (reverse octets)
        // 06               SMS STATUS REPORT = 06
        // C6               sms id
        // 0C               MSG REFERENCE NR. :	198 (0xC6)
        // 91               international format
        // 127364837467     target number       (reverse octets)
        // 51306132916200   sent timestamp      (reverse octets)
        // 51306132918200   delivered timestamp (reverse octets)
        // 00

        String numberType = rawPdu.substring(0,2),
               numberFormat = rawPdu.substring(2,4),
               smscNumber = rawPdu.substring(4,16),
               smsMode = rawPdu.substring(16,18),
               smsRefNo = rawPdu.substring(18,20),
               receiverNumberLen = rawPdu.substring(20,22);

        int hexlen = Integer.parseInt(receiverNumberLen,16);
        String
               numberFormat2 = rawPdu.substring(22,24),
               targetNumber = rawPdu.substring(24,24+hexlen),
               sentTimeStamp = rawPdu.substring(36,50),
               deliveredTimeStamp = rawPdu.substring(50,64),
               smsStatus = rawPdu.substring(rawPdu.length()-2);



        Log.d("PduSms",
                "numberType:"+numberType+
                "\nnumberFormat:"+numberFormat+
                "\nsmscNumber:"+SwapNumber(smscNumber)+
                "\nsmsMode:"+smsMode+
                "\nsmsRefNo:"+smsRefNo+
                "\nreceiverNumberLen:"+receiverNumberLen+
                "\nnumberFormat2:"+numberFormat2+
                 "\ntargetNumber:"+SwapNumber(targetNumber)+
                "\nsentTimeStamp:"+SwapNumber(sentTimeStamp)+
                "\ndeliveredTimeStamp:"+SwapNumber(deliveredTimeStamp)+
                 "\nsmsStatus:"+smsStatus
                );


        return null;
    }
    public static String StringToHex(String hexstring)
    {
        StringBuilder sb = new StringBuilder();
        for(char t: hexstring.toCharArray()) {
            sb.append(Integer.toHexString(t));
        }
        return sb.toString();
    }

    public static String StringToHex18Bit(String hexstring)
    {
        StringBuilder sb = new StringBuilder();
        for(char t: hexstring.toCharArray())
            sb.append("00"+Integer.toHexString(t));
        return sb.toString();
    }


    public static boolean sendSms(String serial_port,String pdu_string){
        Process p;
        String ECHO_PDU_START = String.format("echo -e \"%s\" > %s\n",SEND_PDU_CMD,serial_port);// AT+CMGS=17
        String ECHO_PDU_DATA = String.format("echo -e \"%s\" > %s\n",pdu_string,serial_port);
        String ECHO_PDU_END = String.format("echo -e '\\x1A' > %s\n",serial_port);

         try {
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb =  new StringBuilder();
            os.writeBytes(ECHO_PDU_START);
            //os.writeByte(15);
            os.flush();
            try{Thread.sleep(200);}catch (Exception e){e.printStackTrace();}
            os.writeBytes(ECHO_PDU_DATA);
            os.flush();
            os.writeBytes(ECHO_PDU_END);
            try{Thread.sleep(200);}catch (Exception e){e.printStackTrace();}
            os.writeByte(26);
            os.flush();
            System.out.println("SENT");
            os.writeBytes("exit\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
             System.out.println("ERROR:" + e);
        }

         return false;
    }

}
