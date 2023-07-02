package radon.jujutsu_kaisen.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.ArrayUtils;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Ability {
    public enum ActivationType {
        INSTANT,
        TOGGLED
    }

    public enum Status {
        FAILURE,
        SUCCESS,
        ENERGY,
        COOLDOWN,
        BURNOUT
    }

    public abstract ActivationType getActivationType();
    public abstract void run(LivingEntity owner);

    public int getCooldown() { return 0; }
    public boolean isTechnique() {
        return false;
    }

    public int getRealCooldown(LivingEntity owner) {
        AtomicInteger cooldown = new AtomicInteger(this.getCooldown());

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getTrait() == Trait.SIX_EYES) {
                cooldown.set(cooldown.get() / 2);
            }
        });
        return cooldown.get();
    }

    public boolean checkCost(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean(true);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
                float cost = this.getRealCost(owner);

                if (cap.getEnergy() < cost) {
                    result.set(false);
                    return;
                }
                cap.useEnergy(cost);
            }
        });
        return result.get();
    }

    public Status checkStatus(LivingEntity owner) {
        AtomicReference<Status> result = new AtomicReference<>(Status.SUCCESS);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (!ArrayUtils.contains(cap.getTechnique().getAbilities(owner), this)) {
                result.set(Status.FAILURE);
                return;
            }

            if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
                if (this.isTechnique() && cap.hasBurnout()) {
                    result.set(Status.BURNOUT);
                    return;
                }

                if (this.getRealCooldown(owner) > 0) {
                    if (!cap.isCooldownDone(this) && !cap.hasToggledAbility(this)) {
                        result.set(Status.COOLDOWN);
                        return;
                    }
                }

                if (!this.checkCost(owner)) {
                    result.set(Status.ENERGY);
                    return;
                }

                if (this.getRealCooldown(owner) > 0) {
                    cap.addCooldown(owner, this);
                }
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
            if (cap.getTrait() == Trait.SIX_EYES) {
                cost.set(cost.get() / 2);
            }
        });
        return cost.get();
    }

    public interface IDomainAttack {
        void perform(LivingEntity owner, @Nullable Entity indirect, @Nullable LivingEntity target);
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
