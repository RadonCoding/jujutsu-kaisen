package radon.jujutsu_kaisen.mixin.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(SkinManager.class)
public interface ISkinManagerAccessor {
    @Invoker
    ResourceLocation invokeRegisterTexture(MinecraftProfileTexture pProfileTexture, MinecraftProfileTexture.Type pTextureType, @Nullable SkinManager.SkinTextureCallback pSkinAvailableCallback);
}
