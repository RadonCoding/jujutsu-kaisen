package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;

import java.util.ArrayList;
import java.util.List;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"))
    public Iterable<Entity> entitiesForRendering(ClientLevel instance) {
        Iterable<Entity> iter = instance.entitiesForRendering();

        List<Entity> result = new ArrayList<>();

        iter.forEach(entity -> {
            result.add(entity);

            if (!entity.isMultipartEntity()) return;

            for (PartEntity<?> part : entity.getParts()) {
                if (part instanceof JJKPartEntity<?>) {
                    result.add(part);
                }
            }
        });
        return result;
    }
}
