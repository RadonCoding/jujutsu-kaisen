package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogBlackEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class DivineDogBlack extends Summon<DivineDogBlackEntity> {
    public DivineDogBlack() {
        super(DivineDogBlackEntity.class);
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

        IAbilityData data = cap.getAbilityData();

        if (data.hasToggled(this)) {
            return owner.level().getGameTime() % 20 != 0 || HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return owner.level().getGameTime() % 20 == 0 && HelperMethods.RANDOM.nextInt(20) == 0;
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
    protected DivineDogBlackEntity summon(LivingEntity owner) {
        return new DivineDogBlackEntity(owner, false);
    }

    @Override
    protected boolean isDead(LivingEntity owner, EntityType<?> type) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ITenShadowsData data = cap.getTenShadowsData();

        return data.isDead(JJKEntities.DIVINE_DOG_BLACK.get());
    }

    @Override
    public void spawn(LivingEntity owner, boolean clone) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        ITenShadowsData tenShadowsData = cap.getTenShadowsData();

        if (tenShadowsData == null || sorcererData == null) return;

        DivineDogBlackEntity black = new DivineDogBlackEntity(owner, false);
        black.setClone(clone);
        owner.level().addFreshEntity(black);
        sorcererData.addSummon(black);

        if (owner instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(sorcererData.serializeNBT(player.registryAccess())));
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();
            data.unsummonByClass(DivineDogBlackEntity.class);

            if (owner instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(data.serializeNBT(player.registryAccess())));
            }
        }
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.DIVINE_DOG_BLACK.get());
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
