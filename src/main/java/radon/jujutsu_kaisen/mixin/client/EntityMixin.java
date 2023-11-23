package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.effect.JJKEffect;
import radon.jujutsu_kaisen.effect.JJKEffects;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    public void isInvisible(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = ((Entity) (Object) this);

        if (!(entity instanceof LivingEntity living)) return;

        if (living.hasEffect(JJKEffects.INVISIBILITY.get())) {
            cir.setReturnValue(true);
        }

        if (entity instanceof Player && Minecraft.getInstance().player == entity &&
                Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) return;

        ClientVisualHandler.VisualData data = ClientVisualHandler.getOrRequest(living);

        if (data != null && data.toggled().contains(JJKAbilities.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING.get())) {
            cir.setReturnValue(true);
        }
    }
}
