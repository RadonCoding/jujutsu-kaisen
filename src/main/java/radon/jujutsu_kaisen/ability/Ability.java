package radon.jujutsu_kaisen.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
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

    public enum Classification {
        NONE,
        MELEE,
        DISASTER_FLAMES,
        SLASH,
        PURE_LOVE,
        LIMITLESS
    }

    public Classification getClassification() {
        return Classification.NONE;
    }

    // Used for AI
    public abstract boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target);

    public abstract ActivationType getActivationType(LivingEntity owner);
    public abstract void run(LivingEntity owner);

    public int getCooldown() { return 0; }
    public boolean isTechnique() {
        return false;
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

    public DisplayType getDisplayType() {
        return DisplayType.RADIAL;
    }

    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    public Status getStatus(LivingEntity owner, boolean cost, boolean charge, boolean cooldown, boolean duration) {
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

                if ((this.isTechnique() && !(this instanceof DomainExpansion && cap.hasTrait(Trait.STRONGEST))) &&
                        cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
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
                }

                if (cooldown) {
                    if (this.getRealCooldown(owner) > 0) {
                        cap.addCooldown(owner, this);
                    }
                }
            }

            if (duration) {
                if (((IToggled) this).getRealDuration(owner) > 0) {
                    cap.addDuration(owner, this);
                }
            }
        });
        return result.get();
    }

    public Status checkTriggerable(LivingEntity owner) {
        if (JJKAbilities.hasToggled(owner, JJKAbilities.SIMPLE_DOMAIN.get())) return Status.SIMPLE_DOMAIN;
        return this.getStatus(owner, true, true, true, false);
    }

    public Status checkToggleable(LivingEntity owner) {
        return this.getStatus(owner, !((IToggled) this).isPassive(), false, true, true);
    }

    public Status checkChannelable(LivingEntity owner) {
        return this.getStatus(owner, true, false, true, false);
    }

    public Status checkStatus(LivingEntity owner) {
        return this.getStatus(owner, true, true, true, false);
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
            if (cap.hasTrait(Trait.SIX_EYES)) {
                cost.set(cost.get() / 2);
            }
        });
        return cost.get();
    }

    public interface IDomainAttack {
        void perform(LivingEntity owner, @Nullable DomainExpansionEntity domain, @Nullable LivingEntity target);
    }

    public interface ITenShadowsAttack {
        void perform(LivingEntity owner, @Nullable LivingEntity target);
    }

    public interface IChannelened {
        void onRelease(LivingEntity owner, int charge);
    }

    public interface IToggled {
        void onEnabled(LivingEntity owner);
        void onDisabled(LivingEntity owner);

        default boolean shouldLog() {
            return true;
        }

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
