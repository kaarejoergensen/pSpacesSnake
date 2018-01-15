package org.team08.pspacessnake.Helpers;

import org.jspace.ActualField;
import org.jspace.Space;

import java.util.Random;

public class Utils {
    private final static String ALPHABET = "ABCDEFZHIKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateServerUUID(int length, Space space) throws InterruptedException {
        StringBuilder UID;
        do {
            UID = new StringBuilder();
            Random r = new Random();
            for (int i = 0; i < length; i++) {
                UID.append(ALPHABET.charAt(r.nextInt(ALPHABET.length())));
            }
        } while (space.queryp(new ActualField("UUID"), new ActualField(UID)) != null);
        space.put("UUID", UID.toString());
        return UID.toString();
    }
}