package radon.jujutsu_kaisen.ability.mimicry;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Mimicry extends Ability implements Ability.IToggled {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getCopied().size() < ConfigHolder.SERVER.maximumCopiedTechniques.get() && JJKAbilities.hasToggled(owner, JJKAbilities.RIKA.get()) && super.isValid(owner);
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

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class MimicryForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();

            if (!HelperMethods.isMelee(source)) return;

            if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

            if (!attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!attackerCap.hasToggled(JJKAbilities.MIMICRY.get())) return;

            LivingEntity victim = event.getEntity();

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            CursedTechnique current = attackerCap.getTechnique();
            CursedTechnique copied = victimCap.getTechnique();

            if (copied == null || current == null || attackerCap.hasTechnique(copied)) return;

            if (current != copied) {
                attacker.sendSystemMessage(Component.translatable(String.format("chat.%s.mimicry", JujutsuKaisen.MOD_ID), copied.getName()));

                attackerCap.copy(copied);

                attackerCap.toggle(JJKAbilities.MIMICRY.get());

                if (attacker instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(attackerCap.serializeNBT()), player);
                }
            }
        }
    }
}
