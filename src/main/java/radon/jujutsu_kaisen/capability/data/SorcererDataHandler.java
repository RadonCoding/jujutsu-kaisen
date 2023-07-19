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

public class SorcererDataHandler {
    public static Capability<ISorcererData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        SorcererDataProvider provider = new SorcererDataProvider();
        event.addCapability(SorcererDataProvider.IDENTIFIER, provider);
    }

    private static class SorcererDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "sorcerer_data");

        private final ISorcererData cap = new SorcererData();
        private final LazyOptional<ISorcererData> optional = LazyOptional.of(() -> this.cap);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == INSTANCE ? this.optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.cap.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.cap.deserializeNBT(nbt);
        }
    }
}
