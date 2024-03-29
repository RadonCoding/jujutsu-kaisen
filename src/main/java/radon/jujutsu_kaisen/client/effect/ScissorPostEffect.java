package radon.jujutsu_kaisen.client.effect;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.effect.base.PostEffect;
import radon.jujutsu_kaisen.entity.effect.ScissorEntity;


public class ScissorPostEffect extends PostEffect {
    private static final ResourceLocation EFFECT = new ResourceLocation(JujutsuKaisen.MOD_ID, "shaders/post/scissor.json");

    @Override
    protected ResourceLocation getEffect() {
        return EFFECT;
    }

    @Override
    public boolean shouldRender(LocalPlayer player) {
        for (ScissorEntity scissor : player.level().getEntitiesOfClass(ScissorEntity.class, AABB.ofSize(player.position(),
                16.0D, 16.0D, 16.0D))) {
            if (scissor.getVictim() == player) return true;
        }
        return false;
    }
}