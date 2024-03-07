package radon.jujutsu_kaisen.item.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.client.render.item.armor.ArmBladeRenderer;
import radon.jujutsu_kaisen.client.render.item.armor.GunRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class GunItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GunItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private static final HumanoidModel.ArmPose POSE = HumanoidModel.ArmPose.create("gun", true, (model, entity, arm) -> {
                model.leftArm.yRot = 0.8F + model.head.yRot;
                model.rightArm.yRot = model.head.yRot;
                model.leftArm.xRot = (-(float)Math.PI / 2.0F) + model.head.xRot + 0.1F;
                model.rightArm.xRot = -1.5F + model.head.xRot;
            });

            private GunRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) this.renderer = new GunRenderer();
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }

            @Override
            public HumanoidModel.@NotNull ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return POSE;
            }

            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                poseStack.translate(0.7625F, -0.4F, 1.5F);
                poseStack.mulPose(Axis.YN.rotationDegrees(6.0F));
                poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
                poseStack.scale(2.5F, 2.5F, 2.5F);

                float f = Mth.sqrt(swingProcess);
                float f1 = Mth.sin(f * Mth.PI);
                float f2 = 0.4F * Mth.sin(f * (Mth.PI * 2.0F));
                float f3 = -0.3F * Mth.sin(swingProcess * Mth.PI);
                poseStack.translate(0.0F, f2 - 0.3F * f1, f3);

                return true;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}