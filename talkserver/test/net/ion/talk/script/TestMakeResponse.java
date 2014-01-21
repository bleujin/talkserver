package net.ion.talk.script;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.*;

import java.util.concurrent.ExecutionException;

/**
 * Author: Ryunhee Han
 * Date: 2014. 1. 14.
 */
public class TestMakeResponse extends TestCase{

    public void testParent() throws ExecutionException {
        AbstractBuilder parent = TalkBuilder.createBasic();
        AbstractBuilder child = parent.inner("child");

        assertEquals(parent, child.parent());
    }

    public void testRoot() throws ExecutionException {

        AbstractBuilder builder = TalkBuilder.createBasic();
        AbstractBuilder root = builder.root();
        assertEquals(builder, root);

        AbstractBuilder inner = builder.inner("inner");
        assertEquals(root, inner.root());
    }

    public void testInnerWithSameKey() throws Exception {
        BasicBuilder bbOne = TalkBuilder.createBasic().inner("friends");
        BasicBuilder bbTwo = bbOne.parent().inner("friends");
        assertEquals(bbOne, bbTwo);
    }

    public void testInListWithSameKey() throws ExecutionException {

        ListBuilder lbOne = TalkBuilder.createBasic().inlist("friends");
        ListBuilder lbTwo = lbOne.parent().inlist("friends");
        assertEquals(lbOne, lbTwo);
    }

    public void testArray() throws ExecutionException {
        TalkResponse response = TalkBuilder.createList().add("alex").build();
        assertEquals(new JsonArray().adds("alex"), response.toJsonArray());
    }

    public void testObject() throws ExecutionException {
        TalkResponse response = TalkBuilder.createBasic().property("name", "ryun").build();
        JsonObject object = JsonObject.fromString("{\"name\":\"ryun\"}");
        assertEquals(object, response.toJsonObject());
    }

    public void testObjectInObject() throws Exception {
        TalkResponse response = TalkBuilder.createBasic().property("name", "ryun").inner("friends").property("name", "joshua").build();
        assertEquals("ryun", response.toJsonObject().get("name").getAsString());
        assertEquals("joshua", response.toJsonObject().get("friends").getAsJsonObject().get("name").getAsString());
    }

    public void testArrayInObject() throws ExecutionException {
        TalkResponse response = TalkBuilder.createBasic().property("name", "ryun").inlist("friends").inner("name").property("name","joshua").build();
        assertEquals("joshua", response.toJsonObject().get("friends").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString());
    }

    public void testObjectInArray() throws ExecutionException {
        TalkResponse response = TalkBuilder.createList().add("one").inner("inner").property("name","ryun").build();
        assertEquals("ryun", response.toJsonArray().get(0).getAsJsonObject().get("name").getAsString());
    }

    public void testArrayInArrayInObject() throws ExecutionException {
        TalkResponse response = TalkBuilder.createBasic().property("name", "ryun").inlist("friends").append("alex", "jinsu").inlist("best").add("joshua").build();
        assertEquals("joshua", response.toJsonObject().get("friends").getAsJsonArray().get(0).getAsJsonArray().get(0).getAsString());
    }
    
    

}











