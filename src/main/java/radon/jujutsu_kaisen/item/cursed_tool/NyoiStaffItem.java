package radon.jujutsu_kaisen.item.cursed_tool;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
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

import java.util.function.Consumer;

public class NyoiStaffItem extends CursedToolItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public NyoiStaffItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
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
            float cost = JJKAbilities.LIGHTNING.get().getRealCost(player) * 0.5F;

            boolean success = player.getAbilities().instabuild;

            if (!player.getAbilities().instabuild) {
                if (cap.getEnergy() >= cost) {
                    cap.useEnergy(cost);
                    success = true;
                }
            }
            staff.setCharged(success);
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
