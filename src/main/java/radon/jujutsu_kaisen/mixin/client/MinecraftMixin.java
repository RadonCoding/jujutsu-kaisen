package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.curse_manipulation.AbsorbCurse;
import radon.jujutsu_kaisen.client.gui.overlay.MeleeAbilityOverlay;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Redirect(method = "shouldEntityAppearGlowing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isCurrentlyGlowing()Z"))
    public boolean isCurrentlyGlowing(Entity instance) {
        if (instance.isCurrentlyGlowing()) return true;

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !player.hasLineOfSight(instance)) return false;

        ClientVisualHandler.VisualData data = ClientVisualHandler.getOrRequest(instance);

        if (data != null && data.toggled().contains(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
            return true;
        }

        if (!(instance instanceof LivingEntity entity)) return false;

        return MeleeAbilityOverlay.getSelected() == JJKAbilities.ABSORB_CURSE.get() &&
                AbsorbCurse.canAbsorb(player, entity);
    }
}
