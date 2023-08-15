package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.misc.Summon;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogBlackEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogWhiteEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.concurrent.atomic.AtomicBoolean;

public class DivineDogs extends Summon<DivineDogEntity> {
    public DivineDogs() {
        super(DivineDogEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.getHealth() / owner.getMaxHealth() <= 0.9F;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.15F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    protected DivineDogEntity summon(int index, LivingEntity owner) {
        return null;
    }

    @Override
    protected boolean isDead(LivingEntity owner, EntityType<?> type) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.isDead(owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), JJKEntities.DIVINE_DOG_WHITE.get()) &&
                        cap.isDead(owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), JJKEntities.DIVINE_DOG_BLACK.get())));
        return result.get();
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!this.isDead(owner, JJKEntities.DIVINE_DOG_WHITE.get())) {
                    DivineDogWhiteEntity white = new DivineDogWhiteEntity(owner, false);
                    owner.level.addFreshEntity(white);
                    cap.addSummon(white);
                }
                if (!this.isDead(owner, JJKEntities.DIVINE_DOG_BLACK.get())) {
                    DivineDogBlackEntity black = new DivineDogBlackEntity(owner, false);
                    owner.level.addFreshEntity(black);
                    cap.addSummon(black);
                }
            });
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.unsummonByClass((ServerLevel) owner.level, DivineDogWhiteEntity.class);
                cap.unsummonByClass((ServerLevel) owner.level, DivineDogBlackEntity.class);

                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            });
        }
    }

    @Override
    public EntityType<DivineDogEntity> getType() {
        return null;
    }

    @Override
    public boolean canDie() {
        return true;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
