package azura.somlib.sparse_material;


import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class SparseMaterial {
    public SparseMaterial(){}

    public boolean affectsEntity(Entity entity){return false;}
    public void effectOnEntity(Entity entity) {}
    public float diffusionChance(){return 0.2f;}
    public float diffusionDivision() {return 0.6f;}
    public float maxConcentration() {return 10f;}

    public Direction getRandomMoveDirection(Random r) {return Direction.random(r);}

    public Identifier textureId() {
        return Identifier.of("statesofmatterlib", "textures/test/icon.png");
    }
}