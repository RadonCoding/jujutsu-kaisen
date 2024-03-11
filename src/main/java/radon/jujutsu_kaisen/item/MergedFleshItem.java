package radon.jujutsu_kaisen.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;

public class MergedFleshItem extends CursedEnergyFleshItem {
    public MergedFleshItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);

        IJujutsuCapability cap = pEntityLiving.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return stack;

        ISorcererData data = cap.getSorcererData();

        if (data.hasTrait(Trait.HEAVENLY_RESTRICTION)) return stack;

        if (data.getType() == JujutsuType.CURSE) {
            data.addExtraEnergy((getGrade(pStack).ordinal() + 1) * ConfigHolder.SERVER.cursedObjectEnergyForGrade.get().floatValue() * 2.0F);
        }

        if (getGrade(pStack) == SorcererGrade.SPECIAL_GRADE && !data.hasTrait(Trait.PERFECT_BODY)) {
            if (data.getExperience() >= SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 4.0F) {
                data.addTrait(Trait.PERFECT_BODY);
            } else {
                pEntityLiving.sendSystemMessage(Component.translatable(String.format("chat.%s.not_strong_enough", JujutsuKaisen.MOD_ID)));
            }
        }
        return stack;
    }
}
