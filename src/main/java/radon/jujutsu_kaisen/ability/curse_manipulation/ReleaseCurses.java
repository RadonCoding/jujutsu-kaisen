package radon.jujutsu_kaisen.ability.curse_manipulation;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncCurseManipulationDataS2CPacket;
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

        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return;

        ISorcererData ownerSorcererData = ownerCap.getSorcererData();
        ICurseManipulationData ownerCurseManipulationData = ownerCap.getCurseManipulationData();

        for (Entity summon : ownerSorcererData.getSummons()) {
            if (!(summon instanceof CursedSpirit curse)) continue;

            ownerSorcererData.removeSummon(curse);

            IJujutsuCapability curseCap = curse.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (curseCap == null) continue;

            ISorcererData curseData = curseCap.getSorcererData();

            if (curse instanceof AbsorbedPlayerEntity absorbed) {
                ownerCurseManipulationData.addCurse(new AbsorbedCurse(curse.getName(), curse.getType(), curseData.serializeNBT(curse.registryAccess()), absorbed.getPlayer()));
            } else {
                ownerCurseManipulationData.addCurse(new AbsorbedCurse(curse.getName(), curse.getType(), curseData.serializeNBT(curse.registryAccess())));
            }

            if (owner instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(ownerSorcererData.serializeNBT(player.registryAccess())));
                PacketDistributor.sendToPlayer(player, new SyncCurseManipulationDataS2CPacket(ownerCurseManipulationData.serializeNBT(player.registryAccess())));
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
