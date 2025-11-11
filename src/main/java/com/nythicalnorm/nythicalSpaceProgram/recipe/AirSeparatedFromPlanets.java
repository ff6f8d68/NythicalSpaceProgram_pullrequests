package com.nythicalnorm.nythicalSpaceProgram.recipe;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class AirSeparatedFromPlanets {

    public static class ManufacturingFluid {
        public Fluid resultingFluid;
        public int airSeparatorHeightNeeded;
        public int tankCapacity;
        public int creationRate;

        public ManufacturingFluid(Fluid ResultingFluid, int AirSeparatorHeightNeeded, int TankCapacity, int CreationRate) {
            this.resultingFluid = ResultingFluid;
            this.airSeparatorHeightNeeded = AirSeparatorHeightNeeded;
            this.tankCapacity = TankCapacity;
            this.creationRate = CreationRate;
        }
    }

    private static final ManufacturingFluid[] OVERWORLD_FLUIDS = new ManufacturingFluid[]{
            new ManufacturingFluid(Fluids.LAVA, 0, 20000, 10),
            new ManufacturingFluid(Fluids.WATER, 10, 10000, 25),
            new ManufacturingFluid(Fluids.EMPTY, 20, 5000, 0)
    };

    public static ManufacturingFluid[] getRecipe(Level level) {
        if (level.dimensionType().bedWorks()) {
            return OVERWORLD_FLUIDS;
        }
        else {
            return null;
        }
    }
}
