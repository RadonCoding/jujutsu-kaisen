package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SorcererDataHandler {
    public static Capability<ISorcererData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player player = event.getEntity();

        original.reviveCaps();

        ISorcererData oldCap = original.getCapability(INSTANCE).resolve().orElseThrow();
        ISorcererData newCap = player.getCapability(INSTANCE).resolve().orElseThrow();

        newCap.deserializeNBT(oldCap.serializeNBT());

        if (event.isWasDeath()) {
            newCap.setEnergy(newCap.getMaxEnergy());
            newCap.resetCooldowns();
            newCap.resetBurnout();
            newCap.clearToggled();
            
            newCap.resetBlackFlash();
            newCap.resetExtraEnergy();
            newCap.resetSpeedStacks();

            if (!player.level().isClientSide) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(newCap.serializeNBT()), (ServerPlayer) player);
            }
        }
        original.invalidateCaps();
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity entity) {
            if (entity instanceof Player || entity instanceof ISorcerer) {
                SorcererDataProvider provider = new SorcererDataProvider();
                ISorcererData cap = provider.getCapability(INSTANCE).resolve().orElseThrow();
                cap.init(entity);
                event.addCapability(SorcererDataProvider.IDENTIFIER, provider);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ISorcererData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ISorcererData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ISorcererData cap = player.getCapability(INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    public static class SorcererDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "sorcerer_data");

        private ISorcererData cap = null;
        private final LazyOptional<ISorcererData> optional = LazyOptional.of(this::create);

        private ISorcererData create() {
            if (this.cap == null) {
                this.cap = new SorcererData();
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
