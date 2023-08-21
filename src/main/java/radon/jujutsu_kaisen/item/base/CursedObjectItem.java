package radon.jujutsu_kaisen.item.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;

import java.util.List;

public abstract class CursedObjectItem extends Item {
    private static final int DURATION = 30 * 20;
    private static final int AMPLIFIER = 5;

    public CursedObjectItem(Properties pProperties) {
        super(pProperties);
    }

    public abstract SorcererGrade getGrade();

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable(String.format("item.%s.grade", JujutsuKaisen.MOD_ID),
                this.getGrade().getName().copy().withStyle(ChatFormatting.DARK_RED)));
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);

        if (!pLevel.isClientSide) {
            pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (cap.isCurse()) {
                    cap.consume(pEntityLiving, this.getGrade());
                } else {
                    pEntityLiving.addEffect(new MobEffectInstance(MobEffects.WITHER, Mth.floor(DURATION * ((float) (this.getGrade().ordinal() + 1) / SorcererGrade.values().length)),
                            Mth.floor(AMPLIFIER * ((float) (this.getGrade().ordinal() + 1) / SorcererGrade.values().length))));
                }
            });
        }
        return stack;
    }
}
