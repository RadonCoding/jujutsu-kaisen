package radon.jujutsu_kaisen.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.effect.base.PostEffect;
import radon.jujutsu_kaisen.entity.effect.ScissorEntity;

import java.io.IOException;


public class ScissorPostEffect extends PostEffect {
    private static final ResourceLocation EFFECT = new ResourceLocation(JujutsuKaisen.MOD_ID, "shaders/post/scissor.json");

    @Override
    public PostChain create() {
        Minecraft mc = Minecraft.getInstance();

        try {
            PostChain chain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), EFFECT);
            PostEffectHandler.preparePostEffect(chain);
            return chain;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean shouldRender() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return false;

        for (ScissorEntity scissor : mc.level.getEntitiesOfClass(ScissorEntity.class, AABB.ofSize(mc.player.position(),
                16.0D, 16.0D, 16.0D))) {
            if (scissor.getVictim() == mc.player) return true;
        }
        return false;
    }
}