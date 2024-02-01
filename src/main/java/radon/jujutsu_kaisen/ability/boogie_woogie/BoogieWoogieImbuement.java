package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Imbuement;

public class BoogieWoogieImbuement extends Imbuement {
    @Override
    public void hit(LivingEntity owner, LivingEntity target) {
        JJKAbilities.SWAP_SELF.get().run(owner, target);
    }

    @Override
    public int getCooldown() {
        return JJKAbilities.SWAP_SELF.get().getCooldown();
    }
}