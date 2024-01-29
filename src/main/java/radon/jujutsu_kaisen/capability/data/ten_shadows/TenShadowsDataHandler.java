package radon.jujutsu_kaisen.capability.data.ten_shadows;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.base.ISorcerer;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TenShadowsDataHandler {
    public static Capability<ITenShadowsData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player player = event.getEntity();

        original.reviveCaps();

        ITenShadowsData oldCap = original.getCapability(INSTANCE).resolve().orElseThrow();
        ITenShadowsData newCap = player.getCapability(INSTANCE).resolve().orElseThrow();

        newCap.deserializeNBT(oldCap.serializeNBT());

        if (event.isWasDeath()) {
            if (!ConfigHolder.SERVER.realisticShikigami.get()) {
                newCap.revive(false);
            }
        }
        original.invalidateCaps();
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity entity) {
            if (entity instanceof Player || entity instanceof ISorcerer) {
                TenShadowsDataProvider provider = new TenShadowsDataProvider();
                ITenShadowsData cap = provider.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
                cap.init(entity);
                event.addCapability(TenShadowsDataProvider.IDENTIFIER, provider);
            }
        }
    }
    
    public static class TenShadowsDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "ten_shadows_data");

        private ITenShadowsData cap = null;
        private final LazyOptional<ITenShadowsData> optional = LazyOptional.of(this::create);

        private ITenShadowsData create() {
            if (this.cap == null) {
                this.cap = new TenShadowsData();
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
            this.create().deserializeNBT(nbt);
        }
    }
}
