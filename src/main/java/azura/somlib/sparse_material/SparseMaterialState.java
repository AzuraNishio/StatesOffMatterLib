package azura.somlib.sparse_material;

import net.minecraft.util.Identifier;

public class SparseMaterialState {
    SparseMaterial type;
    float concentration;

    public SparseMaterialState(SparseMaterial type, float concentration){
        this.concentration = concentration;
        this.type = type;
    }

    public SparseMaterialState(Identifier type, float concentration){
        this.concentration = concentration;
        this.type = SparseMaterialRegistry.MATERIAL_TYPES.get(type);
    }

    public SparseMaterialState(){
        this.concentration = 0f;
        this.type = null;
    }

    public float getConcentration() {
        return concentration;
    }

    public SparseMaterial getType() {
        return type;
    }

    public SparseMaterialState addConcentration(float increment){
        this.concentration += increment;
        return this;
    }

    public SparseMaterialState multiplyConcentration(float k){
        this.concentration *= k;
        return this;
    }

    public boolean addTogether(SparseMaterialState value) {
        this.concentration += value.getConcentration();

        return this.concentration > 0.5;
    }

    public SparseMaterialState copy(){
        return new SparseMaterialState(this.type, this.concentration);
    }
}
