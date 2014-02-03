package net.ion.message.sms.original;

public class Test {
    public static void main(String[] argvs)
    {
        String member		= "12323123";

        String usercode		= "ioncom2";
        String username		= "I-ON";

        String callphone1 	= "010";
        String callphone2 	= "9139";
        String callphone3 	= "9660";
        String callmessage 	= "안녕하세요";	            // 일부 시스템에서는 한글을 직접 입력하면 깨질 수 있습니다.
        String rdate        = "00000000";		// 미래의 날자와 , 0000000 <-- 즉시전송
        String rtime		= "000000";			// 미래의 시간을 입력하면 자동 예약이 됩니다.
        String reqphone1	= "" ;			    // 회신번호1
        String reqphone2	= "" ;			    // 회신번호2
        String reqphone3	= "";			    // 회신번호3
        String callname		= "test";
        String deptcode		= "8J-N2W-G1";
        String deptname		= "I-ON";
        String result		= "";

    /* 메시지 예약 */
        Message M = new Message();
        result = M.sendMain(member, usercode, username, callphone1, callphone2,
                callphone3,
                callmessage, rdate, rtime, reqphone1, reqphone2,
                reqphone3,
                callname, deptcode, deptname);
        System.out.println(result);
    }
}


