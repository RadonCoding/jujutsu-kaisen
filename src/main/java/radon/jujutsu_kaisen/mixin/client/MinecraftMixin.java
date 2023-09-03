package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.curse_manipulation.AbsorbCurse;
import radon.jujutsu_kaisen.client.gui.overlay.MeleeAbilityOverlay;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Redirect(method = "shouldEntityAppearGlowing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isCurrentlyGlowing()Z"))
    public boolean isCurrentlyGlowing(Entity instance) {
        if (instance.isCurrentlyGlowing()) return true;

        LivingEntity owner = Minecraft.getInstance().player;

        if (owner == null) return false;

        return MeleeAbilityOverlay.getSelected() == JJKAbilities.ABSORB_CURSE.get() && owner.hasLineOfSight(instance) && AbsorbCurse.canAbsorb(owner, instance);
    }
}
