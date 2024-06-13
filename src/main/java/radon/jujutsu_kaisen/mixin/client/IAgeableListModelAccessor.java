package radon.jujutsu_kaisen.mixin.client;


import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AgeableListModel.class)
public interface IAgeableListModelAccessor {
    @Invoker
    Iterable<ModelPart> invokeHeadParts();

    @Invoker
    Iterable<ModelPart> invokeBodyParts();
}
