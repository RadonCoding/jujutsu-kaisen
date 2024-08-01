package radon.jujutsu_kaisen.client.layer;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nonnull;

public class CuriosLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final RenderLayerParent<T, M> renderLayerParent;

    public CuriosLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);

        this.renderLayerParent = renderer;
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight, @Nonnull T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || pLivingEntity.isInvisibleTo(mc.player)) return;

        CuriosApi.getCuriosInventory(pLivingEntity)
                .ifPresent(handler -> handler.getCurios().forEach((id, stacksHandler) -> {
                    IDynamicStackHandler stackHandler = stacksHandler.getStacks();
                    IDynamicStackHandler cosmeticStacksHandler = stacksHandler.getCosmeticStacks();

                    for (int i = 0; i < stackHandler.getSlots(); i++) {
                        ItemStack stack = cosmeticStacksHandler.getStackInSlot(i);
                        boolean cosmetic = true;
                        NonNullList<Boolean> renderStates = stacksHandler.getRenders();
                        boolean renderable = renderStates.size() > i && renderStates.get(i);

                        if (stack.isEmpty() && renderable) {
                            stack = stackHandler.getStackInSlot(i);
                            cosmetic = false;
                        }

                        if (!stack.isEmpty()) {
                            SlotContext slotContext = new SlotContext(id, pLivingEntity, i, cosmetic, renderable);
                            ItemStack finalStack = stack;
                            CuriosRendererRegistry.getRenderer(stack.getItem()).ifPresent(
                                    renderer -> renderer
                                            .render(finalStack, slotContext, pPoseStack, renderLayerParent,
                                                    pBuffer, pPackedLight, pLimbSwing, pLimbSwingAmount, pPartialTicks,
                                                    pAgeInTicks, pNetHeadYaw, pHeadPitch));
                        }
                    }
                }));
    }
}
