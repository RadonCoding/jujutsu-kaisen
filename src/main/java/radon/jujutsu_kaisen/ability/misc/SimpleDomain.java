package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityDisplayInfo;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.List;

public class SimpleDomain extends Summon<SimpleDomainEntity> {
    public SimpleDomain() {
        super(SimpleDomainEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (!owner.level().isClientSide) {
            if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            for (DomainExpansionEntity domain : cap.getDomains((ServerLevel) owner.level())) {
                if (!domain.hasSureHitEffect() || !domain.checkSureHitEffect()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.SIMPLE_DOMAIN.get());
    }

    @Override
    public boolean isTenShadows() {
        return false;
    }

    @Override
    protected SimpleDomainEntity summon(int index, LivingEntity owner) {
        return new SimpleDomainEntity(owner);
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.DOMAIN;
    }

    @Override
    public boolean display() {
        return false;
    }

    @Override
    public boolean isChantable() {
        return false;
    }

    @Override
    public int getPointsCost() {
        return 50;
    }
}
