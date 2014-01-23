package net.ion.message.sms.original;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

public class Message_Common {
    private Socket sendSocket = null;
    private InputStreamReader inputReader = null;
    private BufferedReader buffReader = null;
    private StringTokenizer strTocken = null;
    private String readLine = null;
    private String smsServerIP1 = "messenger.surem.com";
    private String smsServerIP2 = "messenger3.surem.com";
    private int smsServerPort = 8080;
    private Vector vec = null;


    protected void appendToBuffer(StringBuffer strBuff, String fieldData,
                                  int fieldLength) throws UnsupportedEncodingException {
        int fieldDataLength = fieldData.getBytes("euc-kr").length;
        strBuff.append(fieldData);

        for (int inx = fieldLength - fieldDataLength; inx > 0; inx--)
            strBuff.append('\0');
    }

    protected Vector readFile(String filename, String type) {
        int startidx = 0;
        int endidx = 0;
        String temp = "";
        vec = new Vector();
        if (filename.length() == 0) {
            if (type.equals("S")) {
                filename = "sms.txt";
            } else {
                filename = "url.txt";
            }
        }
        try {
            inputReader = new InputStreamReader(new FileInputStream(filename));
            buffReader = new BufferedReader(inputReader);

            while (buffReader.ready()) {
                readLine = buffReader.readLine();
                System.out.println(readLine);
                strTocken = new StringTokenizer(readLine, "\t");
                vec.addElement(strTocken);
            } //end while
        } catch (ArrayIndexOutOfBoundsException ie) {
            System.out.println("ArrayIndexOutOfBoundsException : " + ie);
        } catch (IOException ie) {
            System.out.println("IOException : " + ie);
        } catch (Exception ne) {
            System.out.println("StringIndexOutOfBoundsException : " + ne);
        } //end try

        return vec;
    } //end writeFile

    protected void writeFile(String str) {
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
        java.util.Date currTime = new java.util.Date();
        File file = null;
        FileOutputStream fos = null;
        int c;

        try {
            fos = new FileOutputStream("c:/Temp/" + dfDate.format(currTime) + ".LOG", true);
            fos.write((str).getBytes());
        } catch (IOException e) {
            System.out.println("File Writer Error : " + e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                System.out.println("File Writer Error : " + e);
            }
        }
    }

    protected int int2byte(int i) {

        i = (((i >> 24) & 0xFF) | ((i >> 8) & 0xFF00) | ((i << 8) & 0xFF0000) |
                ((i << 24) & 0xFF000000));

        return i;
    }

    public int Result_int(int[] intArray) {
        int money = 0;
        int temp = 0;

        if (intArray[3] >= 0) {
            for (int i = 0; i < 3; i++) {
                if (intArray[i] < 0) {
                    temp = 256 + intArray[i];
                } else {
                    temp = intArray[i];
                }

                if (i == 0) {
                    temp = temp * 1;
                } else if (i == 1) {
                    temp = temp * 256;
                } else if (i == 2) {
                    temp = temp * 65536;
                } else {
                    temp = temp * 16777216;
                }

                money = money + temp;
            }
        } else {
            for (int i = 0; i < 3; i++) {
                if (intArray[i] < 0) {
                    temp = intArray[i];
                } else {
                    temp = intArray[i] - 256;
                }

                if (i == 0) {
                    temp = temp * 1;
                } else if (i == 1) {
                    temp = (temp + 1) * 256;
                } else if (i == 2) {
                    temp = (temp + 1) * 65536;
                } else {
                    temp = (temp + 1) * 16777216;
                }

                money = money + temp;
            }


        }

        return money;
    }


    protected Socket Sconnect() throws SocketException {

        try {
            sendSocket = new Socket(smsServerIP1, smsServerPort); //클라이언트측 소캣 생성
        } catch (IOException e) {
            try {
                sendSocket = new Socket(smsServerIP2, smsServerPort); //클라이언트측 소캣 생성
            } catch (IOException se) {
                throw new SocketException(se.getMessage());
            }
        }

        return sendSocket;
    }

}

