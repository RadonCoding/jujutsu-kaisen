package radon.jujutsu_kaisen.item.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
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
import radon.jujutsu_kaisen.entity.sorcerer.TojiFushiguroEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class InventoryCurseItem extends ArmorItem implements GeoItem, MenuProvider, ICurioItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public InventoryCurseItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
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
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
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

    public static void addItem(ItemStack inventory, int slot, ItemStack stack) {
        CompoundTag nbt = inventory.getOrCreateTag();
        ListTag itemsTag = nbt.getList("items", Tag.TAG_COMPOUND);
        itemsTag.add(slot, stack.save(new CompoundTag()));
        nbt.put("items", itemsTag);
    }

    public static void removeItem(ItemStack inventory, int slot) {
        CompoundTag nbt = inventory.getOrCreateTag();
        ListTag itemsTag = nbt.getList("items", Tag.TAG_COMPOUND);

        if (itemsTag.size() - 1 >= slot) {
            itemsTag.remove(slot);
        }
        nbt.put("items", itemsTag);
    }

    public static void clear(ItemStack inventory) {
        CompoundTag nbt = inventory.getOrCreateTag();
        nbt.remove("items");
    }

    public static ItemStack getItem(ItemStack inventory, int slot) {
        CompoundTag nbt = inventory.getOrCreateTag();
        ListTag itemsTag = nbt.getList("items", Tag.TAG_COMPOUND);
        return ItemStack.of(itemsTag.getCompound(slot));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        CompoundTag nbt = pPlayer.getItemBySlot(EquipmentSlot.CHEST).getOrCreateTag();

        AtomicInteger previous = new AtomicInteger(nbt.getList("items", Tag.TAG_COMPOUND).size());

        SimpleContainer container = new SimpleContainer(9);
        container.fromTag(nbt.getList("items", Tag.TAG_COMPOUND));
        container.addListener(pContainer -> {
            nbt.put("items", ((SimpleContainer) pContainer).createTag());

            int current = nbt.getList("items", Tag.TAG_COMPOUND).size();

            if (current > previous.get()) {
                pPlayer.level().playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), JJKSounds.SWALLOW.get(), SoundSource.MASTER, 1.0F, 1.0F);
            }
            previous.set(current);
        });
        return new ChestMenu(MenuType.GENERIC_9x1, pContainerId, pPlayerInventory, container, container.getContainerSize() / 9);
    }
}