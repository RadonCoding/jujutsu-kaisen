package radon.jujutsu_kaisen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.model.item.armor.InstantSpiritBodyOfDistortedKillingModel;

public class JJKResourceManager implements ResourceManagerReloadListener {
    public static InstantSpiritBodyOfDistortedKillingModel<?> instantSpiritBodyOfDistortedKillingModel;

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager pResourceManager) {
        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
        instantSpiritBodyOfDistortedKillingModel = new InstantSpiritBodyOfDistortedKillingModel<>(modelSet.bakeLayer(InstantSpiritBodyOfDistortedKillingModel.LAYER_LOCATION));
    }
}
