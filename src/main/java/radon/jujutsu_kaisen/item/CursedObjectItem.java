package radon.jujutsu_kaisen.item;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.List;

public abstract class CursedObjectItem extends Item {
    private static final int DURATION = 30 * 20;
    private static final int AMPLIFIER = 5;

    public CursedObjectItem(Properties pProperties) {
        super(pProperties);
    }

    public abstract SorcererGrade getGrade();

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        MutableComponent name = super.getName(pStack).copy();
        return name.withStyle(ChatFormatting.DARK_PURPLE);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable(String.format("item.%s.grade", JujutsuKaisen.MOD_ID),
                this.getGrade().getName().copy().withStyle(ChatFormatting.DARK_RED)));
    }

    public float getEnergy() {
        return (this.getGrade().ordinal() + 1) * ConfigHolder.SERVER.cursedObjectEnergyForGrade.get().floatValue();
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);

        IJujutsuCapability cap = pEntityLiving.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap != null) {
            ISorcererData data = cap.getSorcererData();

            if (data.getType() == JujutsuType.CURSE) {
                data.addExtraEnergy(this.getEnergy());
            }
        }
        return stack;
    }
}
