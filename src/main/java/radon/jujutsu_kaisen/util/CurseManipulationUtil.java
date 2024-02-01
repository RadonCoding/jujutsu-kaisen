package radon.jujutsu_kaisen.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.CurseManipulationDataHandler;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncCurseManipulationDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import javax.annotation.Nullable;
import java.util.List;

public class CurseManipulationUtil {
    @Nullable
    public static CursedSpirit createCurse(LivingEntity owner, AbsorbedCurse curse) {
        CursedSpirit entity = curse.getType() == EntityType.PLAYER ? JJKEntities.ABSORBED_PLAYER.get().create(owner.level()) :
                (CursedSpirit) curse.getType().create(owner.level());

        if (entity == null) return null;

        entity.setTame(true);
        entity.setOwner(owner);

        GameProfile profile = curse.getProfile();

        if (profile != null && entity instanceof AbsorbedPlayerEntity absorbed) {
            absorbed.setPlayer(profile);
        }

        Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
        Vec3 pos = owner.position()
                .subtract(direction.multiply(entity.getBbWidth(), 0.0D, entity.getBbWidth()));
        entity.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());

        return entity;
    }

    public static float getCurseExperience(AbsorbedCurse curse) {
        ISorcererData data = new SorcererData();
        data.deserializeNBT(curse.getData());
        return data.getExperience();
    }

    public static float getCurseCost(AbsorbedCurse curse) {
        return Math.max(1.0F, getCurseExperience(curse) * 0.01F);
    }

    @Nullable
    public static Entity summonCurse(LivingEntity owner, AbsorbedCurse curse, boolean charge) {
        ICurseManipulationData cap = owner.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();

        List<AbsorbedCurse> curses = cap.getCurses();

        if (!curses.contains(curse)) return null;

        return summonCurse(owner, curses.indexOf(curse), charge);
    }

    @Nullable
    public static Entity summonCurse(LivingEntity owner, int index, boolean charge) {
        if (owner.hasEffect(JJKEffects.UNLIMITED_VOID.get()) || JJKAbilities.hasToggled(owner, JJKAbilities.DOMAIN_AMPLIFICATION.get())) return null;

        ISorcererData ownerSorcererCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        ICurseManipulationData ownerCurseManipulationCap = owner.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();

        List<AbsorbedCurse> curses = ownerCurseManipulationCap.getCurses();

        if (index >= curses.size()) return null;

        AbsorbedCurse curse = curses.get(index);

        if (charge) {
            float cost = getCurseCost(curse);

            if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
                if (ownerSorcererCap.getEnergy() < cost) {
                    return null;
                }
                ownerSorcererCap.useEnergy(cost);
            }
        }

        CursedSpirit entity = createCurse(owner, curse);

        if (entity == null) return null;

        owner.level().addFreshEntity(entity);

        ISorcererData curseCap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        curseCap.deserializeNBT(curse.getData());

        ownerSorcererCap.addSummon(entity);
        ownerCurseManipulationCap.removeCurse(curse);

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncCurseManipulationDataS2CPacket(ownerSorcererCap.serializeNBT()), player);
        }
        return entity;
    }
}
