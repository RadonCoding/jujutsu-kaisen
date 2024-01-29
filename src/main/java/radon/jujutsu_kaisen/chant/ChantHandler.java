package radon.jujutsu_kaisen.chant;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.chant.ClientChantHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.*;

public class ChantHandler {
    public static boolean isChanted(LivingEntity owner, Ability ability) {
        return getChant(owner, ability) > 0.0F;
    }

    public static float getOutput(LivingEntity owner, Ability ability) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getOutput() + getChant(owner, ability);
    }

    public static float getChant(LivingEntity owner, Ability ability) {
        List<String> messages = owner.level().isClientSide ? ClientChantHandler.getMessages() : ServerChantHandler.getMessages(owner);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        Set<String> chants = cap.getFirstChants(ability);

        if (chants.isEmpty()) return 0.0F;

        if (messages.isEmpty()) return 0.0F;

        int count = 0;
        int length = 0;

        Iterator<String> iter = chants.iterator();

        for (String chant : messages) {
            if (!iter.hasNext() || !chant.equals(iter.next())) break;

            count++;
            length += chant.length();
        }
        float countFactor = (float) count / ConfigHolder.SERVER.maximumChantCount.get();
        float lengthFactor = (float) length / (ConfigHolder.SERVER.maximumChantCount.get() * ConfigHolder.SERVER.maximumChantLength.get());
        return (0.75F * countFactor) + (0.5F * lengthFactor);
    }

    @Nullable
    public static String next(LivingEntity owner) {
        List<String> messages = owner.level().isClientSide ? ClientChantHandler.getMessages() : ServerChantHandler.getMessages(owner);

        if (messages.isEmpty()) return null;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        Ability ability = cap.getAbility(messages.get(messages.size() - 1));

        if (ability != null) {
            List<String> chants = new ArrayList<>(cap.getFirstChants(ability));

            if (chants.size() == 1) return null;

            int index = 0;

            Iterator<String> iter = chants.iterator();

            for (String chant : messages) {
                if (!iter.hasNext() || !chant.equals(iter.next())) break;

                index++;
            }
            return index < chants.size() ? chants.get(index) : null;
        }
        return null;
    }
}
