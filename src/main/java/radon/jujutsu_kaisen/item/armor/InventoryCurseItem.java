package radon.jujutsu_kaisen.item.armor;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.client.render.item.armor.InventoryCurseRenderer;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;
import radon.jujutsu_kaisen.sound.JJKSounds;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.function.Consumer;

public class InventoryCurseItem extends ArmorItem implements GeoItem, MenuProvider, ICurioItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public InventoryCurseItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @NotNull
    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return slotContext.entity() instanceof Player ? ICurioItem.super.getDropRule(slotContext, source, lootingLevel, recentlyHit, stack) : ICurio.DropRule.DESTROY;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private InventoryCurseRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot equipmentSlot, @NotNull HumanoidModel<?> original) {
                if (this.renderer == null) this.renderer = new InventoryCurseRenderer();
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
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

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(String.format("%s.desc", this.getDescriptionId()));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull net.minecraft.world.entity.player.Inventory pPlayerInventory, @NotNull Player pPlayer) {
        ItemStack chest = pPlayer.getItemBySlot(EquipmentSlot.CHEST);
        List<ItemStack> inventory = chest.get(JJKDataComponentTypes.HIDDEN_INVENTORY);

        if (inventory == null) return null;

        SimpleContainer container = new SimpleContainer(9);

        for (ItemStack stack : inventory) {
            container.addItem(stack);
        }

        chest.set(JJKDataComponentTypes.HIDDEN_INVENTORY, container.getItems());

        container.addListener(pContainer -> {
            pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), JJKSounds.SWALLOW.get(), SoundSource.MASTER, 1.0F, 1.0F);
        });
        return new ChestMenu(MenuType.GENERIC_9x1, pContainerId, pPlayerInventory, container, container.getContainerSize() / 9);
    }
}