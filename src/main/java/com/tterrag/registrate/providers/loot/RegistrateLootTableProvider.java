package com.tterrag.registrate.providers.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RegistrateLootTableProvider implements SingleRegistryBootstrap<LootTable> {

    public interface LootType<T extends RegistrateLootTables> {

        LootType<RegistrateBlockLootTables> BLOCK = register("block", LootContextParamSets.BLOCK, RegistrateBlockLootTables::new);
        LootType<RegistrateEntityLootTables> ENTITY = register("entity", LootContextParamSets.ENTITY, RegistrateEntityLootTables::new);

        T create(LootTableSubProvider.Context output, AbstractRegistrate<?> parent, Consumer<T> callback);

        ContextKeySet contextKeySet();

        static <T extends RegistrateLootTables> LootType<T> register(String name, ContextKeySet contextKeySet,
                                                                    TriFunction<LootTableSubProvider.Context, AbstractRegistrate<?>, Consumer<T>, T> factory) {
            LootType<T> type = new LootType<>() {
                @Override
                public T create(LootTableSubProvider.Context output, AbstractRegistrate<?> parent, Consumer<T> callback) {
                    return factory.apply(output, parent, callback);
                }

                @Override
                public ContextKeySet contextKeySet() {
                    return contextKeySet;
                }
            };
            LOOT_TYPES.put(name, type);
            return type;
        }
    }

    private static final Map<String, LootType<?>> LOOT_TYPES = new LinkedHashMap<>();

    private final AbstractRegistrate<?> parent;
    private final Multimap<LootType<?>, Consumer<? super RegistrateLootTables>> lootCallbacks = HashMultimap.create();
    private final Multimap<ContextKeySet, Consumer<BiConsumer<ResourceKey<LootTable>, LootTable.Builder>>> tableCallbacks = HashMultimap.create();

    public RegistrateLootTableProvider(AbstractRegistrate<?> parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    public <T extends RegistrateLootTables> void addLootAction(LootType<T> type, NonNullConsumer<T> action) {
        lootCallbacks.put(type, (Consumer<RegistrateLootTables>) action);
    }

    public void addLootAction(ContextKeySet contextKeySet,
                              Consumer<BiConsumer<ResourceKey<LootTable>, LootTable.Builder>> action) {
        tableCallbacks.put(contextKeySet, action);
    }

    @Override
    public void run(BootstrapContext<LootTable> registry) {
        lootCallbacks.clear();
        tableCallbacks.clear();

        parent.genData(ProviderType.LOOT, this);

        List<LootTableProvider.SubProviderEntry> entries = new ArrayList<>();
        LOOT_TYPES.values().forEach(type -> entries.add(
                new LootTableProvider.SubProviderEntry(context -> createSubProvider(type, context), type.contextKeySet())));
        tableCallbacks.keySet().forEach(contextKeySet -> entries.add(
                new LootTableProvider.SubProviderEntry(
                        context -> () -> tableCallbacks.get(contextKeySet).forEach(action -> action.accept(context::accept)),
                        contextKeySet)));

        new LootTableProvider(Set.of(), entries).run(registry);
    }

    @SuppressWarnings("unchecked")
    private LootTableSubProvider createSubProvider(LootType<?> type, LootTableSubProvider.Context context) {
        LootType<RegistrateLootTables> concreteType = (LootType<RegistrateLootTables>) type;
        return concreteType.create(context, parent,
                tables -> lootCallbacks.get(concreteType).forEach(callback -> callback.accept(tables)));
    }
}
