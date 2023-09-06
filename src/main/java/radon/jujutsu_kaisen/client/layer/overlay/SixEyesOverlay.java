package radon.jujutsu_kaisen.client.layer.overlay;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class SixEyesOverlay extends RenderableOverlay {
    @Override
    public RenderType getRenderType() {
        return RenderType.eyes(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/overlay/six_eyes.png"));
    }

    @Override
    public int getPackedLight() {
        return 15728640;
    }

    @Override
    public void init(LivingEntity owner) {

    }

    @Override
    public CompoundTag addCustomData() {
        return new CompoundTag();
    }

    @Override
    public void readCustomData(CompoundTag nbt) {

    }
}
