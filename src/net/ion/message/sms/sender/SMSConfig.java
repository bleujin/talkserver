package net.ion.message.sms.sender;

import net.ion.radon.aclient.NewClient;

public class SMSConfig {

    enum TargetLoc {
    	Domestic {
			public String deptCode() {
				return "8J-N2W-G1";
			}
			public String userCode() {
				return "ioncom2";
			}
			public String handlerURL() {
				return "https://toll.surem.com:440/message/direct_call_sms_return_post.asp";
			}

			public String senderPhoneKey() {
				return "group_name";
			}
			
			public boolean isDomestic() {
				return true ;
			}
    		
    	}, International {
			public String deptCode() {
				return "JM-BWB-P6";
			}
			public String userCode() {
				return "ioncom";
			}
			public String handlerURL() {
				return "https://toll.surem.com:440/message/direct_INTL_return_post.asp";
			}
			public String senderPhoneKey() {
				return "group_name";
			}
			public boolean isDomestic() {
				return false ;
			}
    		
    	} ;
    	
    	public abstract String deptCode() ;
    	public abstract String userCode() ;
    	public abstract String handlerURL() ;
    	public abstract String senderPhoneKey() ;
    	public abstract boolean isDomestic() ;
    	public String callBackURL(){
    		return "http://127.0.0.1/callback" ;
    	}
    	
    }
    
    public SMSConfig() {
    }
    
    public TargetLoc target(String receiverPhoneNo){
    	return receiverPhoneNo.startsWith("+") ? TargetLoc.International : TargetLoc.Domestic ;
    }

    public SMSSender createSender(NewClient client) {
        return new SMSSender(client, this);
    }

}
