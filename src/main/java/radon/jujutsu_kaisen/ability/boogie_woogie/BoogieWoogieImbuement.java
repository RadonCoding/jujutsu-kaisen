package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.base.Imbuement;

public class BoogieWoogieImbuement extends Imbuement {
    @Override
    public void hit(LivingEntity owner, LivingEntity target) {
        SwapSelf.swap(owner, target);
    }
}
