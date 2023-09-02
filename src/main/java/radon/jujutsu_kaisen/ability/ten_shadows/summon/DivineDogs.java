package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.*;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DivineDogs extends Summon<DivineDogEntity> {
    public DivineDogs() {
        super(DivineDogEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (JJKAbilities.hasToggled(owner, this)) {
            return target != null;
        }
        return target != null && HelperMethods.RANDOM.nextInt(10) == 0;
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

        Registry<EntityType<?>> registry = owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.isDead(registry, JJKEntities.DIVINE_DOG_WHITE.get()) && cap.isDead(registry, JJKEntities.DIVINE_DOG_BLACK.get())));
        return result.get();
    }

    @Override
    public void spawn(LivingEntity owner, boolean clone) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                Registry<EntityType<?>> registry = owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);

                if (!cap.isDead(registry, JJKEntities.DIVINE_DOG_WHITE.get())) {
                    DivineDogWhiteEntity white = new DivineDogWhiteEntity(owner, false);
                    white.setClone(clone);
                    owner.level.addFreshEntity(white);
                    cap.addSummon(white);
                }
                if (!cap.isDead(registry, JJKEntities.DIVINE_DOG_BLACK.get())) {
                    DivineDogBlackEntity black = new DivineDogBlackEntity(owner, false);
                    black.setClone(clone);
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
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.DIVINE_DOG_WHITE.get(), JJKEntities.DIVINE_DOG_BLACK.get());
    }

    @Override
    public boolean canDie() {
        return true;
    }

    @Override
    public boolean isTenShadows() {
        return true;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
