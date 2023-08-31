package radon.jujutsu_kaisen.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.base.CursedObjectItem;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CursedTotemItem extends CursedObjectItem {
    private static final int INTERVAL = 10 * 20;

    public CursedTotemItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_1;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);

        if (!(pEntity instanceof LivingEntity living) || pLevel.isClientSide) return;

        if (pLevel.getGameTime() % INTERVAL == 0) {
            AtomicBoolean result = new AtomicBoolean();
            living.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.getType() == JujutsuType.CURSE));

            if (result.get()) return;

            Registry<MobEffect> registry = pLevel.registryAccess().registryOrThrow(Registries.MOB_EFFECT);
            List<MobEffect> effects = registry.entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .filter(x -> x.getCategory() == MobEffectCategory.HARMFUL)
                    .toList();

            if (!effects.isEmpty()) {
                MobEffect random = effects.get(HelperMethods.RANDOM.nextInt(effects.size()));
                living.addEffect(new MobEffectInstance(random, HelperMethods.RANDOM.nextInt(15) * 20));
            }
        }
    }
}
