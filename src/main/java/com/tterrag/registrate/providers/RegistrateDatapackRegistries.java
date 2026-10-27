package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.generators.RegistrateRecipeProvider;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

public final class RegistrateDatapackRegistries {

    private RegistrateDatapackRegistries() {}

    public static void gather(AbstractRegistrate<?> owner, GatherDataRegistryEntriesEvent event) {
        owner.getDataGenInitializer().gatherRegistryEntries(event);

        event.recipe((recipes, advancements) -> new RegistrateRecipeProvider(owner, recipes, advancements));
        event.add(Registries.LOOT_TABLE, new RegistrateLootTableProvider(owner));
        event.add(Registries.ADVANCEMENT, advancements -> {
            RegistrateAdvancementProvider provider = new RegistrateAdvancementProvider(owner, advancements);
            owner.genData(ProviderType.ADVANCEMENT, provider);
        });
    }
}
