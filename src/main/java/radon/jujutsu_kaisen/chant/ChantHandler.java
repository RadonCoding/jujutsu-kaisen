package radon.jujutsu_kaisen.chant;


import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.client.chant.ClientChantHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChantHandler {
    public static boolean isChanted(LivingEntity owner, Ability ability) {
        return getChant(owner, ability) > 0.0F;
    }

    public static float getOutput(LivingEntity owner, Ability ability) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISorcererData data = cap.getSorcererData();

        if (data == null) return 0.0F;

        return data.getOutput() + getChant(owner, ability);
    }

    public static float getChant(LivingEntity owner, Ability ability) {
        List<String> messages = owner.level().isClientSide ? ClientChantHandler.getMessages() : ServerChantHandler.getMessages(owner);

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        IChantData data = cap.getChantData();

        List<String> chants = new ArrayList<>(data.getFirstChants(ability));

        if (chants.isEmpty()) return 0.0F;

        if (messages.isEmpty()) return 0.0F;

        int count = 0;
        int length = 0;

        int index = 0;

        for (String chant : messages) {
            if (index == chants.size()) break;
            if (!chant.equals(chants.get(index))) continue;

            index++;

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

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return null;

        IChantData data = cap.getChantData();

        Ability ability = data.getAbility(messages.getLast());

        if (ability != null) {
            List<String> chants = new ArrayList<>(data.getFirstChants(ability));

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
