package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.concurrent.atomic.AtomicBoolean;

public class DivineDogs extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return owner.getHealth() / owner.getMaxHealth() <= 0.9F;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.25F;
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!cap.hasSummonOfClass(level, DivineDogEntity.class)) {
                    result.set(true);
                }
            });
        }
        return result.get() ? Status.FAILURE : super.checkStatus(owner);
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                DivineDogEntity white = new DivineDogEntity(owner, DivineDogEntity.Variant.WHITE, false);
                owner.level.addFreshEntity(white);
                cap.addSummon(white);

                DivineDogEntity black = new DivineDogEntity(owner, DivineDogEntity.Variant.BLACK, false);
                owner.level.addFreshEntity(black);
                cap.addSummon(black);
            });
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.unsummonByClass((ServerLevel) owner.level, DivineDogEntity.class);

                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            });
        }
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
