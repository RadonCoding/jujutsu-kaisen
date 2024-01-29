package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuickDraw extends Ability implements Ability.IToggled {
    private static final Map<UUID, Vec3> POSITIONS = new HashMap<>();

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    private static void attack(LivingEntity owner, Entity entity) {
        if (entity instanceof AbstractArrow || entity instanceof ThrowableItemProjectile) {
            owner.lookAt(EntityAnchorArgument.Anchor.EYES, entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D));
            owner.swing(InteractionHand.MAIN_HAND, true);
            entity.discard();
        } else if (entity instanceof LivingEntity) {
            if (entity.invulnerableTime > 0) return;

            owner.lookAt(EntityAnchorArgument.Anchor.EYES, entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D));
        }
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!POSITIONS.containsKey(owner.getUUID())) return;

        if (JJKAbilities.hasToggled(owner, JJKAbilities.SIMPLE_DOMAIN.get())) {
            SimpleDomainEntity domain = cap.getSummonByClass(SimpleDomainEntity.class);

            if (domain == null) return;

            for (Entity entity : owner.level().getEntities(owner, domain.getBoundingBox())) {
                if (entity == domain || entity.distanceTo(domain) > domain.getRadius()) continue;

                attack(owner, entity);
            }
        }
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        if (POSITIONS.containsKey(owner.getUUID())) {
            if (owner.position() != POSITIONS.get(owner.getUUID())) {
                return Status.FAILURE;
            }
        }
        return super.isStillUsable(owner);
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            POSITIONS.put(owner.getUUID(), owner.position());
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            POSITIONS.remove(owner.getUUID());

            if (owner instanceof ServerPlayer player) {
                ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            }
        }
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return (JJKAbilities.hasToggled(owner, JJKAbilities.SIMPLE_DOMAIN.get()) || JJKAbilities.hasToggled(owner, JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) && super.isValid(owner);
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.SIMPLE_DOMAIN.get();
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.quickDrawCost.get();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(4.0F, 4.0F);
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class QuickDrawForgeEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            Entity attacker = event.getSource().getDirectEntity();

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide || !JJKAbilities.hasToggled(victim, JJKAbilities.QUICK_DRAW.get()) ||
                    !JJKAbilities.hasToggled(victim, JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) return;

            QuickDraw.attack(victim, attacker);
        }
    }
}
