package radon.jujutsu_kaisen.menu;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.entity.sorcerer.TojiFushiguroEntity;

public class BountyMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    private final @Nullable TojiFushiguroEntity entity;
    private final Container container;

    private final DataSlot cost = DataSlot.standalone();

    public BountyMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess, @Nullable TojiFushiguroEntity entity) {
        super(JJKMenus.BOUNTY.get(), pContainerId);

        this.access = pAccess;

        this.entity = entity;
        this.container = new SimpleContainer(1) {
            public void setChanged() {
                super.setChanged();

                BountyMenu.this.slotsChanged(this);
            }
        };
        this.addSlot(new Slot(this.container, 0, 144, 25) {
            @Override
            public boolean mayPlace(@NotNull ItemStack pStack) {
                return pStack.is(Items.EMERALD);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlot(this.cost);
    }

    public BountyMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL, null);
    }

    public boolean charge() {
        Slot slot = this.getSlot(0);

        if (slot.hasItem() && slot.getItem().getCount() >= this.cost.get()) {
            slot.getItem().shrink(this.cost.get());
            return true;
        }
        return false;
    }

    public int getCost() {
        return this.cost.get();
    }

    public void setCost(int cost) {
        this.cost.set(cost);
    }

    @Nullable
    public TojiFushiguroEntity getEntity() {
        return this.entity;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);

        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            result = current.copy();

            if (pIndex == 0) {
                if (!this.moveItemStackTo(current, 28, 37, true)) {
                    if (!this.moveItemStackTo(current, 1, 28, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                if (current.is(Items.EMERALD)) {
                    if (!this.moveItemStackTo(current, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (current.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (current.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, current);
        }
        return result;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return this.entity != null && this.entity.getCurrentCustomer() == pPlayer;
    }

    @Override
    public void removed(@NotNull Player pPlayer) {
        super.removed(pPlayer);

        this.access.execute((level, pos) ->
                this.clearContainer(pPlayer, this.container));

        if (this.entity != null) {
            this.entity.stopTrading();
        }
    }
}