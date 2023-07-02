package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import radon.jujutsu_kaisen.client.model.RugbyFieldCurseModel;
import radon.jujutsu_kaisen.entity.RugbyFieldCurseEntity;

public class RugbyFieldCurseRenderer extends CurseRenderer<RugbyFieldCurseEntity> {
    public RugbyFieldCurseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RugbyFieldCurseModel());
    }
}
