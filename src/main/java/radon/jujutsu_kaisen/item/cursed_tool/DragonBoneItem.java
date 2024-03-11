package radon.jujutsu_kaisen.item.cursed_tool;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.DragonBoneRenderer;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class DragonBoneItem extends CursedToolItem implements GeoItem {
    private static final float MAX_ENERGY = 100.0F;
    private static final double RANGE = 5.0D;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DragonBoneItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    public static float getEnergy(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.getFloat("energy");
    }

    public static void addEnergy(ItemStack stack, float energy) {
        CompoundTag nbt = stack.getOrCreateTag();

        if (nbt.contains("energy")) {
            energy += nbt.getFloat("energy");
        }
        nbt.putFloat("energy", Math.min(MAX_ENERGY, energy));
    }

    public static void resetEnergy(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putFloat("energy", 0.0F);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        IJujutsuCapability cap = pPlayer.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return super.use(pLevel, pPlayer, pUsedHand);

        ISorcererData data = cap.getSorcererData();

        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        float charge = getEnergy(stack) / MAX_ENERGY;

        if (charge == 0.0F) super.use(pLevel, pPlayer, pUsedHand);

        if (RotationUtil.getLookAtHit(pPlayer, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity entity) {
            pPlayer.teleportTo(entity.getX(), entity.getY(), entity.getZ());

            Vec3 pos = entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D);

            if (pPlayer.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
            }
            entity.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

            entity.hurt(JJKDamageSources.jujutsuAttack(pPlayer, null), this.getDamage() * data.getAbilityOutput() * charge);

            pPlayer.swing(InteractionHand.MAIN_HAND);

            resetEnergy(stack);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        pTooltipComponents.add(Component.translatable(String.format("%s.energy", this.getDescriptionId()), (getEnergy(pStack) / MAX_ENERGY) * 100));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private DragonBoneRenderer renderer;

            @Override
            public DragonBoneRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new DragonBoneRenderer();
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
