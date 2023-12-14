package radon.jujutsu_kaisen.item.cursed_tool;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.NyoiStaffRenderer;
import radon.jujutsu_kaisen.entity.NyoiStaffEntity;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class NyoiStaffItem extends CursedToolItem implements GeoItem {
    private static final float COST = 100.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public NyoiStaffItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable(String.format("item.%s.cost", JujutsuKaisen.MOD_ID), COST));
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        BlockPlaceContext ctx = new BlockPlaceContext(pContext);

        Player player = ctx.getPlayer();

        if (!ctx.canPlace() || ctx.getClickedFace() != Direction.UP || player == null) {
            return InteractionResult.FAIL;
        }

        ItemStack stack = ctx.getItemInHand();
        NyoiStaffEntity staff = new NyoiStaffEntity(player, stack, Vec3.atLowerCornerWithOffset(ctx.getClickedPos(), 0.5D, 0.0D, 0.5D));

        ISorcererData cap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.getNature() == CursedEnergyNature.LIGHTNING) {
            if (cap.getEnergy() >= COST) {
                cap.useEnergy(COST);
            } else if (!player.getAbilities().instabuild) {
                return InteractionResult.FAIL;
            }
            staff.setCharged(true);
        }
        stack.shrink(1);
        ctx.getLevel().addFreshEntity(staff);

        return InteractionResult.sidedSuccess(ctx.getLevel().isClientSide);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private NyoiStaffRenderer renderer;

            @Override
            public NyoiStaffRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new NyoiStaffRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
