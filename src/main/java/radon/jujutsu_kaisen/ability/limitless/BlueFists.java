package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;

public class BlueFists extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.distanceTo(target) < 5.0D;
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
        return 1.0F;
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        if (!owner.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() || !owner.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
            return Status.FAILURE;
        }
        return super.checkStatus(owner);
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            boolean melee = !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK));

            if (melee && JJKAbilities.hasToggled(attacker, JJKAbilities.BLUE_FISTS.get())) {
                victim.setDeltaMovement(attacker.position().subtract(victim.position()).normalize());
                victim.hurtMarked = true;

                victim.playSound(SoundEvents.PLAYER_ATTACK_CRIT);

                if (!attacker.level.isClientSide) {
                    ((ServerLevel) attacker.level).getChunkSource().broadcastAndSend(attacker, new ClientboundAnimatePacket(victim, ClientboundAnimatePacket.CRITICAL_HIT));
                }
                victim.invulnerableTime = 0;
                victim.hurt(JJKDamageSources.jujutsuAttack(attacker, JJKAbilities.BLUE_FISTS.get()), event.getAmount() * 0.5F);
            }
        }
    }
}
