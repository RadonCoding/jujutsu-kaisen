package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;

public class ZeroPointTwoSecondDomainExpansion extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.DOMAIN;
    }

    @Override
    public boolean isDisplayed(LivingEntity owner) {
        IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return false;

        ISorcererData data = jujutsuCap.getSorcererData();

        ICursedTechnique technique = data.getTechnique();
        return technique != null && technique.getDomain() != null && super.isDisplayed(owner);
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return false;

        ISorcererData data = jujutsuCap.getSorcererData();

        ICursedTechnique technique = data.getTechnique();
        return technique != null && technique.getDomain() != null && technique.getDomain().isValid(owner) && super.isValid(owner);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
            IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (jujutsuCap == null) return Status.FAILURE;

            ISorcererData data = jujutsuCap.getSorcererData();

            if (data == null) return Status.FAILURE;

            ICursedTechnique technique = data.getTechnique();

            if (technique == null || !(technique.getDomain() instanceof DomainExpansion ability)) return Status.FAILURE;

            if (ability.getStatus(owner) != Status.SUCCESS ) {
                return Status.FAILURE;
            }
        }
        return super.isTriggerable(owner);
    }

    @Override
    public void run(LivingEntity owner) {
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.SPARK.get(), SoundSource.MASTER, 2.0F, 1.0F);

        IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return;

        ISorcererData data = jujutsuCap.getSorcererData();


        data.delayTickEvent(() -> {
            ICursedTechnique technique = data.getTechnique();

            if (technique == null || !(technique.getDomain() instanceof DomainExpansion ability)) return;

            AbilityHandler.trigger(owner, ability);

            DomainExpansionEntity domain = data.getSummonByClass(DomainExpansionEntity.class);

            if (domain == null) return;

            data.delayTickEvent(() -> {
                for (Entity entity : domain.getAffected()) {
                    if (entity instanceof LivingEntity living) {
                        ability.onHitEntity(domain, owner, living, true);
                    }
                }
                domain.discard();
            }, 4);

            if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
                data.addCooldown(ability);
            }
        }, 20);
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.zeroPointTwoSecondDomainExpansionCost.get();
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return null;

        ISorcererData data = jujutsuCap.getSorcererData();

        if (data == null) return null;

        ICursedTechnique technique = data.getTechnique();
        return technique == null || technique.getDomain() == null ? JJKAbilities.SIMPLE_DOMAIN.get() : technique.getDomain();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(4.0F, 0.0F);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
