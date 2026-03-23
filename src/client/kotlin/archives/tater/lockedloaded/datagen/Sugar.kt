package archives.tater.lockedloaded.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack.RegistryDependentFactory
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.advancements.criterion.DataComponentMatchers
import net.minecraft.advancements.criterion.ItemPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.data.tags.TagAppender
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.level.storage.loot.functions.FilteredFunction
import net.minecraft.world.level.storage.loot.functions.LootItemFunction
import java.util.*

context(appender: TagAppender<E, *>)
operator fun <E: Any> E.unaryPlus() {
    appender.add(this)
}

context(appender: TagAppender<*, T>)
operator fun <T: Any> TagKey<T>.unaryPlus() {
    appender.forceAddTag(this)
}

fun <T: Any> dynamicRegistry(name: String, registry: ResourceKey<Registry<T>>): RegistryDependentFactory<FabricDynamicRegistryProvider> = { output, registriesFuture ->
    object : FabricDynamicRegistryProvider(output, registriesFuture) {
        override fun configure(registries: HolderLookup.Provider, entries: Entries) {
            entries.addAll(registries.lookupOrThrow(registry))
        }

        override fun getName(): String = name
    }
}

operator fun <T: Any> BootstrapContext<T>.set(key: ResourceKey<T>, value: T) {
    register(key, value)
}

fun ItemPredicate(init: ItemPredicate.Builder.() -> Unit): ItemPredicate = ItemPredicate.Builder.item().apply(init).build()
fun itemPredicateBuilder(init: ItemPredicate.Builder.() -> Unit): ItemPredicate.Builder = ItemPredicate.Builder.item().apply(init)

fun ItemPredicate.Builder.withComponents(init: DataComponentMatchers.Builder.() -> Unit) {
    withComponents(DataComponentMatchers.Builder.components().apply(init).build())
}

fun DataComponentMatchers.Builder.hasAny(type: DataComponentType<*>) {
    any<DataComponentType<*>>(type)
}

fun FilteredFunction(filter: ItemPredicate, onPass: LootItemFunction? = null, onFail: LootItemFunction? = null): LootItemFunction =
    FilteredFunction.filtered(filter).apply {
        onPass(Optional.ofNullable(onPass))
        onFail(Optional.ofNullable(onFail))
    }.build()