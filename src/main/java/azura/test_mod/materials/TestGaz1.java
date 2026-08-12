package azura.test_mod.materials;

import azura.somlib.sparse_material.SparseMaterial;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class TestGaz1 extends SparseMaterial {
    public static Direction getRandomDirectionBias(float top, float down, float sideways, Random r){
        float v = (r.nextFloat() * (top + down + (sideways * 4f)));

        if (v < top) return Direction.UP;
        if (v < top + down) return Direction.DOWN;
        if (v < top + down + sideways) return Direction.NORTH;
        if (v < top + down + sideways + sideways) return Direction.SOUTH;
        if (v < top + down + sideways + sideways + sideways) return Direction.EAST;
        if (v <= top + down + sideways + sideways + sideways + sideways) return Direction.WEST;
        return null;
    }



    @Override
    public float diffusionChance(){return 0.2f;}
    @Override
    public float diffusionDivision() {return 0.5f;}
    @Override
    public float maxConcentration() {return 5f;}
    @Override
    public Direction getRandomMoveDirection(Random r) {
        return getRandomDirectionBias(3, 1, 2, r);
    }
}
