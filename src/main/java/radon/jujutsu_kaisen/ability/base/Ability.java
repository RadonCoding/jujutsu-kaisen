package radon.jujutsu_kaisen.ability.base;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.ability.AbilityDisplayInfo;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.SorcererUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;


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
        BURNOUT
    }

    public enum Classification {
        NONE,
        CURSED_SPEECH,
        SLASHING,
        FIRE,
        WATER,
        PLANTS,
        BLUE,
        LIGHTNING,
        PROJECTION
    }

    public static float getPower(Ability ability, LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getAbilityPower() * (1.0F + ChantHandler.getChant(owner, ability));
    }

    public float getPower(LivingEntity owner) {
        return getPower(this, owner);
    }

    public boolean isScalable(LivingEntity owner) {
        return this.getActivationType(owner) != ActivationType.TOGGLED;
    }

    protected boolean isDisabledFromDA() {
        return false;
    }

    // Used for skill tree
    public boolean isCursedEnergyColor() {
        return false;
    }

    // Used for skill tree
    public boolean isDisplayed(LivingEntity owner) {
        return this.getPointsCost() > 0;
    }

    protected int getPointsCost() {
        return 0;
    }

    public int getRealPointsCost(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        /*if (cap.hasTrait(Trait.SIX_EYES)) {
            return this.getPointsCost() / 2;
        }*/
        return this.getPointsCost();
    }

    public boolean isUnlocked(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.isUnlocked(this);
    }

    public boolean canUnlock(LivingEntity owner) {
        if (this.isBlocked(owner)) return false;
        if (owner instanceof Player player && player.getAbilities().instabuild) return true;
        if (!this.isUnlockable() || this.getPointsCost() == 0) return false;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getPoints() >= this.getRealPointsCost(owner);
    }

    public boolean isUnlockable() {
        return this.getPointsCost() > 0;
    }

    public boolean isBlocked(LivingEntity owner) {
        Ability parent = this.getParent(owner);
        return parent != null && !parent.isUnlocked(owner);
    }

    public Vec2 getDisplayCoordinates() {
        return Vec2.ZERO;
    }

    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        Vec2 coordinates = this.getDisplayCoordinates();
        return new AbilityDisplayInfo(JJKAbilities.getKey(this).getPath(), coordinates.x, coordinates.y);
    }

    @Nullable
    public Ability getParent(LivingEntity owner) {
        return null;
    }

    public Classification getClassification() {
        return Classification.NONE;
    }

    public boolean isMelee() {
        return false;
    }

    // Used for AI
    public abstract boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target);

    public abstract ActivationType getActivationType(LivingEntity owner);

    public abstract void run(LivingEntity owner);

    public List<Ability> getRequirements() {
        return List.of();
    }

    protected int getCooldown() {
        return 0;
    }

    public boolean isTechnique() {
        return true;
    }

    public int getRealCooldown(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (this.isMelee() && cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            return this.getCooldown() / 2;
        }
        return this.getCooldown();
    }

    public MenuType getMenuType() {
        return MenuType.RADIAL;
    }

    public boolean isValid(LivingEntity owner) {
        if (owner instanceof Player player && player.isSpectator()) return false;

        if (this.isUnlockable() && !this.isUnlocked(owner)) return false;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if ((this.isTechnique() && (this.isDisabledFromDA() || !SorcererUtil.isExperienced(cap.getExperience()))) && cap.hasToggled(this) &&
                cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get()))
            return false;

        for (Ability ability : this.getRequirements()) {
            if (!ability.isUnlocked(owner)) return false;
        }
        return true;
    }

    private void addDuration(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (this instanceof IDurationable durationable && durationable.getRealDuration(owner) > 0) {
            cap.addDuration(this);
        }
    }

    public void charge(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            cap.useEnergy(this.getRealCost(owner));

            if (this.getRealCooldown(owner) > 0) {
                cap.addCooldown(this);
            }
        }
    }

    public Status getStatus(LivingEntity owner) {
        if (this != JJKAbilities.WHEEL.get() && owner.hasEffect(JJKEffects.UNLIMITED_VOID.get())) return Status.FAILURE;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!JJKAbilities.getAbilities(owner).contains(this)) {
            return Status.UNUSUABLE;
        }

        if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            if (this.isTechnique() && cap.hasBurnout()) {
                return Status.BURNOUT;
            }

            if (!cap.isCooldownDone(this)) {
                return Status.COOLDOWN;
            }

            if (!this.checkCost(owner)) {
                return Status.ENERGY;
            }
        }
        return Status.SUCCESS;
    }

    public Status isTriggerable(LivingEntity owner) {
        MobEffectInstance instance = owner.getEffect(JJKEffects.STUN.get());

        if (instance != null && instance.getAmplifier() > 0) return Status.FAILURE;

        Status status = this.getStatus(owner);

        if (status == Status.SUCCESS && this.getActivationType(owner) == ActivationType.INSTANT) {
            this.charge(owner);
        }
        this.addDuration(owner);
        return status;
    }

    public Status isStillUsable(LivingEntity owner) {
        if (this instanceof IAttack || this instanceof ICharged) {
            return this.getStatus(owner);
        }

        Status status = this.getStatus(owner);

        if (status == Status.SUCCESS) {
            this.charge(owner);
        }
        return status;
    }

    public boolean checkCost(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            float cost = this.getRealCost(owner);
            return cap.getEnergy() >= cost;
        }
        return true;
    }

    public Component getName() {
        ResourceLocation key = JJKAbilities.getKey(this);
        return Component.translatable(String.format("ability.%s.%s", key.getNamespace(), key.getPath()));
    }

    public abstract float getCost(LivingEntity owner);

    public boolean shouldLog(LivingEntity owner) {
        return this.getActivationType(owner) == ActivationType.TOGGLED;
    }

    public Component getEnableMessage() {
        ResourceLocation key = JJKAbilities.getKey(this);

        if (key == null) return Component.empty();

        return Component.translatable(String.format("ability.%s.%s.enable", key.getNamespace(), key.getPath()));
    }

    public Component getDisableMessage() {
        ResourceLocation key = JJKAbilities.getKey(this);

        if (key == null) return Component.empty();

        return Component.translatable(String.format("ability.%s.%s.disable", key.getNamespace(), key.getPath()));
    }

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
        return Float.parseFloat(String.format(Locale.ROOT, "%.2f", cost * (this.isScalable(owner) ? ChantHandler.getOutput(owner, this) : 1.0F)));
    }

    public interface IDomainAttack {
        default void performEntity(LivingEntity owner, LivingEntity target, DomainExpansionEntity domain) {}
        default void performBlock(LivingEntity owner, DomainExpansionEntity domain, BlockPos pos) {}
    }

    public interface ITenShadowsAttack {
        void perform(LivingEntity owner, @Nullable LivingEntity target);
    }

    public interface IDurationable {
        default int getDuration() {
            return 0;
        }

        default int getRealDuration(LivingEntity owner) {
            int duration = this.getDuration();

            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (duration > 0) {
                duration = (int) (duration * cap.getRealPower());
            }
            return duration;
        }
    }

    public interface IChannelened {
        default void onStop(LivingEntity owner) {}

        default int getCharge(LivingEntity owner) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            return cap.getCharge();
        }
    }

    public interface ICharged extends IChannelened {
        default boolean onRelease(LivingEntity owner) {
            return true;
        }
    }

    public interface IToggled {
        void onEnabled(LivingEntity owner);

        void onDisabled(LivingEntity owner);

        default void applyModifiers(LivingEntity owner) {}

        default void removeModifiers(LivingEntity owner) {}
    }

    public interface IAttack {
        boolean attack(DamageSource source, LivingEntity owner, LivingEntity target);
    }
}
