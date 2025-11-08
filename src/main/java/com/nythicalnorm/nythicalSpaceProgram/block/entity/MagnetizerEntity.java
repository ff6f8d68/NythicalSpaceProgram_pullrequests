package com.nythicalnorm.nythicalSpaceProgram.block.entity;

import com.nythicalnorm.nythicalSpaceProgram.screen.MagnetizerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MagnetizerEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler InputItemHandler = new ItemStackHandler(1);
    private final ItemStackHandler OutputItemHandler = new ItemStackHandler(1);

    private LazyOptional<IItemHandler> lazyInputItemHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyOutputItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    public MagnetizerEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MAGNETIZER_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> MagnetizerEntity.this.progress;
                    case 1 -> MagnetizerEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                 switch (pIndex) {
                    case 0 -> MagnetizerEntity.this.progress = pValue;
                    case 1 -> MagnetizerEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.DOWN) {
                return lazyOutputItemHandler.cast();
            }
            else {
                return lazyInputItemHandler.cast();
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyInputItemHandler = LazyOptional.of(() ->  InputItemHandler);
        lazyOutputItemHandler = LazyOptional.of(() ->  OutputItemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyInputItemHandler.invalidate();
        lazyOutputItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(InputItemHandler.getSlots() + OutputItemHandler.getSlots());

        inventory.setItem(0, InputItemHandler.getStackInSlot(0));
        inventory.setItem(1, OutputItemHandler.getStackInSlot(0));

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.nythicalspaceprogram.magnetizer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int ContainerId, Inventory inventory, Player player) {
        return new MagnetizerMenu(ContainerId, inventory, this, this.data);
    }

    public  <T> LazyOptional<T> getSlotForDisplay(int id) {
        if (id == 0) {
            return lazyInputItemHandler.cast();
        }
        else {
            return lazyOutputItemHandler.cast();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventoryIn", InputItemHandler.serializeNBT());
        pTag.put("inventoryOut", OutputItemHandler.serializeNBT());
        pTag.putInt("magnetizer.progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        InputItemHandler.deserializeNBT(pTag.getCompound("inventoryIn"));
        OutputItemHandler.deserializeNBT(pTag.getCompound("inventoryOut"));
        progress = pTag.getInt("magnetizer.progress");
        super.load(pTag);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (hasRecipe()) {
            progress++;
            setChanged(pLevel, pPos, pState);
            if (progress >= maxProgress) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        ItemStack result = new ItemStack(Items.GOLD_INGOT, 1);
        this.InputItemHandler.extractItem(0, 1, false);

        this.OutputItemHandler.setStackInSlot(0, new ItemStack(result.getItem(),
                this.OutputItemHandler.getStackInSlot(0).getCount() + result.getCount()));
    }

    private boolean hasRecipe() {
        boolean hasCraftingItem = this.InputItemHandler.getStackInSlot(0).getItem() == Items.IRON_INGOT;
        ItemStack result = new ItemStack(Items.GOLD_INGOT);

        return hasCraftingItem && camInsertAmountIntoOutputSlot(result.getCount()) && camInsertItemIntoOutputSlot((result.getItem()));
    }

    private boolean camInsertItemIntoOutputSlot(Item item) {
        return this.OutputItemHandler.getStackInSlot(0).isEmpty() || this.OutputItemHandler.getStackInSlot(0).is(item);
    }

    private boolean camInsertAmountIntoOutputSlot(int count) {
        return this.OutputItemHandler.getStackInSlot(0).getCount() + count  <= this.OutputItemHandler.getStackInSlot(0).getMaxStackSize();
    }
}
