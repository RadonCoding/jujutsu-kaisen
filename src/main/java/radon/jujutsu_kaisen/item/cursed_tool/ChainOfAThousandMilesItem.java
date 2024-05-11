package radon.jujutsu_kaisen.item.cursed_tool;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.ChainOfAThousandMilesRenderer;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.item.CursedToolItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ChainOfAThousandMilesItem extends CursedToolItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ChainOfAThousandMilesItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private ChainOfAThousandMilesRenderer renderer;

            @Override
            public @NotNull ChainOfAThousandMilesRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new ChainOfAThousandMilesRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return 72000;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);

        ItemStack item = pLivingEntity.getOffhandItem();

        if (item.getItem() instanceof SwordItem || item.isEmpty() && pRemainingUseDuration == this.getUseDuration(pStack)) {
            ThrownChainProjectile projectile = new ThrownChainProjectile(pLivingEntity, item.copy());
            pLevel.addFreshEntity(projectile);

            if (!item.isEmpty()) {
                if (!(pLivingEntity instanceof Player player && player.getAbilities().instabuild)) {
                    item.hurtAndBreak(1, pLivingEntity, EquipmentSlot.OFFHAND);
                }
                item.shrink(1);

                if (item.isEmpty()) {
                    pLivingEntity.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
