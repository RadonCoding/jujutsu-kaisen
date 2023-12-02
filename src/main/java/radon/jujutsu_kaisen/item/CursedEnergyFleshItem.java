package radon.jujutsu_kaisen.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.List;

public class CursedEnergyFleshItem extends Item {
    private static final int DURATION = 30 * 20;
    private static final int AMPLIFIER = 5;

    public CursedEnergyFleshItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable(String.format("item.%s.grade", JujutsuKaisen.MOD_ID),
                getGrade(pStack).getName().copy().withStyle(ChatFormatting.DARK_RED)));
    }

    public static SorcererGrade getGrade(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return SorcererGrade.values()[nbt.getInt("grade")];
    }

    public static void setGrade(ItemStack stack, SorcererGrade grade) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt("grade", grade.ordinal());
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);

        pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            SorcererGrade grade = getGrade(pStack);

            if (cap.getType() == JujutsuType.CURSE) {
                cap.addExtraEnergy((grade.ordinal() + 1) * ConfigHolder.SERVER.cursedObjectEnergyForGrade.get().floatValue());
            } else if (!cap.hasTrait(Trait.HEAVENLY_RESTRICTION) && !cap.hasTrait(Trait.PERFECT_BODY)) {
                pEntityLiving.addEffect(new MobEffectInstance(MobEffects.WITHER, Mth.floor(DURATION * ((float) (grade.ordinal() + 1) / SorcererGrade.values().length)),
                        Mth.floor(AMPLIFIER * ((float) (grade.ordinal() + 1) / SorcererGrade.values().length))));
            }
        });
        return stack;
    }
}
