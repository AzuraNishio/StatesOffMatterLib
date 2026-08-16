package azura.somlib.sparse_material;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class SparseMaterialState {
    SparseMaterial type;
    float concentration;
    float previousConcentration;

    public SparseMaterialState(SparseMaterial type, float concentration){
        this.concentration = concentration;
        this.previousConcentration = 0f;
        this.type = type;
    }

    public SparseMaterialState(Identifier type, float concentration){
        this.concentration = concentration;
        this.previousConcentration = 0f;
        this.type = SparseMaterialRegistry.MATERIAL_TYPES.get(type);
    }

    public SparseMaterialState(){
        this.concentration = 0f;
        this.previousConcentration = 0f;
        this.type = null;
    }

    public void applyEffectOnEntity(Entity entity) {
        this.type.applyEffectOnEntity(entity, this);
    }

    public float getConcentration() {
        return concentration;
    }

    public float getPreviousConcentration() {
        return previousConcentration;
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
        return this.concentration > 0.25;
    }

    public SparseMaterialState copy(){
        return new SparseMaterialState(this.type, this.concentration);
    }

    public void updateLastConcentration() {
        this.previousConcentration = concentration;
    }
}
