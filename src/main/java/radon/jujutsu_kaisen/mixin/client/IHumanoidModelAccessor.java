package radon.jujutsu_kaisen.mixin.client;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HumanoidModel.class)
public interface IHumanoidModelAccessor {
    @Invoker
    Iterable<ModelPart> invokeHeadParts();

    @Invoker
    Iterable<ModelPart> invokeBodyParts();
}
