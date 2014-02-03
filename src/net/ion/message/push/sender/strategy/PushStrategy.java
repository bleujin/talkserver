package net.ion.message.push.sender.strategy;

import net.ion.message.push.sender.Vendor;

public interface PushStrategy {

    public int getBadge();
    public String getSound();

    public int getTimeToLive();
    public String getCollapseKey();
    public boolean getDelayWhenIdle();

    public Vendor vender(String targetId) ;
    public String deviceId(String targetId) ;

}
