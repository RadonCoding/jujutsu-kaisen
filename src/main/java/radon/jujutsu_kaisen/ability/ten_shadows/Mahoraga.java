package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;

import java.util.concurrent.atomic.AtomicBoolean;

public class Mahoraga extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return owner.getHealth() / owner.getMaxHealth() <= 0.1F;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return this.isTamed(owner) ? ActivationType.TOGGLED : ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!this.isTamed(owner)) {
            if (!owner.level.isClientSide) {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    owner.level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.WOLF_HOWLING.get(), SoundSource.MASTER, 5.0F, 1.0F);

                    MahoragaEntity mahoraga = new MahoragaEntity(owner, false);
                    owner.level.addFreshEntity(mahoraga);
                    cap.addSummon(mahoraga);
                });
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return this.isTamed(owner) ? 1.0F : 100.0F;
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!cap.hasSummonOfClass(level, MahoragaEntity.class)) {
                    result.set(true);
                }
            });
        }
        return result.get() ? Status.FAILURE : super.checkStatus(owner);
    }

    private boolean isTamed(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.hasTamed(owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), JJKEntities.MAHORAGA.get())));
        return result.get();
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                MahoragaEntity mahoraga = new MahoragaEntity(owner, true);
                owner.level.addFreshEntity(mahoraga);
                cap.addSummon(mahoraga);
            });
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.unsummonByClass((ServerLevel) owner.level, MahoragaEntity.class);

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
