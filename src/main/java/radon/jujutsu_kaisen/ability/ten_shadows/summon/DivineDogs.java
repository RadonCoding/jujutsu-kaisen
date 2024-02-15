package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
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
        if (target == null || target.isDeadOrDying()) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        if (data.hasToggled(this)) {
            return owner.level().getGameTime() % 20 != 0 || HelperMethods.RANDOM.nextInt(10) != 0;
        }
        return HelperMethods.RANDOM.nextInt(40) == 0;
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
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ITenShadowsData data = cap.getTenShadowsData();

        return data.isDead(JJKEntities.DIVINE_DOG_WHITE.get()) && data.isDead(JJKEntities.DIVINE_DOG_BLACK.get());
    }

    @Override
    public void spawn(LivingEntity owner, boolean clone) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        ITenShadowsData tenShadowsData = cap.getTenShadowsData();

        if (tenShadowsData == null || sorcererData == null) return;

        if (!tenShadowsData.isDead(JJKEntities.DIVINE_DOG_WHITE.get())) {
            DivineDogWhiteEntity white = new DivineDogWhiteEntity(owner, false);
            white.setClone(clone);
            owner.level().addFreshEntity(white);
            sorcererData.addSummon(white);
        }
        if (!tenShadowsData.isDead(JJKEntities.DIVINE_DOG_BLACK.get())) {
            DivineDogBlackEntity black = new DivineDogBlackEntity(owner, false);
            black.setClone(clone);
            owner.level().addFreshEntity(black);
            sorcererData.addSummon(black);
        }

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(sorcererData.serializeNBT()), player);
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

            data.unsummonByClass(DivineDogWhiteEntity.class);
            data.unsummonByClass(DivineDogBlackEntity.class);

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);
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
