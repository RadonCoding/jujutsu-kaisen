package radon.jujutsu_kaisen.ability.cursed_speech;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Imbuement;

public class CursedSpeechImbuement extends Imbuement {
    @Override
    public void hit(ItemStack stack, LivingEntity owner, LivingEntity target) {

    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }
}
