package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract UUID getUUID();

    @Inject(method = "getTeamColor", at = @At("TAIL"), cancellable = true)
    public void getTeamColor(CallbackInfoReturnable<Integer> cir) {
        ClientVisualHandler.VisualData data = ClientVisualHandler.getOrRequest((Entity) (Object) this);

        if (data == null || !data.toggled().contains(JJKAbilities.DOMAIN_AMPLIFICATION.get())) return;

        Vector3f color = ParticleColors.getCursedEnergyColor(data.type());

        int r = (int) (color.x() * 255.0D);
        int g = (int) (color.y() * 255.0D);
        int b = (int) (color.z() * 255.0D);

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        cir.setReturnValue((r << 16) | (g << 8) | b);
    }
}
