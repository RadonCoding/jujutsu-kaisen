package radon.jujutsu_kaisen.block.menu;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.VeilRodItem;
import radon.jujutsu_kaisen.item.veil.*;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.HashMap;
import java.util.Map;

public class AltarMenu extends ItemCombinerMenu {
    private static final Map<Item, ResourceLocation> ENTITY_DROPS = new HashMap<>();

    public AltarMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(JJKMenus.ALTAR.get(), pContainerId, pPlayerInventory, pAccess);

        if (ENTITY_DROPS.isEmpty()) {
            pAccess.evaluate((level, pos) -> {
                ENTITY_DROPS.put(Items.ROTTEN_FLESH, getKey(level.registryAccess(), EntityType.ZOMBIE));
                ENTITY_DROPS.put(Items.BONE, getKey(level.registryAccess(), EntityType.SKELETON));
                ENTITY_DROPS.put(Items.GUNPOWDER, getKey(level.registryAccess(), EntityType.CREEPER));
                return true;
            });
        }
    }

    public AltarMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }

    private static ResourceLocation getKey(RegistryAccess access, EntityType<?> type) {
        return access.registryOrThrow(Registries.ENTITY_TYPE).getKey(type);
    }

    @Override
    protected @NotNull ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 29, 17, stack -> stack.is(JJKItemTags.ALTAR))
                .withSlot(1, 47, 17, stack -> stack.is(JJKItemTags.ALTAR))
                .withSlot(2, 65, 17, stack -> stack.is(JJKItemTags.ALTAR))
                .withSlot(3, 29, 35, stack -> stack.is(JJKItemTags.ALTAR))
                .withSlot(4, 47, 35, stack -> stack.getCount() == 1 && stack.is(JJKItems.VEIL_ROD.get()))
                .withSlot(5, 65, 35, stack -> stack.is(JJKItemTags.ALTAR))
                .withSlot(6, 29, 53, stack -> stack.is(JJKItemTags.ALTAR))
                .withSlot(7, 47, 53, stack -> stack.is(JJKItemTags.ALTAR))
                .withSlot(8, 65, 53, stack -> stack.is(JJKItemTags.ALTAR))
                .withResultSlot(9, 131, 35)
                .build();
    }

    @Override
    protected boolean mayPickup(@NotNull Player pPlayer, boolean pHasStack) {
        return true;
    }

    @Override
    protected void onTake(@NotNull Player pPlayer, @NotNull ItemStack pStack) {
        for (int i = 0; i < this.inputSlots.getContainerSize(); i++) {
            ItemStack slot = this.inputSlots.getItem(i);

            if (!slot.isEmpty()) {
                this.inputSlots.setItem(i, ItemStack.EMPTY);
            }
        }
        this.access.execute((level, pos) ->
                level.playLocalSound(pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, HelperMethods.RANDOM.nextFloat() * 0.1F + 0.9F, false));
    }

    @Override
    protected boolean isValidBlock(@NotNull BlockState pState) {
        return true;
    }

    private static Modifier getModifier(ItemStack stack) {
        if (stack.is(Items.NAME_TAG)) {
            return new PlayerBlacklistModifier(stack.getHoverName().getString());
        } else if (stack.getItem() instanceof DyeItem dye) {
            return new ColorModifier(dye.getDyeColor());
        } else if (stack.is(Items.GLASS)) {
            return new TransparentModifier();
        } else if (ENTITY_DROPS.containsKey(stack.getItem())) {
            return new EntityBlacklistModifier(ENTITY_DROPS.get(stack.getItem()));
        }
        throw new NotImplementedException();
    }

    @Override
    public void createResult() {
        ItemStack stack = this.inputSlots.getItem(4);

        if (stack.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            ItemStack copy = stack.copy();
            copy.setCount(4);

            for (int i = 0; i < this.inputSlots.getContainerSize(); i++) {
                if (i == 4) continue;

                ItemStack slot = this.inputSlots.getItem(i);

                if (!slot.isEmpty()) {
                    VeilRodItem.setModifier(copy, i > 4 ? i - 1 : i, getModifier(slot));
                }
            }
            this.resultSlots.setItem(0, copy);
            this.broadcastChanges();
        }
    }
}