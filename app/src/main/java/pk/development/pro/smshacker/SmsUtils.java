package pk.development.pro.smshacker;

import java.math.BigInteger;
import java.util.ArrayList;

public class SmsUtils {


    public static final ArrayList<Short> WAP_PORTS = new ArrayList<>();

    static {
        WAP_PORTS.add((short)0x0B84);
        WAP_PORTS.add((short)9200);
        WAP_PORTS.add((short)0x0B81);
        WAP_PORTS.add((short)0x0B82);
        WAP_PORTS.add((short)0x0B83);
        WAP_PORTS.add((short)0x0B85);
    }

    public static final String[] ContentTypeHex = {
            "00",
            "01",
            "02",
            "03",
            "04",
            "05",
            "06",
            "07",
            "08",
            "09",
            "0A",
            "0B",
            "0C",
            "0D",
            "0E",
            "0F",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "1A",
            "1B",
            "1C",
            "1D",
            "1E",
            "1F",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29",
            "2A",
            "2B",
            "2C",
            "2D",
            "2E",
            "2F",
            "30",
            "31",
            "32",
            "33",
            "34",
            "35",
            "36",
            "37",
            "38",
            "39",
            "3A",
            "3B",
            "3C",
            "3D",
            "3E",
            "3F",
            "40",
            "41",
            "42",
            "43",
            "44",
            "45",
            "46",
            "47",
            "48",
            "49",
            "4A",
            "4B",
            "4C",
            "4D",
            "4E",
            "4F",
            "50",
            "51",
    };

    public static final String[] ContentTypes = {
            "*/*",                                        /* 0x00 */ //128
            "text/*",                                     /* 0x01 */
            "text/html",                                  /* 0x02 */ //130
            "text/plain",                                 /* 0x03 */ //131
            "text/x-hdml",                                /* 0x04 */
            "text/x-ttml",                                /* 0x05 */
            "text/x-vCalendar",                           /* 0x06 */
            "text/x-vCard",                               /* 0x07 */
            "text/vnd.wap.wml",                           /* 0x08 */
            "text/vnd.wap.wmlscript",                     /* 0x09 */
            "text/vnd.wap.wta-event",                     /* 0x0A */
            "multipart/*",                                /* 0x0B */
            "multipart/mixed",                            /* 0x0C */ //140
            "multipart/form-data",                        /* 0x0D */
            "multipart/byterantes",                       /* 0x0E */
            "multipart/alternative",                      /* 0x0F */
            "application/*",                              /* 0x10 */
            "application/java-vm",                        /* 0x11 */
            "application/x-www-form-urlencoded",          /* 0x12 */
            "application/x-hdmlc",                        /* 0x13 */
            "application/vnd.wap.wmlc",                   /* 0x14 */
            "application/vnd.wap.wmlscriptc",             /* 0x15 */
            "application/vnd.wap.wta-eventc",             /* 0x16 */ //150
            "application/vnd.wap.uaprof",                 /* 0x17 */
            "application/vnd.wap.wtls-ca-certificate",    /* 0x18 */
            "application/vnd.wap.wtls-user-certificate",  /* 0x19 */
            "application/x-x509-ca-cert",                 /* 0x1A */
            "application/x-x509-user-cert",               /* 0x1B */
            "image/*",                                    /* 0x1C */
            "image/gif",                                  /* 0x1D */
            "image/jpeg",                                 /* 0x1E */
            "image/tiff",                                 /* 0x1F */
            "image/png",                                  /* 0x20 */ //160
            "image/vnd.wap.wbmp",                         /* 0x21 */
            "application/vnd.wap.multipart.*",            /* 0x22 */
            "application/vnd.wap.multipart.mixed",        /* 0x23 */
            "application/vnd.wap.multipart.form-data",    /* 0x24 */
            "application/vnd.wap.multipart.byteranges",   /* 0x25 */
            "application/vnd.wap.multipart.alternative",  /* 0x26 */
            "application/xml",                            /* 0x27 */
            "text/xml",                                   /* 0x28 */
            "application/vnd.wap.wbxml",                  /* 0x29 */
            "application/x-x968-cross-cert",              /* 0x2A */ //170
            "application/x-x968-ca-cert",                 /* 0x2B */
            "application/x-x968-user-cert",               /* 0x2C */    //172
            "text/vnd.wap.si",                            /* 0x2D */
            "application/vnd.wap.sic",                    /* 0x2E */    //174
            "text/vnd.wap.sl",                            /* 0x2F */    //175
            "application/vnd.wap.slc",                    /* 0x30 */
            "text/vnd.wap.co",                            /* 0x31 */
            "application/vnd.wap.coc",                    /* 0x32 */
            "application/vnd.wap.multipart.related",      /* 0x33 */
            "application/vnd.wap.sia",                    /* 0x34 */ //180
            "text/vnd.wap.connectivity-xml",              /* 0x35 */
            "application/vnd.wap.connectivity-wbxml",     /* 0x36 */
            "application/pkcs7-mime",                     /* 0x37 */
            "application/vnd.wap.hashed-certificate",     /* 0x38 */
            "application/vnd.wap.signed-certificate",     /* 0x39 */
            "application/vnd.wap.cert-response",          /* 0x3A */
            "application/xhtml+xml",                      /* 0x3B */
            "application/wml+xml",                        /* 0x3C */
            "text/css",                                   /* 0x3D */
            "application/vnd.wap.mms-message",            /* 0x3E */ //190
            "application/vnd.wap.rollover-certificate",   /* 0x3F */
            "application/vnd.wap.locc+wbxml",             /* 0x40 */
            "application/vnd.wap.loc+xml",                /* 0x41 */
            "application/vnd.syncml.dm+wbxml",            /* 0x42 */
            "application/vnd.syncml.dm+xml",              /* 0x43 */
            "application/vnd.syncml.notification",        /* 0x44 */
            "application/vnd.wap.xhtml+xml",              /* 0x45 */
            "application/vnd.wv.csp.cir",                 /* 0x46 */
            "application/vnd.oma.dd+xml",                 /* 0x47 */
            "application/vnd.oma.drm.message",            /* 0x48 */ //200
            "application/vnd.oma.drm.content",            /* 0x49 */
            "application/vnd.oma.drm.rights+xml",         /* 0x4A */
            "application/vnd.oma.drm.rights+wbxml",       /* 0x4B */
            "application/vnd.wv.csp+xml",                 /* 0x4C */
            "application/vnd.wv.csp+wbxml",               /* 0x4D */
            "application/vnd.syncml.ds.notification",     /* 0x4E */
            "audio/*",                                    /* 0x4F */
            "video/*",                                    /* 0x50 */
            "application/vnd.oma.dd2+xml",                /* 0x51 */
            "application/mikey"                           /* 0x52 */ //300
    };

    public static String[] StringMessages = {
            "1C-06-03-BE-AF-84-8C-82-98-%s-00-8D-90-89-0E-80-%s-00-96-%s-00-8A-82-8E-02-%s-88-05-81-03-09-3A-80-83-%s-00",
            "080605-040B-84000000",
            "00000"
    };

    public static byte[] hexToByteArray(String str) {
        return new BigInteger(str,16).toByteArray();
    }

    /**
     * Turns A string into a Hex string
     * "123456" = 313243343536
     * @param bytes
     * @return
     */
    public static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i <bytes.length; i++) {
            sb.append(String.format("%02X ",bytes[i]));
        }
        return sb.toString();
    }


    public static byte[] createCustomMessage(String payload) {
        return hexToByteArray(payload.replace("-", "").replace(" ", "").trim());
    }

    public static byte[] createSms(String subject, String body, String payload) {

        String testId = "" + System.currentTimeMillis();
        testId = testId.substring(testId.length()-8);

        if(testId.length() % 2 != 0) testId += "1";

        String wapTest = String.format(
                        payload,
                        testId,
                        hex(subject.getBytes()),
                        hex(body.getBytes()),
                        "FFFF",
                        hex("test.com/img.png".getBytes()));

        return hexToByteArray(wapTest.replace("-", "").replace(" ", ""));
    }


}
