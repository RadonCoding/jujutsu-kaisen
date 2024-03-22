package radon.jujutsu_kaisen.client.render.entity.sorcerer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.sorcerer.SorcererVillager;

public class SorcererVillagerRenderer extends MobRenderer<SorcererVillager, VillagerModel<SorcererVillager>> {
    private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/villager/villager.png");

    public SorcererVillagerRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new VillagerModel<>(pContext.bakeLayer(ModelLayers.VILLAGER)), 0.5F);

        this.addLayer(new CustomHeadLayer<>(this, pContext.getModelSet(), pContext.getItemInHandRenderer()));
        this.addLayer(new CrossedArmsItemLayer<>(this, pContext.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SorcererVillager pEntity) {
        return VILLAGER_BASE_SKIN;
    }
}

