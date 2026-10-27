package com.tterrag.registrate.providers.loot;

import com.tterrag.registrate.AbstractRegistrate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Generated;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RegistrateEntityLootTables extends EntityLootSubProvider implements RegistrateLootTables {

    private final AbstractRegistrate<?> parent;
    private final Consumer<RegistrateEntityLootTables> callback;

    public RegistrateEntityLootTables(LootTableSubProvider.Context output, AbstractRegistrate<?> parent,
                                      Consumer<RegistrateEntityLootTables> callback) {
        super(FeatureFlags.REGISTRY.allFlags(), output);
        this.parent = parent;
        this.callback = callback;
    }

    @Override
    public void generate() {
        callback.accept(this);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return parent.getAll(Registries.ENTITY_TYPE).stream().map(Supplier::get);
    }

    public <T> HolderGetter<T> lookup(ResourceKey<? extends Registry<? extends T>> key) {
        return output.lookup(key);
    }

    public HolderGetter<Item> itemLookup() {
        return items;
    }

    public HolderGetter<Block> blockLookup() {
        return output.lookup(Registries.BLOCK);
    }

    public HolderGetter<EntityType<?>> entityLookup() {
        return output.lookup(Registries.ENTITY_TYPE);
    }

    // @formatter:off
    // GENERATED START - DO NOT EDIT BELOW THIS LINE

    /** Generated override to expose protected method: {@link EntityLootSubProvider#killedByFrog} */
    @Override
    @Generated(value = "com.tterrag.registrate.test.meta.UpdateEntityLootTables", date = "Sat, 20 Jun 2026 04:20:42 GMT")
    public LootItemCondition.Builder killedByFrog() { return super.killedByFrog(); }

    /** Generated override to expose protected method: {@link EntityLootSubProvider#add} */
    @Override
    @Generated(value = "com.tterrag.registrate.test.meta.UpdateEntityLootTables", date = "Sat, 20 Jun 2026 04:20:42 GMT")
    public void add(EntityType<?> type, LootTable.Builder builder) { super.add(type, builder); }

    /** Generated override to expose protected method: {@link EntityLootSubProvider#add} */
    @Override
    @Generated(value = "com.tterrag.registrate.test.meta.UpdateEntityLootTables", date = "Sat, 20 Jun 2026 04:20:42 GMT")
    public void add(EntityType<?> type, ResourceKey<LootTable> lootTable, LootTable.Builder builder) { super.add(type, lootTable, builder); }

    // GENERATED END
}
