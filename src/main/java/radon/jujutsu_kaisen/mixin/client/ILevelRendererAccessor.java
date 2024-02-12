package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface ILevelRendererAccessor {
    @Accessor("level")
    ClientLevel getLevelAccessor();

    @Accessor("level")
    void setLevelAccessor(ClientLevel level);
}
