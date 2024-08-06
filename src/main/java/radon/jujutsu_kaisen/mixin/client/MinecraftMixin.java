package radon.jujutsu_kaisen.mixin.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.DimensionManager;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow @Nullable public ClientLevel level;

    @Redirect(method = "shouldEntityAppearGlowing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isCurrentlyGlowing()Z"))
    public boolean isCurrentlyGlowing(Entity instance) {
        if (instance.isCurrentlyGlowing()) return true;

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !player.hasLineOfSight(instance)) return false;

        ClientVisualHandler.ClientData client = ClientVisualHandler.get(instance);

        return client != null && client.toggled.contains(JJKAbilities.DOMAIN_AMPLIFICATION.get());
    }
}
