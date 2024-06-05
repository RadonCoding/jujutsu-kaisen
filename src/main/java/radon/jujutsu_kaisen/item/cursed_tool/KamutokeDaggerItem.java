package radon.jujutsu_kaisen.item.cursed_tool;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.KamutokeDaggerRenderer;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;
import radon.jujutsu_kaisen.entity.JujutsuLightningEntity;
import radon.jujutsu_kaisen.item.CursedToolItem;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class KamutokeDaggerItem extends CursedToolItem implements GeoItem {
    public static final double RANGE = 30.0D;
    private static final int COUNT = 16;
    private static final float RANGE_COST = 500.0F;
    public static final float MELEE_DAMAGE = 5.0F;
    private static final float RANGE_DAMAGE = 15.0F;
    public static final int STUN = 10;
    private static final int DURATION = 3 * 20;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public KamutokeDaggerItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public boolean doPreHurtEffects(ItemStack stack, DamageSource source, LivingEntity attacker, LivingEntity victim, float amount) {
        IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        if (!victim.hurt(JJKDamageSources.jujutsuAttack(attacker, null), KamutokeDaggerItem.MELEE_DAMAGE * data.getAbilityOutput())) return false;

        if (victim.isDeadOrDying()) return true;

        victim.addEffect(new MobEffectInstance(JJKEffects.STUN, KamutokeDaggerItem.STUN, 0, false, false, false));

        attacker.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.MASTER, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);

        for (int i = 0; i < 32; i++) {
            double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5D;
            double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5D;
            double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5D;
            ((ServerLevel) attacker.level()).sendParticles(new LightningParticle.Options(ParticleColors.getCursedEnergyColorBright(attacker), 0.5F, 1),
                    victim.getX() + offsetX, victim.getY() + offsetY, victim.getZ() + offsetZ,
                    0, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        return false;
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private KamutokeDaggerRenderer renderer;

            @Override
            public @NotNull KamutokeDaggerRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new KamutokeDaggerRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return 72000;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(itemstack);
    }

    private float getPowerForTime(int pUseTime) {
        float f = (float) pUseTime / DURATION;

        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Nullable
    private BlockHitResult getBlockHit(LivingEntity owner) {
        Vec3 start = owner.getEyePosition();
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = RotationUtil.getHitResult(owner, start, end);

        if (result.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) result;
        } else if (result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            Vec3 offset = entity.position().subtract(0.0D, 5.0D, 0.0D);
            return owner.level().clip(new ClipContext(entity.position(), offset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        }
        return null;
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);

        int i = this.getUseDuration(pStack) - pRemainingUseDuration;
        float f = this.getPowerForTime(i);

        if (pLivingEntity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.cost", JujutsuKaisen.MOD_ID), RANGE_COST * f, RANGE_COST), false));
        }
    }

    @Override
    public void onStopUsing(@NotNull ItemStack stack, @NotNull LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        BlockHitResult hit = this.getBlockHit(entity);

        if (hit != null) {
            int i = this.getUseDuration(stack) - count;
            float f = this.getPowerForTime(i);
            float cost = RANGE_COST * f;

            if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
                if (data.getEnergy() < cost) return;

                data.useEnergy(cost);
            }

            Vec3 pos = hit.getBlockPos().getCenter();

            for (int j = 0; j < COUNT * f; j++) {
                JujutsuLightningEntity lightning = new JujutsuLightningEntity(entity, RANGE_DAMAGE * f);
                lightning.setPos(pos.add((HelperMethods.RANDOM.nextDouble() - 0.5F) * 5.0D, 0.0D, (HelperMethods.RANDOM.nextDouble() - 0.5F) * 5.0D));
                entity.level().addFreshEntity(lightning);
            }
        }
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
