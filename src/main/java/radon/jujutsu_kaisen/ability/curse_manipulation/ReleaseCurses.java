package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.CurseManipulationDataHandler;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ReleaseCurses extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target == null;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private static void makePoofParticles(Entity entity) {
        for (int i = 0; i < 20; ++i) {
            double d0 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            double d1 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            double d2 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            ((ServerLevel) entity.level()).sendParticles(ParticleTypes.POOF, entity.getRandomX(1.0D), entity.getRandomY(), entity.getRandomZ(1.0D),
                    0, d0, d1, d2, 1.0D);
        }
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        ISorcererData ownerSorcererCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        ICurseManipulationData ownerCurseManipulationCap = owner.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();

        for (Entity summon : ownerSorcererCap.getSummons()) {
            if (!(summon instanceof CursedSpirit curse)) continue;

            ownerSorcererCap.removeSummon(curse);

            ISorcererData curseCap = curse.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (curse instanceof AbsorbedPlayerEntity absorbed) {
                ownerCurseManipulationCap.addCurse(new AbsorbedCurse(curse.getName(), curse.getType(), curseCap.serializeNBT(), absorbed.getPlayer()));
            } else {
                ownerCurseManipulationCap.addCurse(new AbsorbedCurse(curse.getName(), curse.getType(), curseCap.serializeNBT()));
            }

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerSorcererCap.serializeNBT()), player);
            }

            if (!owner.level().isClientSide) {
                makePoofParticles(curse);
            }
            curse.discard();
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
