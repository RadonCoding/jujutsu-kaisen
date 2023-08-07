package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.WheelEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.concurrent.atomic.AtomicBoolean;

public class Wheel extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return true;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!cap.hasSummonOfClass(level, WheelEntity.class)) {
                    result.set(true);
                }
            });
        }
        return result.get() ? Status.FAILURE : super.checkStatus(owner);
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                WheelEntity wheel = new WheelEntity(owner);
                owner.level.addFreshEntity(wheel);
                cap.addSummon(wheel);
            });
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.unsummonByClass((ServerLevel) owner.level, WheelEntity.class);

                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            });
        }
    }

    @Override
    public boolean shouldLog() {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
