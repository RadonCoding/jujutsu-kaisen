package radon.jujutsu_kaisen.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.SpecialTrait;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Ability {
    public enum ActivationType {
        INSTANT,
        TOGGLED
    }

    public enum Status {
        SUCCESS,
        ENERGY,
        COOLDOWN,
        BURNOUT
    }

    public abstract ActivationType getActivationType();
    public abstract void run(LivingEntity owner);

    public int getCooldown() { return 0; }

    public int getRealCooldown(LivingEntity owner) {
        AtomicInteger cooldown = new AtomicInteger(this.getCooldown());

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getTrait() == SpecialTrait.SIX_EYES) {
                cooldown.set(cooldown.get() / 2);
            }
        });
        return cooldown.get();
    }

    public Status checkStatus(LivingEntity owner) {
        AtomicReference<Status> result = new AtomicReference<>(Status.SUCCESS);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
                float cost = this.getRealCost(owner);

                if (cap.getEnergy() < cost) {
                    result.set(Status.ENERGY);
                    return;
                }

                if (cap.hasBurnout()) {
                    result.set(Status.BURNOUT);
                    return;
                }

                if (this.getRealCooldown(owner) > 0) {
                    if (!cap.isCooldownDone(this) && !cap.hasToggledAbility(this)) {
                        result.set(Status.COOLDOWN);
                        return;
                    }
                    cap.addCooldown(owner, this);
                }
                cap.useEnergy(cost);
            }
        });
        return result.get();
    }


    public Component getName() {
        ResourceLocation key = JJKAbilities.getKey(this);
        return Component.translatable(String.format("ability.%s.%s", key.getNamespace(), key.getPath()));
    }

    public abstract float getCost(LivingEntity owner);

    public float getRealCost(LivingEntity owner) {
        AtomicReference<Float> cost = new AtomicReference<>(this.getCost(owner));

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getTrait() == SpecialTrait.SIX_EYES) {
                cost.set(cost.get() * 0.3F);
            }
        });
        return cost.get();
    }

    public interface IToggled {
        void onEnabled(LivingEntity owner);
        void onDisabled(LivingEntity owner);

        default Component getEnableMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = JJKAbilities.getKey(ability);
            return Component.translatable(String.format("ability.%s.%s.enable", key.getNamespace(), key.getPath()));
        }

        default Component getDisableMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = JJKAbilities.getKey(ability);
            return Component.translatable(String.format("ability.%s.%s.disable", key.getNamespace(), key.getPath()));
        }
    }
}
