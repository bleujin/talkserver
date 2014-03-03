package net.ion.message.sms.original;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Message extends Message_Common {

	private Socket sendSocket = null;
	private BufferedInputStream bis = null;
	private BufferedOutputStream bos = null;
	private DataOutputStream dos = null;
	private String[] smsFieldData = null;
	private String[] urlFieldData = null;
	private String smsResponse = null;
	private int[] smsFieldLength;
	private int[] urlFieldLength;

	/* 기본 적으로 받아야 하는 값들 */
	private String member; // Client측 key일련번호
	private String usercode = null; // 사용자 발신 코드
	private String username = null; // 사용자명
	private String callphone1 = null; // 호출 번호 #1
	private String callphone2 = null; // 호출 번호 #2
	private String callphone3 = null; // 호출 번호 #3
	private String callmessage = null; // 호출 메시지
	private String rdate = null; // 메시지 전송 예약일자
	private String rtime = null; // 메시지 전송 예약시간
	private String reqphone1 = null; // 회신 번호#1
	private String reqphone2 = null; // 회신 번호#2
	private String reqphone3 = null; // 회신 번호#3
	private String callname = null;
	private String deptcode = null; // 회사 코드
	private String deptname = null; // 회사명
	private String callurl = null; // CallUrl
	private String status = null;

	/*
	 * 단건 문자 메시지 호출 하는 함수
	 */
	public String sendMain(String member, String usercode, String username, String callphone1, String callphone2, String callphone3, String callmessage, String rdate, String rtime, String reqphone1, String reqphone2, String reqphone3, String callname, String deptcode, String deptname) {
		// 파라미터로 받은값을 위의 변수에 setting
		this.member = member;
		this.usercode = usercode;
		this.username = username;
		this.callphone1 = callphone1;
		this.callphone2 = callphone2;
		this.callphone3 = callphone3;
		this.callmessage = callmessage;
		this.rdate = rdate;
		this.rtime = rtime;
		this.reqphone1 = reqphone1;
		this.reqphone2 = reqphone2;
		this.reqphone3 = reqphone3;
		this.callname = callname;
		this.deptcode = deptcode;
		this.deptname = deptname;

		// smsFieldData = new String[ smsFieldName.length ] ;
		smsFieldData = new String[22];
		setSMSFieldData(smsFieldData); // SMS Packet 의 각 필드를 setting 하는 메소드

		try {
			sendSocket = Sconnect(); // 클라이언트측 소캣 생성

			bos = new BufferedOutputStream(sendSocket.getOutputStream());
			bis = new BufferedInputStream(sendSocket.getInputStream());
			dos = new DataOutputStream(bos);

			smsFieldLength = new int[22];
			setSMSFieldInfo(smsFieldLength);

			// ///서버에 SMS request 를 send..../
			sendSMSRequest(dos, smsFieldData);

			// ////서버로 부터 응답을 받아옴 /////
			smsResponse = reciveSMSResponse(bis);

		} catch (IOException e) {
			System.out.println("sendMain() 부분 예외 : " + e);
		} finally {
			try {
				if (dos != null)
					dos.close();
				if (bos != null)
					bos.close();
				if (bis != null)
					bis.close();
				if (sendSocket != null)
					sendSocket.close();
			} catch (Exception e) {
				System.out.println("sendMain() 의 스트림과 소켓객체 반납부분 예외 : " + e);
			}
		}
		System.out.println("sendmain succ");
		return smsResponse;
	}

	/*
	 * URL 전송 요청
	 */

	public String sendURLMain(String member, String usercode, String username, String callphone1, String callphone2, String callphone3, String callmessage, String rdate, String rtime, String reqphone1, String reqphone2, String reqphone3, String callname, String deptcode, String deptname,
			String callurl) {
		// 파라미터로 받은값을 위의 변수에 setting
		this.member = member;
		this.usercode = usercode;
		this.username = username;
		this.callphone1 = callphone1;
		this.callphone2 = callphone2;
		this.callphone3 = callphone3;
		this.callmessage = callmessage;
		this.rdate = rdate;
		this.rtime = rtime;
		this.reqphone1 = reqphone1;
		this.reqphone2 = reqphone2;
		this.reqphone3 = reqphone3;
		this.callname = callname;
		this.deptcode = deptcode;
		this.deptname = deptname;
		this.callurl = callurl;

		// smsFieldData = new String[ smsFieldName.length ] ;
		urlFieldData = new String[25];
		setURLFieldData(urlFieldData); // SMS Packet 의 각 필드를 setting 하는 메소드

		try {
			sendSocket = Sconnect(); // 클라이언트측 소캣 생성

			/******** 스트림 생성 ***********************/
			bos = new BufferedOutputStream(sendSocket.getOutputStream());
			bis = new BufferedInputStream(sendSocket.getInputStream());

			dos = new DataOutputStream(bos);

			urlFieldLength = new int[25];
			setURLFieldInfo(urlFieldLength);

			/**** 서버에 SMS request 를 send.... ****/
			sendURLRequest(dos, urlFieldData);

			/**** 서버로 부터 응답을 받아옴 ********/
			smsResponse = reciveURLResponse(bis);

		} catch (IOException e) {
			System.out.println("sendURLMain() 부분 예외 : " + e);
		} finally {
			try {
				if (dos != null)
					dos.close();
				if (bos != null)
					bos.close();
				if (bis != null)
					bis.close();
				if (sendSocket != null)
					sendSocket.close();
			} catch (Exception e) {
				System.out.println("sendURLMain() 의 스트림과 소켓객체 반납부분 예외 : " + e);
			}
		}
		return smsResponse;
	}

	/*
	 * SMS 패킷 정의시 각 필드의 크기
	 */
	private void setSMSFieldInfo(int[] smsFieldLength) {
		smsFieldLength[0] = 1; // char
		smsFieldLength[1] = 1; // char
		smsFieldLength[2] = 10; // char[10]
		smsFieldLength[3] = 8; // char[8]
		smsFieldLength[4] = 30; // char[30]
		smsFieldLength[5] = 16; // char[16]
		smsFieldLength[6] = 12; // char[12]
		smsFieldLength[7] = 16; // char[16]
		smsFieldLength[8] = 1; // char
		smsFieldLength[9] = 4; // char[4]
		smsFieldLength[10] = 4; // char[4]
		smsFieldLength[11] = 4; // char[4]
		smsFieldLength[12] = 120; // char[120]
		smsFieldLength[13] = 10; // char[10]
		smsFieldLength[14] = 8; // char[8]
		smsFieldLength[15] = 3; // char[3]
		smsFieldLength[16] = 8; // long
		smsFieldLength[17] = 4; // char[4]
		smsFieldLength[18] = 4; // char[4]
		smsFieldLength[19] = 4; // char[4]
		smsFieldLength[20] = 32; // char[32]
		smsFieldLength[21] = 32; // char[32]
	}

	/*
	 * SMS 패킷 값을 지정하는 함수
	 */
	private void setSMSFieldData(String[] smsFieldData) {

		smsFieldData[0] = "B"; // Command
		smsFieldData[1] = "O"; // Type
		smsFieldData[2] = rdate; // Date
		smsFieldData[3] = rtime; // Time
		smsFieldData[4] = usercode; // UserCode
		smsFieldData[5] = username; // UserName
		smsFieldData[6] = deptcode; // DeptCode
		smsFieldData[7] = deptname; // DeptName

		if (rdate.equals("00000000")) {
			smsFieldData[8] = ""; // Status
		} else {
			smsFieldData[8] = "R"; // Status
		}

		smsFieldData[9] = callphone1; // CallPhone1 받을 사람 번호
		smsFieldData[10] = callphone2; // CallPhone2
		smsFieldData[11] = callphone3; // CallPhone3
		smsFieldData[12] = callmessage; // CallMessage
		smsFieldData[13] = rdate; // Rdate
		smsFieldData[14] = rtime; // Rtime
		smsFieldData[15] = ""; // Dummy
		smsFieldData[16] = member; // 클라이언트측 일련 번호
		smsFieldData[17] = reqphone1; // reqphone1 보낸 사람 번호
		smsFieldData[18] = reqphone2; // reqphone2
		smsFieldData[19] = reqphone3; // reqphone3
		smsFieldData[20] = callname; // reqname
		smsFieldData[21] = ""; // reserved
	}

	/*
	 * 전송 모듈
	 */
	private void sendSMSRequest(DataOutputStream dos, String[] smsFieldData) throws IOException {

		StringBuffer strBuff = new StringBuffer();
		byte[] byteBuff = null;

		try {
			for (int inx = 0; inx < 16; inx++) {
				appendToBuffer(strBuff, smsFieldData[inx], smsFieldLength[inx]);
				System.out.println(inx + " : " + new String(strBuff.toString().getBytes()));
			}

			byteBuff = strBuff.toString().getBytes("euc-kr");
			dos.write(byteBuff, 0, byteBuff.length);
			strBuff.delete(0, strBuff.length());

			dos.writeInt(int2byte(Integer.parseInt(smsFieldData[16])));

			for (int inx = 17; inx < 22; inx++) {
				appendToBuffer(strBuff, smsFieldData[inx], smsFieldLength[inx]);
				System.out.println(inx + " : " + new String(strBuff.toString().getBytes()));
			}

			byteBuff = strBuff.toString().getBytes();
			dos.write(byteBuff, 0, byteBuff.length);
			dos.flush();
		} catch (IOException e) {
			throw e;
		}
	}

	/*
	 * 전송 후 결과를 받는 부분
	 */

	private String reciveSMSResponse(BufferedInputStream bis) {
		byte[] byteBuff = new byte[1024];
		String strReceivedMsg = null;
		int[] intArray = new int[4];

		try {
			int iReadCount = bis.read(byteBuff);
			if (iReadCount > 0) {
				status = new String(byteBuff, 94, 1);

				/* 로그 화일에 기록 */
				// writeFile("command=[" + smsFieldData[0] + "]	usercode=[" +
				// smsFieldData[4] + "]	username=[" + smsFieldData[5] +
				// "]	callphone1=[" + smsFieldData[9] + "]	callphone2=[" +
				// smsFieldData[10] + "]	callphone3=[" + smsFieldData[11] +
				// "]	callmessage=[" + smsFieldData[12] + "]	rdate=[" +
				// smsFieldData[13] + "]	rtime=[" + smsFieldData[14] +
				// "]	reqphone1=[" + smsFieldData[17] + "]	reqphone2=[" +
				// smsFieldData[18] + "]	reqphone3=[" + smsFieldData[18] +
				// "]	status=[" + status + "]\n");
			}
		} catch (IOException e) {
			System.out.println("서버로 결과 수신 실패 : " + e);
		}
		return status;
	}

	// SMS Packet 각 필드의 Length를 Setting

	private void setURLFieldInfo(int[] urlFieldLength) {
		urlFieldLength[0] = 1; // char
		urlFieldLength[1] = 1; // char
		urlFieldLength[2] = 10; // char[10]
		urlFieldLength[3] = 8; // char[8]
		urlFieldLength[4] = 30; // char[30]
		urlFieldLength[5] = 16; // char[16]
		urlFieldLength[6] = 12; // char[12]
		urlFieldLength[7] = 16; // char[16]
		urlFieldLength[8] = 1; // char
		urlFieldLength[9] = 4; // char[4]
		urlFieldLength[10] = 4; // char[4]
		urlFieldLength[11] = 4; // char[4]
		urlFieldLength[12] = 80; // char[80]
		urlFieldLength[13] = 62; // char[62]
		urlFieldLength[14] = 10; // char[10]
		urlFieldLength[15] = 8; // char[8]
		urlFieldLength[16] = 4; // char[4]
		urlFieldLength[17] = 4; // char[4]
		urlFieldLength[18] = 4; // char[4]
		urlFieldLength[19] = 32; // char[32]
		urlFieldLength[20] = 1; // char
		urlFieldLength[21] = 4; // char[4]
		urlFieldLength[22] = 4; // char[4]
		urlFieldLength[23] = 4; // char[4]
		urlFieldLength[24] = 4; // char[4]
	}

	// SMS Packet의 각 필드를 Setting
	private void setURLFieldData(String[] urlFieldData) {
		urlFieldData[0] = "I"; // Command
		urlFieldData[1] = "O"; // Type
		urlFieldData[2] = rdate; // Date
		urlFieldData[3] = rtime; // Time
		urlFieldData[4] = usercode; // UserCode
		urlFieldData[5] = username; // UserName
		urlFieldData[6] = deptcode; // DeptCode
		urlFieldData[7] = deptname; // DeptName
		if (rdate.equals("00000000")) {
			urlFieldData[8] = ""; // Status
		} else {
			urlFieldData[8] = "R"; // Status
		}

		// CallPhone처리 '011-123-4567' --> '011', '123', 4567'로 분리
		urlFieldData[9] = callphone1; // CallPhone1
		urlFieldData[10] = callphone2; // CallPhone2
		urlFieldData[11] = callphone3; // CallPhone3

		urlFieldData[12] = callmessage; // CallMessage
		urlFieldData[13] = callurl; // CallURL
		urlFieldData[14] = rdate; // Rdate
		urlFieldData[15] = rtime; // Rtime

		// ReqPhone처리 '011-123-4567' --> '011', '123', 4567'로 분리
		urlFieldData[16] = reqphone1; // reqphone1
		urlFieldData[17] = reqphone2; // reqphone2
		urlFieldData[18] = reqphone3; // reqphone3
		urlFieldData[19] = callname; // reqname
		urlFieldData[20] = ""; // Dummy
		urlFieldData[21] = member; // 클라이언트측 일련 번호

		urlFieldData[22] = "0"; // TotalPrice
		urlFieldData[23] = "0"; // CallPrice
		urlFieldData[24] = "0"; // reserved
	}

	// SureM.COM서버에 URL request 를 send.....
	private void sendURLRequest(DataOutputStream dos, String[] urlFieldData) throws IOException {

		StringBuffer strBuff = new StringBuffer();
		byte[] byteBuff = null;

		try {
			for (int inx = 0; inx < 21; inx++) {
				appendToBuffer(strBuff, urlFieldData[inx], urlFieldLength[inx]);
				System.out.println(inx + " : " + strBuff.toString().getBytes());
			}

			byteBuff = strBuff.toString().getBytes();
			dos.write(byteBuff, 0, byteBuff.length);
			strBuff.delete(0, strBuff.length());

			for (int inx = 21; inx <= 24; inx++) {
				dos.writeInt(int2byte(Integer.parseInt(urlFieldData[inx])));
				System.out.println(inx + " : " + urlFieldData[inx]);
			}
			// byteBuff = strBuff.toString().getBytes() ;
			// dos.write( byteBuff, 0 , byteBuff.length ) ;
			dos.flush();
		} catch (IOException e) {
			throw e;
		}
	}

	// SureM.COM 서버로 부터 SMS response를 receive...
	private String reciveURLResponse(BufferedInputStream bis) {
		byte[] byteBuff = new byte[1024];
		String strReceivedMsg = null;

		try {
			int iReadCount = bis.read(byteBuff);
			if (iReadCount > 0) {
				// strReceivedMsg = new String( byteBuff, 0 , iReadCount ) ;
				status = new String(byteBuff, 94, 1);
				writeFile("command=[" + urlFieldData[0] + "]	usercode=[" + urlFieldData[4] + "]	username=[" + urlFieldData[5] + "]	callphone1=[" + urlFieldData[9] + "]	callphone2=[" + urlFieldData[10] + "]	callphone3=[" + urlFieldData[11] + "]	callmessage=[" + urlFieldData[12] + "]	callurl=["
						+ urlFieldData[13] + "]	rdate=[" + urlFieldData[14] + "]	rtime=[" + urlFieldData[15] + "]	reqphone1=[" + urlFieldData[16] + "]	reqphone2=[" + urlFieldData[17] + "]	reqphone3=[" + urlFieldData[19] + "]	status=[" + status + "]\n");
			}
		} catch (IOException e) {
			System.out.println("서버로 부터 응답을 받아오는데 실패 : " + e);
		}

		return status;
	}
}
