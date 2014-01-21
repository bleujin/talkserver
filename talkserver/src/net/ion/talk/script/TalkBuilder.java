package net.ion.talk.script;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public class TalkBuilder {

    private TalkBuilder() {
    }

    public static TalkBuilder create(){
        return new TalkBuilder();
    }

    public static BasicBuilder createBasic() {
        return new BasicBuilder();
    }

    public static ListBuilder createList(){
        return new ListBuilder();
    }
}
