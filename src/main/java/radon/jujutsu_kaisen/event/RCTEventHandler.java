package radon.jujutsu_kaisen.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.cursed_tool.DragonBoneItem;
import radon.jujutsu_kaisen.item.cursed_tool.KamutokeDaggerItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.ArrayList;
import java.util.List;

public class RCTEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class RCTEventHandlerForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (victim.getHealth() - event.getAmount() > 0.0F) return;

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.isUnlocked(JJKAbilities.RCT1.get())) return;
            if (victim instanceof TamableAnimal tamable && tamable.isTame()) return;
            if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) return;
            if (cap.getType() != JujutsuType.SORCERER) return;
            if (SorcererUtil.getGrade(cap.getExperience()).ordinal() < SorcererGrade.GRADE_1.ordinal()) return;

            int chance = ConfigHolder.SERVER.reverseCursedTechniqueChance.get();

            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = victim.getItemInHand(hand);

                if (stack.is(Items.TOTEM_OF_UNDYING)) {
                    chance /= 2;
                }
            }

            if (HelperMethods.RANDOM.nextInt(chance) != 0) return;

            victim.setHealth(victim.getMaxHealth() / 2);
            cap.unlock(JJKAbilities.RCT1.get());

            if (victim instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            }
            event.setCanceled(true);
        }
    }
}
