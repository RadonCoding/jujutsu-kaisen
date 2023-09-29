package radon.jujutsu_kaisen.item;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.client.render.item.KamutokeDaggerRenderer;
import radon.jujutsu_kaisen.entity.JujutsuLightningEntity;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class KamutokeDaggerItem extends CursedToolItem implements GeoItem {
    public static final double RANGE = 30.0D;
    private static final int COUNT = 16;
    private static final float COST = 500.0F;
    private static final float DAMAGE = 20.0F;
    private static final int DURATION = 3 * 20;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public KamutokeDaggerItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    protected SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private KamutokeDaggerRenderer renderer;

            @Override
            public KamutokeDaggerRenderer getCustomRenderer() {
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

    private @Nullable BlockHitResult getBlockHit(LivingEntity owner) {
        Vec3 start = owner.getEyePosition();
        Vec3 look = HelperMethods.getLookAngle(owner);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = HelperMethods.getHitResult(owner, start, end);

        if (result.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) result;
        } else if (result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            Vec3 offset = entity.position().subtract(0.0D, 5.0D, 0.0D);
            return owner.level().clip(new ClipContext(entity.position(), offset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        }
        return null;
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);

        int i = this.getUseDuration(pStack) - pRemainingUseDuration;
        float f = getPowerForTime(i);

        if (pLivingEntity instanceof LocalPlayer && pLevel.isClientSide) {
            ClientWrapper.setOverlayMessage(Component.translatable(String.format("chat.%s.cost", JujutsuKaisen.MOD_ID), COST * f, COST), false);
        }
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);

        if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

        int i = this.getUseDuration(stack) - count;
        float f = getPowerForTime(i);
        float cost = COST * f;

        ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
            if (cap.getEnergy() < cost) return;
            cap.useEnergy(cost);
        }

        BlockHitResult hit = this.getBlockHit(entity);

        if (hit != null) {
            Vec3 pos = hit.getBlockPos().getCenter();

            for (int j = 0; j < COUNT * f; j++) {
                JujutsuLightningEntity lightning = new JujutsuLightningEntity(entity, DAMAGE * f);
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
