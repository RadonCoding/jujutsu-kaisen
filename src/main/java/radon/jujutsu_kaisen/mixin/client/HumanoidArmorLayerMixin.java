package radon.jujutsu_kaisen.mixin.client;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.client.ClientHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.ITransformation;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
    @Shadow
    protected abstract void setPartVisibility(A pModel, EquipmentSlot pSlot);

    @Shadow
    protected abstract A getArmorModel(EquipmentSlot pSlot);

    @Shadow
    protected abstract Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model);

    @Shadow
    protected abstract boolean usesInnerModel(EquipmentSlot pSlot);

    @Shadow protected abstract void renderModel(PoseStack p_289664_, MultiBufferSource p_289689_, int p_289681_, Model p_289658_, float p_289678_, float p_289674_, float p_289693_, ResourceLocation p_324344_);

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
        ClientVisualHandler.ClientData client = ClientVisualHandler.get(pLivingEntity);

        if (client == null) return;

        Map<Ability, ITransformation.Part> parts = new HashMap<>();

        for (Ability ability : client.toggled) {
            if (!(ability instanceof ITransformation transformation)) continue;

            if (transformation.isReplacement()) {
                parts.put(ability, transformation.getBodyPart());
            }
        }

        Set<EquipmentSlot> hidden = new HashSet<>();

        for (Ability ability : client.toggled) {
            if (!(ability instanceof ITransformation transformation)) continue;

            if (transformation.isReplacement()) {
                for (Map.Entry<Ability, ITransformation.Part> entry : parts.entrySet()) {
                    if (entry.getKey() == ability || (entry.getValue() != ITransformation.Part.BODY && entry.getValue() != transformation.getBodyPart())) continue;
                    hidden.add(transformation.getBodyPart().getSlot());
                    break;
                }
            }
        }

        for (Ability ability : client.toggled) {
            if (!(ability instanceof ITransformation transformation)) continue;

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (transformation.getBodyPart().getSlot() != slot && hidden.contains(slot)) continue;

                RenderLayer<T, M> child = (RenderLayer<T, M>) (Object) this;

                A model = this.getArmorModel(slot);

                child.getParentModel().copyPropertiesTo(model);
                this.setPartVisibility(model, slot);

                pPoseStack.pushPose();

                if (transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM ||
                        transformation.getBodyPart() == ITransformation.Part.LEFT_ARM) {
                    if (child.getParentModel() instanceof PlayerModel<?> player) {
                        if (player.slim) {
                            float translation = transformation.getSlimTranslation();
                            pPoseStack.translate(transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM ? translation : -translation, 0.0F, 0.0F);
                        }
                    }
                }

                ItemStack stack = transformation.getItem().getDefaultInstance();
                int i = stack.is(ItemTags.DYEABLE) ? DyedItemColor.getOrDefault(stack, -6265536) : -1;

                Model custom = this.getArmorModelHook(pLivingEntity, stack, slot, model);
                boolean inner = this.usesInnerModel(slot);

                ArmorItem armor = (ArmorItem) transformation.getItem().asItem();

                for (ArmorMaterial.Layer layer : armor.getMaterial().value().layers()) {
                    float r;
                    float g;
                    float b;

                    if (layer.dyeable() && i != -1) {
                        r = (float) FastColor.ARGB32.red(i) / 255.0F;
                        g = (float)FastColor.ARGB32.green(i) / 255.0F;
                        b = (float)FastColor.ARGB32.blue(i) / 255.0F;
                    } else {
                        r = 1.0F;
                        g = 1.0F;
                        b = 1.0F;
                    }
                    this.renderModel(pPoseStack, pBuffer, pPackedLight, custom, r, g, b,
                            ClientHooks.getArmorTexture(pLivingEntity, stack, layer, inner, slot));
                }
                pPoseStack.popPose();
            }
        }
    }

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;setPartVisibility(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/EquipmentSlot;)V", shift = At.Shift.AFTER))
    public void renderArmorPiece(PoseStack pPoseStack, MultiBufferSource pBuffer, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel, CallbackInfo ci) {
        ClientVisualHandler.ClientData client = ClientVisualHandler.get(pLivingEntity);

        if (client == null) return;

        for (Ability ability : client.toggled) {
            if (!(ability instanceof ITransformation transformation)) continue;

            if (transformation.isReplacement()) {
                switch (transformation.getBodyPart()) {
                    case HEAD -> {
                        pModel.head.visible = false;
                        pModel.hat.visible = false;
                    }
                    case BODY -> pModel.setAllVisible(false);
                    case RIGHT_ARM -> pModel.rightArm.visible = false;
                    case LEFT_ARM -> pModel.leftArm.visible = false;
                    case LEGS -> {
                        pModel.rightLeg.visible = false;
                        pModel.leftLeg.visible = false;
                    }
                }
            }
        }
    }
}
