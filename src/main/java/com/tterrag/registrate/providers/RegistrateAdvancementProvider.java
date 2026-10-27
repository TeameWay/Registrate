package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RegistrateAdvancementProvider implements BootstrapContext<Advancement>, Consumer<AdvancementHolder> {

    private final AbstractRegistrate<?> owner;
    private final BootstrapContext<Advancement> advancements;

    public RegistrateAdvancementProvider(AbstractRegistrate<?> owner, BootstrapContext<Advancement> advancements) {
        this.owner = owner;
        this.advancements = advancements;
    }

    @Override
    public Holder.Reference<Advancement> register(ResourceKey<Advancement> key, Advancement value) {
        return advancements.register(key, value);
    }

    @Override
    public Holder.Reference<Advancement> register(ResourceKey<Advancement> key, Advancement value, ICondition... conditions) {
        return advancements.register(key, value, conditions);
    }

    @Override
    public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
        return advancements.lookup(key);
    }

    @Deprecated
    @Override
    public <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> key) {
        return advancements.listContextElements(key);
    }

    public <T> Holder<T> resolve(ResourceKey<T> key) {
        return lookup(key.registryKey()).getOrThrow(key);
    }

    public MutableComponent title(String category, String name, String title) {
        return owner.addLang("advancements", Identifier.fromNamespaceAndPath(category, name), "title", title);
    }

    public MutableComponent desc(String category, String name, String description) {
        return owner.addLang("advancements", Identifier.fromNamespaceAndPath(category, name), "description", description);
    }

    @Override
    public void accept(AdvancementHolder holder) {
        holder.register(advancements);
    }

    public void withConditions(AdvancementHolder holder, List<ICondition> conditions) {
        holder.register(advancements, conditions.toArray(ICondition[]::new));
    }
}
