package radon.jujutsu_kaisen.capability.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class OverlayDataHandler {
    public static Capability<IOverlayData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        OverlayDataProvider provider = new OverlayDataProvider();
        event.addCapability(OverlayDataProvider.IDENTIFIER, provider);
    }

    private static class OverlayDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "overlay_data");

        private IOverlayData cap = null;
        private final LazyOptional<IOverlayData> optional = LazyOptional.of(this::create);

        private IOverlayData create() {
            if (this.cap == null) {
                this.cap = new OverlayData();
            }
            return this.cap;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == INSTANCE ? this.optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.create().serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.create().deserializeLocalNBT(nbt);
        }
    }
}
