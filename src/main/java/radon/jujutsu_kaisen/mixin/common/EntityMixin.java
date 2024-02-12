package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract AABB getBoundingBox();

    @Inject(method = "getTeamColor", at = @At("TAIL"), cancellable = true)
    public void getTeamColor(CallbackInfoReturnable<Integer> cir) {
        ClientVisualHandler.ClientData client = ClientVisualHandler.get((Entity) (Object) this);

        if (client == null || !client.toggled.contains(JJKAbilities.DOMAIN_AMPLIFICATION.get())) return;

        Vector3f color = ParticleColors.getCursedEnergyColor((Entity) (Object) this);

        int r = (int) (color.x * 255.0D);
        int g = (int) (color.y * 255.0D);
        int b = (int) (color.z * 255.0D);

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        cir.setReturnValue((r << 16) | (g << 8) | b);
    }
}
