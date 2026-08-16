package azura.somlib.storage;

import azura.somlib.sparse_material.SparseMaterial;
import azura.somlib.sparse_material.SparseMaterialRegistry;
import azura.somlib.sparse_material.SparseMaterialState;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import nishio.lazuli_lib.internals.LazuliLog;

import java.util.*;

public class SparseMaterialChunkStorage {
    public enum DeltaType {
        ADD, REMOVE, CLEAR
    }

    public static final Codec<DeltaType> DELTA_TYPE_CODEC =
            Codec.INT.xmap(
                    ordinal -> DeltaType.values()[ordinal],
                    mode -> mode.ordinal()
            );

    public record SingleSparseMaterialStateDelta(DeltaType type, SparseMaterialState target){}
    public record DeltaEntry(BlockPos pos, List<SingleSparseMaterialStateDelta> deltas){}


    Map<BlockPos, Map<SparseMaterial, SparseMaterialState>> STORAGE;
    Map<BlockPos, Map<SparseMaterial, SparseMaterialState>> STABLE_STORAGE;
    Map<BlockPos, List<SingleSparseMaterialStateDelta>> dirty = new HashMap<>();

    public record StorageEntry(BlockPos pos, List<SparseMaterialState> materials){}

    public static final Codec<SparseMaterialState> SPARSE_MATERIAL_CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Identifier.CODEC
                                    .fieldOf("type")
                                    .forGetter(material -> SparseMaterialRegistry.MATERIAL_TYPES.getId(material.getType())),
                            Codec.FLOAT
                                    .fieldOf("concentration")
                                    .forGetter(SparseMaterialState::getConcentration)
                    ).apply(instance, SparseMaterialState::new)
            );

    public static final Codec<StorageEntry> ENTRY_CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            BlockPos.CODEC
                                    .fieldOf("pos")
                                    .forGetter(StorageEntry::pos),

                            SPARSE_MATERIAL_CODEC
                                    .listOf()
                                    .fieldOf("materials")
                                    .forGetter(StorageEntry::materials)
                    ).apply(instance, StorageEntry::new)
            );

    public static final Codec<SparseMaterialChunkStorage> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            ENTRY_CODEC
                                    .listOf()
                                    .fieldOf("entries")
                                    .forGetter(SparseMaterialChunkStorage::entries)
                    ).apply(instance, SparseMaterialChunkStorage::new)
            );

    public static final Codec<SingleSparseMaterialStateDelta> SPARSE_STATE_DELTA_CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            DELTA_TYPE_CODEC
                                    .fieldOf("is_add").forGetter(SingleSparseMaterialStateDelta::type),
                            SPARSE_MATERIAL_CODEC
                                    .fieldOf("target")
                                    .forGetter(SingleSparseMaterialStateDelta::target)
                    ).apply(instance, SingleSparseMaterialStateDelta::new)
            );

    public static final Codec<DeltaEntry> DELTA_ENTRY_CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            BlockPos.CODEC
                                    .fieldOf("pos")
                                    .forGetter(DeltaEntry::pos),

                            SPARSE_STATE_DELTA_CODEC
                                    .listOf()
                                    .fieldOf("deltas")
                                    .forGetter(DeltaEntry::deltas)
                    ).apply(instance, DeltaEntry::new)
            );

    public static SparseMaterialChunkStorage fromBuff(PacketByteBuf buf){
        NbtCompound nbt = buf.readNbt();

         return CODEC
                .parse(NbtOps.INSTANCE, nbt)
                .get().orThrow();
    }

    public void writeBuff(PacketByteBuf buf){
        NbtElement nbt = CODEC.encodeStart(
                NbtOps.INSTANCE,
                this
        ).get().orThrow();

        buf.writeNbt((NbtCompound) nbt);
    }

    public synchronized void updateStable() {
        Map<BlockPos, Map<SparseMaterial, SparseMaterialState>> snapshot = new HashMap<>();

        for (var entry : STORAGE.entrySet()) {
            snapshot.put(
                    entry.getKey(),
                    new HashMap<>(entry.getValue())
            );
        }

        STABLE_STORAGE = snapshot;
    }

    public void writeUpdatePacketAndClear(PacketByteBuf buf){
        buf.writeInt(dirty.size());

        for (var entry : dirty.entrySet()){
            NbtElement nbt = DELTA_ENTRY_CODEC.encodeStart(
                    NbtOps.INSTANCE,
                    new DeltaEntry(entry.getKey(), entry.getValue())
            ).get().orThrow();
            buf.writeNbt((NbtCompound) nbt);
        }

        this.dirty.clear();
    }

    public synchronized void applyUpdatePacket(PacketByteBuf buf){
        int count = buf.readInt();
        for (int i = 0; i < count; i++){
            DeltaEntry deltaEntry = DELTA_ENTRY_CODEC.parse(NbtOps.INSTANCE, buf.readNbt()).get().orThrow();
            for (SingleSparseMaterialStateDelta delta : deltaEntry.deltas()){
                switch (delta.type()){
                    case ADD -> this.add(deltaEntry.pos, delta.target);
                    case REMOVE -> this.remove(deltaEntry.pos, delta.target.getType());
                    case CLEAR -> this.STORAGE.clear();
                }
            }
        }
        this.dirty.clear();
    }

    public SparseMaterialChunkStorage() {
        this.STORAGE = new HashMap<>();
    }

    public SparseMaterialChunkStorage(Map<BlockPos, Map<SparseMaterial, SparseMaterialState>> map) {
        this.STORAGE = map;
    }

    public Map<BlockPos, Map<SparseMaterial, SparseMaterialState>> getStableStorage(){
        if (STABLE_STORAGE == null) updateStable();
        return STABLE_STORAGE;
    }

    public SparseMaterialChunkStorage(List<StorageEntry> entries) {
       this.STORAGE = new HashMap<>();
        LazuliLog.Shaders.info("Loading from entries!!!");


        for(StorageEntry e : entries){
            Map<SparseMaterial, SparseMaterialState> map = new HashMap<>();
            e.materials().forEach(s -> map.put(s.getType(), s));
            if (!map.isEmpty()) {
                STORAGE.put(e.pos(), map);
            }
        }

    }

    public Map<SparseMaterial, SparseMaterialState> get(BlockPos pos) {
        return STORAGE.getOrDefault(pos, new HashMap<>());
    }

    public Map<BlockPos, List<SingleSparseMaterialStateDelta>> getDirty(){
        return this.dirty;
    }

    public boolean isEmpty(){
        return this.STORAGE.isEmpty();
    }


    public synchronized void add(BlockPos pos, SparseMaterialState value) {
        var posMap = STORAGE.computeIfAbsent(pos, p -> new HashMap<>());
        if (posMap.containsKey(value.getType())){
            if (!posMap.get(value.getType()).addTogether(value)){
                posMap.remove(value.getType());
            };
        } else {
            if (value.getConcentration() > 0.2) posMap.put(value.getType(), value);
        }
        if (posMap.isEmpty()){
            STORAGE.remove(pos);
        }
        dirty.computeIfAbsent(pos, p -> new ArrayList<>()).add(new SingleSparseMaterialStateDelta(DeltaType.ADD, value));
    }

    public synchronized void clear(BlockPos pos) {
        dirty.computeIfAbsent(pos, p -> new ArrayList<>()).add(new SingleSparseMaterialStateDelta(DeltaType.CLEAR, new SparseMaterialState()));

    }

    public synchronized void remove(BlockPos pos, SparseMaterial value) {
        Map<SparseMaterial, SparseMaterialState> map = STORAGE.get(pos);
        map.remove(value);
        if (map.isEmpty()){
            STORAGE.remove(pos);
        }
        dirty.computeIfAbsent(pos, p -> new ArrayList<>()).add(new SingleSparseMaterialStateDelta(DeltaType.REMOVE, new SparseMaterialState(value, 0)));
    }



    public Map<BlockPos, Map<SparseMaterial, SparseMaterialState>> values() {
        return STORAGE;
    }

    public List<StorageEntry> entries() {
        LazuliLog.Shaders.info("Exporting to entries!!!");
        List<StorageEntry> output = new ArrayList<>();

        for (var entry : STORAGE.entrySet()) {
            output.add(new StorageEntry(
                    entry.getKey(),
                    entry.getValue().values().stream().toList()
            ));
        }

        return output;
    }
}