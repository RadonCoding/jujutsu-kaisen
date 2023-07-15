package radon.jujutsu_kaisen.ability.yuta;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.SyncSorcererDataS2CPacket;

public class Copy extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return JJKAbilities.hasToggled(owner, JJKAbilities.RIKA.get());
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                attacker.getCapability(SorcererDataHandler.INSTANCE).ifPresent(attackerCap -> {
                    if (attackerCap.hasToggled(JJKAbilities.COPY.get())) {
                        victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(victimCap -> {
                            CursedTechnique current = attackerCap.getTechnique();
                            CursedTechnique copied = victimCap.getTechnique();

                            if (copied == null || current == null) return;

                            if (!current.equals(copied)) {
                                attacker.sendSystemMessage(Component.translatable(String.format("chat.%s.copy", JujutsuKaisen.MOD_ID), copied.getComponent()));

                                attackerCap.setCopied(copied);
                                attackerCap.toggle(attacker, JJKAbilities.COPY.get());

                                if (attacker instanceof ServerPlayer player) {
                                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(attackerCap.serializeNBT()), player);
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}
