package net.ion.message.push.sender;

import junit.framework.TestCase;
import net.ion.framework.util.MapUtil;
import net.ion.message.push.sender.handler.ResponseHandler;

import java.util.Map;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 23.
 */
public class BaseTest extends TestCase{

    public static String INVALID_DEVICE_TOKEN = "a7303190e155b41450e7ce0d7262114b3fa4d2fb2081da2f9786356e973114e7";
    public static String APPLE_DEVICE_TOKEN = "a7303190e155b41450e7ce0d7262114b3fa4d2fb2081da2f9786356e973114e8";
    public static String GOOGLE_DEVICE_TOKEN = "APA91bFz7oI4kgcK4dt12fOPcovNv9hPrhC5Q_eFRnEu79maAsJTgjJ2Jl-n3c3kl7aoWuiCk7F0vT9VTl5GJFzVM1mG0Dxwzm0dpo0amhyp6rwKGe2a8MyFTkaf9CnMOUVYHddkeoWyk3QiOglvjOqbvhXs73Yx2XNelT_AOoHeyRCkYF9ZUY0";
    public static String CGM_API_KEY = "AIzaSyCB3YWgx-2ECRJ0sHIlcMvrb6gOfRIQo88";
    public static String KEY_STORE_PATH = "./talkserver/resource/keystore/toontalk.p12";
    public static String PASSWORD = "toontalk";

    public ResponseHandler<Map<String, Integer>> testResponseHandler = new ResponseHandler<Map<String, Integer>>() {

        private Map<String, Integer> countMap = MapUtil.newMap();
        int failCount = 0;
        int exceptionCount = 0;

        @Override
        public Map<String, Integer> result() {
            countMap.put("failCount", failCount);
            countMap.put("exceptionCount", exceptionCount);
            return countMap;
        }

        @Override
        public void onSuccess(PushResponse response) {

        }

        @Override
        public void onFail(PushResponse response) {
            failCount++;
        }

        @Override
        public void onThrow(String receiver, String token, Throwable t) {
            exceptionCount++;
        }
    };


    public String createDummyMessage(int bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes; i++) {
            builder.append("x");
        }
        return builder.toString();
    }
}
