package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

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
    public boolean isValid(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        CursedTechnique technique = cap.getTechnique();
        return technique != null && technique.getDomain() != null && technique.getDomain().isValid(owner) && super.isValid(owner);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            CursedTechnique technique = cap.getTechnique();

            if (technique == null || !(technique.getDomain() instanceof DomainExpansion ability)) return Status.FAILURE;

            if (ability.getStatus(owner) != Status.SUCCESS ) {
                return Status.FAILURE;
            }
        }
        return super.isTriggerable(owner);
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        CursedTechnique technique = cap.getTechnique();

        if (technique == null || !(technique.getDomain() instanceof DomainExpansion ability)) return;

        AbilityHandler.trigger(owner, ability);

        DomainExpansionEntity domain = cap.getSummonByClass(level, DomainExpansionEntity.class);

        if (domain == null) return;

        for (Entity entity : domain.getAffected()) {
            if (entity instanceof LivingEntity living) {
                ability.onHitEntity(domain, owner, living, true);
            }
        }
        cap.delayTickEvent(domain::discard, 4);

        if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
            cap.addCooldown(ability);
        }

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.zeroPointTwoSecondDomainExpansionCost.get();
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        CursedTechnique technique = cap.getTechnique();
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
