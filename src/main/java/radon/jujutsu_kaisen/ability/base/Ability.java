package radon.jujutsu_kaisen.ability.base;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;

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
        THROAT
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
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISorcererData data = cap.getSorcererData();

        if (ability.isScalable(owner)) {
            if (ability.isChantable()) {
                return data.getAbilityOutput(ability);
            }
            return data.getAbilityOutput();
        }
        return data.getBaseOutput();
    }

    public float getPower(LivingEntity owner) {
        return getPower(this, owner);
    }

    // Whether or not the ability scales off of the output of the caster
    public boolean isScalable(LivingEntity owner) {
        return this.getActivationType(owner) != ActivationType.TOGGLED;
    }

    // Whether or not the ability is chantable
    public boolean isChantable() {
        return this.isTechnique();
    }

    protected boolean isNotDisabledFromDA() {
        return false;
    }

    protected boolean isNotDisabledFromUV() {
        return false;
    }

    // Used for skill tree
    public boolean isCursedEnergyColor() {
        return false;
    }

    // Used for skill tree
    public boolean isDisplayed(LivingEntity owner) {
        if (this.isTechnique()) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return false;

            ISorcererData data = cap.getSorcererData();

            if (!data.hasTechnique(JJKCursedTechniques.getTechnique(this))) {
                return false;
            }
        }
        return this.getPointsCost() > 0;
    }

    public boolean isUsableInSpectator() {
        return false;
    }

    protected int getPointsCost() {
        return 0;
    }

    public int getRealPointsCost(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISorcererData data = cap.getSorcererData();

        if (data.hasTrait(Trait.SIX_EYES)) {
            return this.getPointsCost() / 2;
        }
        return this.getPointsCost();
    }

    public boolean isUnlocked(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();
        return data.isUnlocked(this);
    }

    public boolean canUnlock(LivingEntity owner) {
        if (this.isBlocked(owner)) return false;
        if (owner instanceof Player player && player.getAbilities().instabuild) return true;
        if (!this.isUnlockable() || this.getPointsCost() == 0) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getAbilityPoints() >= this.getRealPointsCost(owner);
    }

    public boolean isUnlockable() {
        return this.getPointsCost() > 0;
    }

    public boolean isBlocked(LivingEntity owner) {
        Ability parent = this.getParent(owner);
        return parent != null && !parent.isUnlocked(owner);
    }

    public ResourceLocation getIcon(LivingEntity owner) {
        return new ResourceLocation(JujutsuKaisen.MOD_ID, String.format("textures/ability/%s.png", JJKAbilities.getKey(this).getPath()));
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

    public int getCooldown() {
        return 0;
    }

    public boolean isTechnique() {
        return true;
    }

    public int getRealCooldown(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISorcererData data = cap.getSorcererData();

        if (data == null) return 0;

        if ((this.isMelee() && data.hasTrait(Trait.HEAVENLY_RESTRICTION)) || (this.getCost(owner) > 0.0F && data.hasTrait(Trait.SIX_EYES))) {
            return this.getCooldown() / 2;
        }
        return this.getCooldown();
    }

    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.RADIAL;
    }

    public boolean isValid(LivingEntity owner) {
        if (!this.isUsableInSpectator() && owner.isSpectator()) return false;
        if (this.isUnlockable() && !this.isUnlocked(owner)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        if (this.isTechnique()) {
            if (abilityData.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                if (!this.isNotDisabledFromDA() || !abilityData.hasToggled(this)) {
                    return false;
                }
            }

            if (sorcererData.hasBurnout()) {
                return false;
            }
        }

        for (Ability ability : this.getRequirements()) {
            if (!ability.isUnlocked(owner)) return false;
        }
        return true;
    }

    private void addDuration(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        if (this instanceof IDurationable durationable && durationable.getRealDuration(owner) > 0) {
            data.addDuration(this);
        }
    }

    public void charge(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (owner instanceof Player player && player.getAbilities().instabuild) return;

        data.useEnergy(this.getRealCost(owner));

        if (this instanceof IAttack || this.getActivationType(owner) == ActivationType.INSTANT) {
            this.cooldown(owner);
        }
    }

    public void cooldown(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        if (this.getRealCooldown(owner) > 0) {
            data.addCooldown(this);
        }
    }

    public Status getStatus(LivingEntity owner) {
        if (!this.isNotDisabledFromUV() && owner.hasEffect(JJKEffects.UNLIMITED_VOID.get())) return Status.FAILURE;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Status.FAILURE;

        IAbilityData data = cap.getAbilityData();

        if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            if (!data.isCooldownDone(this)) {
                return Status.COOLDOWN;
            }

            if (!this.checkCost(owner)) {
                return Status.ENERGY;
            }
        }
        return Status.SUCCESS;
    }

    public Status isTriggerable(LivingEntity owner) {
        if (!JJKAbilities.getAbilities(owner).contains(this)) return Status.UNUSUABLE;

        if (this.getActivationType(owner) == ActivationType.INSTANT) {
            MobEffectInstance instance = owner.getEffect(JJKEffects.STUN.get());

            if (instance != null && instance.getAmplifier() > 0) return Status.FAILURE;
        }

        Status status = this.getStatus(owner);

        if (status == Status.SUCCESS && this.getActivationType(owner) == ActivationType.INSTANT) {
            this.charge(owner);
        }
        this.addDuration(owner);
        return status;
    }

    public Status isStillUsable(LivingEntity owner) {
        if (!JJKAbilities.getAbilities(owner).contains(this)) return Status.UNUSUABLE;

        if (this instanceof IAttack || this instanceof ICharged) {
            return this.getStatus(owner);
        }

        Status status = this.getStatus(owner);

        if (status == Ability.Status.SUCCESS || status == Ability.Status.COOLDOWN) {
            this.charge(owner);
        }
        return status;
    }

    public boolean checkCost(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            float cost = this.getRealCost(owner);
            return data.getEnergy() >= cost;
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

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISorcererData sorcererData = cap.getSorcererData();
        IMimicryData mimicryData = cap.getMimicryData();

        ICursedTechnique copied = mimicryData.getCurrentCopied();

        if (copied != null && copied.getAbilities().contains(this)) {
            cost *= 1.5F;
        }
        if (sorcererData.hasTrait(Trait.SIX_EYES)) {
            cost *= 0.5F;
        }

        float output = this.isScalable(owner) ? this.isChantable() ? ChantHandler.getOutput(owner, this) : sorcererData.getOutput() : 1.0F;
        return Float.parseFloat(String.format(Locale.ROOT, "%.2f", cost * output));
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

            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return 0;

            ISorcererData data = cap.getSorcererData();

            if (duration > 0) {
                duration = (int) (duration * data.getBaseOutput());
            }
            return duration;
        }
    }

    public interface IChannelened {
        default void onStop(LivingEntity owner) {}

        default int getCharge(LivingEntity owner) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return 0;

            IAbilityData data = cap.getAbilityData();
            return data.getCharge();
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
