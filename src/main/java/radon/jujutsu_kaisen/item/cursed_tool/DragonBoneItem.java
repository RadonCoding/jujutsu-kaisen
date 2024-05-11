package radon.jujutsu_kaisen.item.cursed_tool;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
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
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.DragonBoneRenderer;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.item.CursedToolItem;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class DragonBoneItem extends CursedToolItem implements GeoItem {
    private static final float MAX_ENERGY = 100.0F;
    private static final double RANGE = 5.0D;
    private static final float MAX_STEAL = 10.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DragonBoneItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public void doPostHurtEffects(ItemStack stack, DamageSource source, LivingEntity attacker, LivingEntity victim) {
        IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        if (!abilityData.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get()) && !abilityData.hasToggled(JJKAbilities.FALLING_BLOSSOM_EMOTION.get())
                && !abilityData.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) return;

        float stolen = Math.min(MAX_STEAL, sorcererData.getEnergy());

        sorcererData.useEnergy(stolen);

        stack.set(JJKDataComponentTypes.CURSED_ENERGY, Math.min(MAX_ENERGY,
                stack.getOrDefault(JJKDataComponentTypes.CURSED_ENERGY, 0.0F) + stolen));
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        IJujutsuCapability cap = pPlayer.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return super.use(pLevel, pPlayer, pUsedHand);

        ISorcererData data = cap.getSorcererData();

        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        float charge = stack.getOrDefault(JJKDataComponentTypes.CURSED_ENERGY, 0.0F) / MAX_ENERGY;

        if (charge == 0.0F) super.use(pLevel, pPlayer, pUsedHand);

        if (RotationUtil.getLookAtHit(pPlayer, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity entity) {
            pPlayer.teleportTo(entity.getX(), entity.getY(), entity.getZ());

            Vec3 pos = entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D);

            if (pPlayer.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
            }
            entity.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.MASTER, 1.0F, 1.0F);

            entity.hurt(JJKDamageSources.jujutsuAttack(pPlayer, null), this.getDamage(stack) * data.getAbilityOutput() * charge);

            pPlayer.swing(InteractionHand.MAIN_HAND);

            stack.remove(JJKDataComponentTypes.CURSED_ENERGY);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);

        pTooltipComponents.add(Component.translatable(String.format("%s.energy", this.getDescriptionId()),
                (pStack.getOrDefault(JJKDataComponentTypes.CURSED_ENERGY, 0.0F) / MAX_ENERGY) * 100));
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
