package radon.jujutsu_kaisen.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


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
        DISASTER_TIDES,
        SLASH,
        PURE_LOVE,
        LIMITLESS,
        WATER,
        ELECTRICITY,
        RAW_CURSED_ENERGY
    }

    public Classification getClassification() {
        return Classification.NONE;
    }

    // Used for AI
    public abstract boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target);

    public abstract ActivationType getActivationType(LivingEntity owner);
    public abstract void run(LivingEntity owner);

    public List<Trait> getRequirements() {
        return List.of();
    }

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
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if ((this.isTechnique() && !(this instanceof DomainExpansion && cap.hasTrait(Trait.STRONGEST))) && cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) return false;

        for (Trait trait : this.getRequirements()) {
            if (!cap.hasTrait(trait)) return false;
        }
        return true;
    }

    public Status getStatus(LivingEntity owner, boolean cost, boolean charge, boolean cooldown, boolean duration) {
        if (owner.hasEffect(JJKEffects.UNLIMITED_VOID.get())) return Status.FAILURE;
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Status.FAILURE;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!JJKAbilities.getAbilities(owner).contains(this)) {
            return Status.UNUSUABLE;
        }

        if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            if (this.isTechnique() && cap.hasBurnout()) {
                return Status.BURNOUT;
            }

            if ((this.isTechnique() && !(this instanceof DomainExpansion && cap.hasTrait(Trait.STRONGEST))) &&
                    cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                return Status.DOMAIN_AMPLIFICATION;
            }

            if (this.getRealCooldown(owner) > 0) {
                if (!cap.isCooldownDone(this) && !cap.hasToggled(this) && !cap.isChanneling(this)) {
                    return Status.COOLDOWN;
                }
            }

            if (cost) {
                if (!this.checkCost(owner, charge)) {
                    return Status.ENERGY;
                }
            }

            if (cooldown) {
                if (this.getRealCooldown(owner) > 0) {
                    cap.addCooldown(owner, this);
                }
            }
        }

        if (duration) {
            if (this instanceof IDurationable durationable && durationable.getRealDuration(owner) > 0) {
                cap.addDuration(owner, this);
            }
        }
        return Status.SUCCESS;
    }

    public Status checkTriggerable(LivingEntity owner) {
        return this.getStatus(owner, true, true, true, false);
    }

    public Status checkToggleable(LivingEntity owner) {
        return this.getStatus(owner, !((IToggled) this).isPassive(), false, true, true);
    }

    public Status checkChannelable(LivingEntity owner) {
        return this.getStatus(owner, true, false, true, true);
    }

    public Status checkStatus(LivingEntity owner) {
        return this.getStatus(owner, true, true, true, false);
    }

    public boolean checkCost(LivingEntity owner, boolean use) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            float cost = this.getRealCost(owner);

            if (cap.getEnergy() < cost) {
                return false;
            }

            if (use) {
                cap.useEnergy(cost);
            }
        }
        return true;
    }

    public Component getName() {
        ResourceLocation key = JJKAbilities.getKey(this);
        return Component.translatable(String.format("ability.%s.%s", key.getNamespace(), key.getPath()));
    }

    public abstract float getCost(LivingEntity owner);

    public float getRealCost(LivingEntity owner) {
        float cost = this.getCost(owner);

        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return cost;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        CursedTechnique copied = cap.getCurrentCopied();

        if (copied != null && List.of(copied.getAbilities()).contains(this)) {
            cost *= 1.5F;
        }

        if (cap.hasTrait(Trait.SIX_EYES)) {
            cost *= 0.5F;
        }
        return cost;
    }

    public interface IDomainAttack {
        void perform(LivingEntity owner, @Nullable DomainExpansionEntity domain, @Nullable LivingEntity target);
    }

    public interface ITenShadowsAttack {
        void perform(LivingEntity owner, @Nullable LivingEntity target);
    }

    public interface IDurationable {
        default int getDuration() { return 0; }
        default int getRealDuration(LivingEntity owner) {
            int duration = this.getDuration();

            if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return duration;
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (duration > 0) {
                duration = (int) (duration * cap.getGrade().getPower(owner));
            }
            return duration;
        }
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
