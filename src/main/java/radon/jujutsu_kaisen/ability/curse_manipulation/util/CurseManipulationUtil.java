package radon.jujutsu_kaisen.ability.curse_manipulation.util;


import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;
import radon.jujutsu_kaisen.entity.IControllableFlyingRide;
import radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity;
import radon.jujutsu_kaisen.entity.curse.CursedSpirit;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.network.packet.s2c.SyncCurseManipulationDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

import javax.annotation.Nullable;
import java.util.List;

public class CurseManipulationUtil {
    @Nullable
    public static CursedSpirit createCurse(LivingEntity owner, AbsorbedCurse curse) {
        CursedSpirit entity = curse.getType() == EntityType.PLAYER ? JJKEntities.ABSORBED_PLAYER.get().create(owner.level()) :
                (CursedSpirit) curse.getType().create(owner.level());

        if (entity == null) return null;

        entity.setTame(true, false);
        entity.setOwner(owner);

        GameProfile profile = curse.getProfile();

        if (profile != null && entity instanceof AbsorbedPlayerEntity absorbed) {
            absorbed.setPlayer(profile);
        }

        Vec3 pos = owner.position()
                .subtract(RotationUtil.calculateViewVector(0.0F, owner.getYRot())
                        .multiply(entity.getBbWidth() / 2.0F, 0.0D, entity.getBbWidth() / 2.0F));
        entity.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());

        return entity;
    }

    public static float getCurseExperience(AbsorbedCurse curse) {
        return curse.getData().getFloat("experience");
    }

    public static float getCurseCost(AbsorbedCurse curse) {
        return Math.max(1.0F, getCurseExperience(curse) * 0.01F);
    }

    @Nullable
    public static Entity summonCurse(LivingEntity owner, AbsorbedCurse curse, boolean charge) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return null;

        ICurseManipulationData data = cap.getCurseManipulationData();

        List<AbsorbedCurse> curses = data.getCurses();

        if (!curses.contains(curse)) return null;

        return summonCurse(owner, curses.indexOf(curse), charge);
    }

    @Nullable
    public static Entity summonCurse(LivingEntity owner, int index, boolean charge) {
        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return null;

        ISorcererData ownerSorcererData = ownerCap.getSorcererData();
        IAbilityData ownerAbilityData = ownerCap.getAbilityData();
        ICurseManipulationData ownerCurseManipulationData = ownerCap.getCurseManipulationData();

        if (owner.hasEffect(JJKEffects.UNLIMITED_VOID) || ownerAbilityData.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get()))
            return null;

        List<AbsorbedCurse> curses = ownerCurseManipulationData.getCurses();

        if (index >= curses.size()) return null;

        AbsorbedCurse curse = curses.get(index);

        if (charge) {
            float cost = getCurseCost(curse);

            if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
                if (ownerSorcererData.getEnergy() < cost) {
                    return null;
                }
                ownerSorcererData.useEnergy(cost);
            }
        }

        CursedSpirit entity = createCurse(owner, curse);

        if (entity == null) return null;

        owner.level().addFreshEntity(entity);

        ownerSorcererData.addSummon(entity);
        ownerCurseManipulationData.removeCurse(curse);

        if (!owner.onGround() && entity instanceof IControllableFlyingRide) {
            owner.startRiding(entity);
        }

        if (owner instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(ownerSorcererData.serializeNBT(player.registryAccess())));
            PacketDistributor.sendToPlayer(player, new SyncCurseManipulationDataS2CPacket(ownerCurseManipulationData.serializeNBT(player.registryAccess())));
        }

        IJujutsuCapability curseCap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (curseCap == null) return null;

        ISorcererData curseData = curseCap.getSorcererData();
        curseData.deserializeNBT(entity.registryAccess(), curse.getData());

        return entity;
    }
}
