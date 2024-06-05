package radon.jujutsu_kaisen.client.chant;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import java.util.*;

public class ClientChantHandler {
    private static final List<String> messages = new ArrayList<>();

    public static void add(String chant) {
        messages.add(chant);
    }

    public static void remove(String chant) {
        messages.remove(chant);
    }

    public static void remove() {
        messages.clear();
    }

    public static List<String> getMessages() {
        return messages;
    }
}
