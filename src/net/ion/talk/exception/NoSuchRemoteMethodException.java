package net.ion.talk.exception;

public class NoSuchRemoteMethodException extends RuntimeException {
    public NoSuchRemoteMethodException(String methodDescription) {
        super("No such remote method: " + methodDescription);
    }
}
