package radon.jujutsu_kaisen.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;

import java.util.concurrent.atomic.AtomicReference;

public abstract class Ability {
    public enum ActivationType {
        INSTANT,
        TOGGLED
    }

    public enum Status {
        SUCCESS,
        ENERGY,
        COOLDOWN
    }

    public abstract ActivationType getActivationType();
    public abstract void run(LivingEntity owner);

    public int getCooldown() { return 0; }

    public Status checkStatus(LivingEntity owner) {
        AtomicReference<Status> result = new AtomicReference<>(Status.SUCCESS);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
                float cost = this.getCost(owner);

                if (cap.getEnergy() < cost) {
                    result.set(Status.ENERGY);
                    return;
                }

                if (this.getCooldown() > 0) {
                    if (!cap.isCooldownDone(this) && !cap.hasToggledAbility(this)) {
                        result.set(Status.COOLDOWN);
                        return;
                    }
                }
                cap.addCooldown(this);
                cap.useEnergy(cost);
            }
        });
        return result.get();
    }


    public Component getName() {
        ResourceLocation key = JujutsuAbilities.getKey(this);
        return Component.translatable(String.format("ability.%s.%s", key.getNamespace(), key.getPath()));
    }

    public abstract float getCost(LivingEntity owner);

    public interface IToggled {
        void onEnabled(LivingEntity owner);
        void onDisabled(LivingEntity owner);

        default Component getEnableMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = JujutsuAbilities.getKey(ability);
            return Component.translatable(String.format("ability.%s.%s.enable", key.getNamespace(), key.getPath()));
        }

        default Component getDisableMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = JujutsuAbilities.getKey(ability);
            return Component.translatable(String.format("ability.%s.%s.disable", key.getNamespace(), key.getPath()));
        }
    }
}
