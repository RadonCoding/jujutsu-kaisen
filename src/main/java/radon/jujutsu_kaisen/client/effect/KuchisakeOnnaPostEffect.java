package radon.jujutsu_kaisen.client.effect;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.effect.base.PostEffect;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnna;

import java.util.concurrent.atomic.AtomicBoolean;

public class KuchisakeOnnaPostEffect extends PostEffect {
    private static final ResourceLocation EFFECT = new ResourceLocation(JujutsuKaisen.MOD_ID, "shaders/post/kuchisake_onna.json");

    @Override
    protected ResourceLocation getEffect() {
        return EFFECT;
    }

    @Override
    public boolean shouldRender(LocalPlayer player) {
        AtomicBoolean result = new AtomicBoolean();

        for (KuchisakeOnna curse : player.level.getEntitiesOfClass(KuchisakeOnna.class, AABB.ofSize(player.position(),
                64.0D, 64.0D, 64.0D))) {
            if (result.get()) break;

            curse.getCurrent().ifPresent(identifier ->
                    result.set(identifier.equals(player.getUUID())));
        }
        return result.get();
    }
}