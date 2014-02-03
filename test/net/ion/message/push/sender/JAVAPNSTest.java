package net.ion.message.push.sender;

import java.util.List;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.Payload;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

import org.json.JSONException;

public class JAVAPNSTest extends BaseTest{

    private PushedNotification sendPayload(Payload payload) throws KeystoreException, CommunicationException, InvalidDeviceTokenFormatException {

        PushNotificationManager pushManager = new PushNotificationManager();

        try {
            AppleNotificationServer server = new AppleNotificationServerBasicImpl(KEY_STORE_PATH, PASSWORD, true);
            pushManager.initializeConnection(server);

            BasicDevice device = new BasicDevice(APPLE_DEVICE_TOKEN);
            BasicDevice.validateTokenFormat(device.getToken());
            PushedNotification notification = pushManager.sendNotification(device, payload, true);

            return notification;
        } finally {
            pushManager.stopConnection();
        }
    }

    public void testSendTest() throws InvalidDeviceTokenFormatException, CommunicationException, KeystoreException {
        PushedNotification pushedNotification = sendPayload(PushNotificationPayload.test());
        printPushedNotification(pushedNotification);
    }

    public void testFeedback() throws CommunicationException, KeystoreException {
        List<Device> deviceList = Push.feedback(KEY_STORE_PATH, PASSWORD, true);
        for (Device device : deviceList) {
            System.out.println(device.getToken());
        }
    }

    public void testSendMessage_withSoundAndBadge() throws JSONException, CommunicationException, KeystoreException, InvalidDeviceTokenFormatException {

        PushNotificationPayload message = PushNotificationPayload.complex();
        message.addAlert("안녕");
        message.addSound("default");
        message.addBadge(100);

        PushedNotification pushed = sendPayload(message);
        printPushedNotification(pushed);
    }

    private void printPushedNotification(PushedNotification pushed) {
        System.out.println("  " + pushed.toString());
    }
}
