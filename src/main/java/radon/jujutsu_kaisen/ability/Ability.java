package radon.jujutsu_kaisen.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.ArrayUtils;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Ability {
    public enum ActivationType {
        INSTANT,
        TOGGLED,
        CHANNELED
    }

    public enum Status {
        FAILURE,
        UNUSUABLE,
        SUCCESS,
        ENERGY,
        COOLDOWN,
        BURNOUT,
        DOMAIN_AMPLIFICATION,
        SIMPLE_DOMAIN
    }

    // Used for AI
    public abstract boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target);

    public abstract ActivationType getActivationType();
    public abstract void run(LivingEntity owner);

    public int getCooldown() { return 0; }
    public boolean isTechnique() {
        return false;
    }

    private boolean isCopied(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            CursedTechnique technique = cap.getCopied();

            if (technique != null) {
                result.set(ArrayUtils.contains(cap.getCopied().getAbilities(), this));
            }
        });
        return result.get();
    }

    public int getRealCooldown(LivingEntity owner) {
        AtomicInteger cooldown = new AtomicInteger(this.getCooldown());

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasTrait(Trait.SIX_EYES)) {
                cooldown.set(cooldown.get() / 2);
            }
        });
        return cooldown.get();
    }

    public boolean isDisplayed() {
        return true;
    }

    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    protected Status getStatus(LivingEntity owner, boolean cost, boolean charge, boolean cooldown) {
        if (owner.hasEffect(JJKEffects.UNLIMITED_VOID.get())) return Status.FAILURE;

        AtomicReference<Status> result = new AtomicReference<>(Status.SUCCESS);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (!JJKAbilities.getAbilities(owner).contains(this)) {
                result.set(Status.UNUSUABLE);
                return;
            }

            if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
                if (this.isTechnique() && cap.hasBurnout()) {
                    result.set(Status.BURNOUT);
                    return;
                }

                if (this.isTechnique() && cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                    result.set(Status.DOMAIN_AMPLIFICATION);
                    return;
                }

                if (this.getRealCooldown(owner) > 0) {
                    if (!cap.isCooldownDone(this) && !cap.hasToggled(this)) {
                        result.set(Status.COOLDOWN);
                        return;
                    }
                }

                if (cost) {
                    if (!this.checkCost(owner, charge)) {
                        result.set(Status.ENERGY);
                        return;
                    }
                } else if (this instanceof IToggled) {
                    if (((IToggled) this).getRealDuration(owner) > 0) {
                        cap.addDuration(owner, this);
                    }
                }

                if (cooldown) {
                    if (this.getRealCooldown(owner) > 0) {
                        cap.addCooldown(owner, this);
                    }
                }
            }
        });
        return result.get();
    }

    public Status checkTriggerable(LivingEntity owner) {
        if (JJKAbilities.hasToggled(owner, JJKAbilities.SIMPLE_DOMAIN.get())) return Status.SIMPLE_DOMAIN;
        return this.getStatus(owner, true, true, true);
    }

    public Status checkToggleable(LivingEntity owner) {
        return this.getStatus(owner, !((IToggled) this).isPassive(), false, true);
    }

    public Status checkChannelable(LivingEntity owner) {
        return this.getStatus(owner, true, false, true);
    }

    public Status checkStatus(LivingEntity owner) {
        return this.getStatus(owner, true, true, true);
    }

    public boolean checkCost(LivingEntity owner, boolean use) {
        AtomicBoolean result = new AtomicBoolean(true);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
                float cost = this.getRealCost(owner);

                if (cap.getEnergy() < cost) {
                    result.set(false);
                    return;
                }

                if (use) {
                    cap.useEnergy(cost);
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
            if (this.isCopied(owner)) {
                cost.set(cost.get() * 2);
            }
            if (cap.hasTrait(Trait.SIX_EYES)) {
                cost.set(cost.get() / 2);
            }
        });
        return cost.get();
    }

    public interface IDomainAttack {
        void perform(LivingEntity owner, @Nullable DomainExpansionEntity domain, @Nullable LivingEntity target);
    }

    public interface IToggled {
        void onEnabled(LivingEntity owner);
        void onDisabled(LivingEntity owner);

        default int getDuration() { return 0; }
        default int getRealDuration(LivingEntity owner) {
            AtomicReference<Integer> result = new AtomicReference<>(0);

            int duration = this.getDuration();

            if (duration > 0) {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                        result.set((int) (duration * cap.getGrade().getPower())));
            }
            return result.get();
        }

        default boolean isPassive() {
            return false;
        }

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
