package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.EntityCapability;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract AABB getBoundingBox();

    @Shadow public abstract Level level();

    @Shadow @Nullable public abstract <T> T getCapability(EntityCapability<T, Void> capability);

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

    @Inject(method = "isPushable", at = @At("TAIL"), cancellable = true)
    public void isPushable(CallbackInfoReturnable<Boolean> cir) {
        if (this.level().isClientSide) {
            ClientVisualHandler.ClientData client = ClientVisualHandler.get((Entity) (Object) this);

            if (client == null || !client.toggled.contains(JJKAbilities.INFINITY.get())) return;

            cir.setReturnValue(false);
        } else {
            IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(JJKAbilities.INFINITY.get())) return;

            cir.setReturnValue(false);
        }
    }
}
