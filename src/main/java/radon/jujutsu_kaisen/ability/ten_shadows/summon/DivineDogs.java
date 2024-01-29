package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.*;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class DivineDogs extends Summon<DivineDogEntity> {
    public DivineDogs() {
        super(DivineDogEntity.class);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (JJKAbilities.hasToggled(owner, this)) {
            return target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(10) == 0;
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
    protected DivineDogEntity summon(LivingEntity owner) {
        return null;
    }

    @Override
    protected boolean isDead(LivingEntity owner, EntityType<?> type) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ITenShadowsData cap = owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
        Registry<EntityType<?>> registry = owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
        return cap.isDead(registry, JJKEntities.DIVINE_DOG_WHITE.get()) && cap.isDead(registry, JJKEntities.DIVINE_DOG_BLACK.get());
    }

    @Override
    public void spawn(LivingEntity owner, boolean clone) {
        if (!owner.level().isClientSide) {
            ITenShadowsData tenShadowsCap = owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
            ISorcererData sorcererCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            Registry<EntityType<?>> registry = owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);

            if (!tenShadowsCap.isDead(registry, JJKEntities.DIVINE_DOG_WHITE.get())) {
                DivineDogWhiteEntity white = new DivineDogWhiteEntity(owner, false);
                white.setClone(clone);
                owner.level().addFreshEntity(white);
                sorcererCap.addSummon(white);
            }
            if (!tenShadowsCap.isDead(registry, JJKEntities.DIVINE_DOG_BLACK.get())) {
                DivineDogBlackEntity black = new DivineDogBlackEntity(owner, false);
                black.setClone(clone);
                owner.level().addFreshEntity(black);
                sorcererCap.addSummon(black);
            }
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            cap.unsummonByClass(DivineDogWhiteEntity.class);
            cap.unsummonByClass(DivineDogBlackEntity.class);

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            }
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
}
