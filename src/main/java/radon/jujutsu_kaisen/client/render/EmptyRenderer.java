package radon.jujutsu_kaisen.client.render;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EmptyRenderer<T extends Entity> extends EntityRenderer<T> {
    public EmptyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}