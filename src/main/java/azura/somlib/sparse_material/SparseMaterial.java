package azura.somlib.sparse_material;


import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import nishio.lazuli_lib.core.shaders.LazuliShader;

public class SparseMaterial {
    public SparseMaterial(){}

    public void applyEffectOnEntity(Entity entity, SparseMaterialState state) {}
    public float diffusionChance(){return 0.2f;}
    public float diffusionDivision() {return 0.6f;}
    public float maxConcentration() {return 10f;}

    public LazuliShader customShader() {return null;}
    public Direction getRandomMoveDirection(Random r) {return Direction.random(r);}

    public Identifier textureId() {
        return Identifier.of("statesofmatterlib", "textures/test/test.png");
    }
}