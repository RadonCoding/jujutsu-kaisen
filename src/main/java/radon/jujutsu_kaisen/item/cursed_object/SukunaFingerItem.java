package radon.jujutsu_kaisen.item.cursed_object;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.item.CursedObjectItem;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;
import radon.jujutsu_kaisen.util.EntityUtil;

public class SukunaFingerItem extends CursedObjectItem {
    public SukunaFingerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack pStack) {
        return super.isFoil(pStack) || pStack.getOrDefault(JJKDataComponentTypes.IS_FULL_SOUL, false);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public int getEntityLifespan(@NotNull ItemStack itemStack, @NotNull Level level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        IJujutsuCapability cap = pEntityLiving.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap != null) {
            ISorcererData data = cap.getSorcererData();

            if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) return pStack;

            if (pStack.getOrDefault(JJKDataComponentTypes.IS_FULL_SOUL, false)) {
                data.addTrait(Trait.PERFECT_BODY);
                return super.finishUsingItem(pStack, pLevel, pEntityLiving);
            } else if (data.getType() == JujutsuType.SORCERER) {
                int count = pStack.getCount();

                int eaten = Math.min(20 - data.getFingers(), count);

                if (data.hasTrait(Trait.VESSEL)) {
                    if (eaten > 0) {
                        data.addFingers(eaten);
                        pStack.shrink(eaten);

                        data.addAdditional(JJKCursedTechniques.SHRINE.get());

                        if (eaten >= 10) {
                            AbilityHandler.trigger(pEntityLiving, JJKAbilities.SWITCH.get(), true);
                        }
                    }
                    return pStack;
                } else {
                    pStack.shrink(count);
                    EntityUtil.convertTo(pEntityLiving, new SukunaEntity(pEntityLiving, count, false), true, false);
                    return pStack;
                }
            } else if (data.getType() == JujutsuType.CURSE) {
                if (pEntityLiving instanceof CursedSpirit curse && !curse.isTame() && curse.getGrade().ordinal() < SorcererGrade.GRADE_1.ordinal()) {
                    pStack.shrink(1);
                    EntityUtil.convertTo(pEntityLiving, JJKEntities.FINGER_BEARER.get().create(pLevel), true, false);
                    return pStack;
                }
                return super.finishUsingItem(pStack, pLevel, pEntityLiving);
            }
        }
        return pStack;
    }
}
