package radon.jujutsu_kaisen.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

import java.util.List;

public class CursedSpiritOrbItem extends Item {
    public CursedSpiritOrbItem(Properties pProperties) {
        super(pProperties);
    }

    public static EntityType<?> getCurse(Registry<EntityType<?>> registry, ItemStack stack) {
        return registry.get(getKey(stack));
    }

    public static ResourceLocation getKey(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return new ResourceLocation(nbt.getString("key"));
    }

    public static void setKey(ItemStack stack, ResourceLocation key) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putString("key", key.toString());
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        MutableComponent name = super.getName(pStack).copy();
        return name.withStyle(ChatFormatting.DARK_PURPLE);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        ResourceLocation key = getKey(pStack);
        pTooltipComponents.add(Component.translatable(String.format("item.%s.curse", JujutsuKaisen.MOD_ID),
                Component.translatable(String.format("entity.%s.%s", key.getNamespace(), key.getPath()))).withStyle(ChatFormatting.DARK_RED));
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);

        if (pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!cap.hasTechnique(CursedTechnique.CURSE_MANIPULATION)) {
                pEntityLiving.addEffect(new MobEffectInstance(MobEffects.POISON, 10 * 20, 1));
                return stack;
            }
            Registry<EntityType<?>> registry = pLevel.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            cap.addCurse(pEntityLiving.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), getCurse(registry, pStack));
        }
        return stack;
    }
}

