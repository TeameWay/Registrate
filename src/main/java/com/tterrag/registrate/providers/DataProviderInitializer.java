package com.tterrag.registrate.providers;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.data.event.DatapackRegistryGatherer;

import org.jspecify.annotations.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataProviderInitializer {

	@FunctionalInterface
	private interface RegistryBootstrap {
		void apply(DatapackRegistryGatherer gatherer);
	}

	private final List<RegistryBootstrap> registryBootstraps = new ArrayList<>();

	private final Map<ProviderType<?>, ProviderType<? extends RegistrateLookupFillerProvider>> providerDependencies = new ConcurrentHashMap<>();

	public DataProviderInitializer() {
		addDependency(ProviderType.ITEM_TAGS, ProviderType.BLOCK_TAGS);
	}

	public void gatherRegistryEntries(DatapackRegistryGatherer gatherer) {
		registryBootstraps.forEach(bootstrap -> bootstrap.apply(gatherer));
	}

	protected List<Sorted> getSortedProviders() {
		List<Sorted> ans = new ArrayList<>();
		Set<ProviderType<?>> added = new HashSet<>();
		List<Map.Entry<String, ProviderType<?>>> remain = new ArrayList<>(RegistrateDataProvider.TYPES.entrySet());
		while (!remain.isEmpty()) {
			if (!remain.removeIf(e -> {
				ProviderType<?> type = e.getValue();
				var parent = providerDependencies.get(type);
				if (parent == null || added.contains(parent)) {
					ans.add(new Sorted(e.getKey(), type, parent));
					added.add(type);
					return true;
				}
				return false;
			})) throw new IllegalStateException("Looping dependency detected: " + remain);
		}
		return ans;
	}

	public <T> void add(ResourceKey<? extends Registry<T>> registry, SingleRegistryBootstrap<T> bootstrap) {
		registryBootstraps.add(gatherer -> gatherer.add(registry, bootstrap));
	}

	public void add(MultiRegistryBootstrap bootstrap) {
		registryBootstraps.add(gatherer -> gatherer.add(bootstrap));
	}

	public void addDependency(ProviderType<?> dependent, ProviderType<? extends RegistrateLookupFillerProvider> parent) {
		var old = providerDependencies.put(dependent, parent);
		if (old != null) throw new IllegalStateException("Providers can have only 1 prerequisite");
	}

	public record Sorted(
			String id, ProviderType<?> type,
			@Nullable ProviderType<? extends RegistrateLookupFillerProvider> parent
	) {
	}

}
