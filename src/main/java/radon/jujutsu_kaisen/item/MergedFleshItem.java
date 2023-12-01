package radon.jujutsu_kaisen.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;

public class MergedFleshItem extends Item {
    private static final int DURATION = 30 * 20;
    private static final int AMPLIFIER = 5;

    public MergedFleshItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity) {
        boolean success = false;

        if (pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getExperience() >= ConfigHolder.SERVER.maximumExperienceAmount.get() && !cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                if (cap.hasTrait(Trait.PERFECT_BODY)) {
                    cap.addTrait(Trait.PERFECT_BODY);
                }
                success = true;
            }
        }

        if (!success) {
            pLivingEntity.sendSystemMessage(Component.translatable(String.format("chat.%s.not_strong_enough", JujutsuKaisen.MOD_ID)));
            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, DURATION, AMPLIFIER));
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }
}
