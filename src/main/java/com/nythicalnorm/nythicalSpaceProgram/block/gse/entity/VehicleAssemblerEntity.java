package com.nythicalnorm.nythicalSpaceProgram.block.gse.entity;

import com.nythicalnorm.nythicalSpaceProgram.block.BlockFindingStorage;
import com.nythicalnorm.nythicalSpaceProgram.block.gse.screen.VehicleAssemblerMenu;
import com.nythicalnorm.nythicalSpaceProgram.block.manufacturing.entity.NSPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VehicleAssemblerEntity extends BlockEntity implements MenuProvider {
    public VehicleAssemblerEntity( BlockPos pPos, BlockState pBlockState) {
        super(NSPBlockEntities.VEHICLE_ASSEMBLER_BE.get(), pPos, pBlockState);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.nythicalspaceprogram.vehicle_assembler");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new VehicleAssemblerMenu(pContainerId,this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null) {
            if (!this.level.isClientSide()) {
                BlockFindingStorage.makeBlockEntityFindable(getBlockPos(), level);
            }
        }
        recalculateBoundingBox();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (this.level != null) {
            if (!this.level.isClientSide()) {
                BlockFindingStorage.destroyBlockEntityFindable(getBlockPos(), level);
            }
        }
    }

    public void recalculateBoundingBox() {
        float x = 1;
    }
}
