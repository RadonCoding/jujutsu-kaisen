package radon.jujutsu_kaisen.ability.base;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class ZeroPointTwoSecondDomainExpansion extends Ability {
    @Override
    public boolean isChantable() {
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
    public MenuType getMenuType() {
        return MenuType.DOMAIN;
    }

    @Override
    public boolean isDisplayed(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        CursedTechnique technique = cap.getTechnique();
        return technique != null && technique.getDomain() != null && super.isDisplayed(owner);
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        CursedTechnique technique = cap.getTechnique();

        if (technique == null || !(technique.getDomain() instanceof DomainExpansion ability)) return Status.FAILURE;

        return cap.isCooldownDone(ability) && ability.getStatus(owner, false, false, false, false) == Status.SUCCESS ?
                super.checkTriggerable(owner) : Status.FAILURE;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        CursedTechnique technique = cap.getTechnique();

        if (technique == null || !(technique.getDomain() instanceof DomainExpansion ability)) return;

        AbilityHandler.trigger(owner, ability);

        DomainExpansionEntity domain = cap.getDomain(level);

        if (domain == null) return;

        for (Entity entity : domain.getAffected()) {
            if (entity instanceof LivingEntity living) {
                ability.onHitEntity(domain, owner, living, true);
            }
        }
        cap.delayTickEvent(domain::discard, 4);

        if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
            cap.addCooldown(owner, ability);
        }
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.zeroPointTwoSecondDomainExpansionCost.get();
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.DOMAIN_AMPLIFICATION.get();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(6.0F, 0.0F);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

}
