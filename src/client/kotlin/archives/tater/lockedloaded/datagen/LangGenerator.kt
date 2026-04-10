package archives.tater.lockedloaded.datagen

import archives.tater.lockedloaded.registry.LockedLoadedEnchantments
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.enchantment.Enchantment
import java.util.concurrent.CompletableFuture

class LangGenerator(packOutput: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : FabricLanguageProvider(packOutput, registryLookup) {
    fun TranslationBuilder.addEnchantment(enchantment: ResourceKey<Enchantment>, name: String, description: String) {
        addEnchantment(enchantment, name)
        add(enchantment.identifier().toLanguageKey("enchantment", "description"), "An enchantment that $description")
        add(enchantment.identifier().toLanguageKey("enchantment", "desc"), description.replaceFirstChar { it.uppercase() })
    }

    override fun generateTranslations(registryLookup: HolderLookup.Provider, translationBuilder: TranslationBuilder) {
        translationBuilder.addEnchantment(LockedLoadedEnchantments.MULTICHAMBERED, "Multichambered", "allows loading multiple shots by crouching, then firing them in sequence")
        translationBuilder.addEnchantment(LockedLoadedEnchantments.PUMP_CHARGE, "Pump Charge", "allows loading multiple shots by crouching, then firing them all at once in a spread")
        translationBuilder.addEnchantment(LockedLoadedEnchantments.MAGAZINE, "Magazine", "loads many shots with a long charge time, then lets you fire them in sequence with a short cooldown")
        translationBuilder.addEnchantment(LockedLoadedEnchantments.SHARPSHOOTING, "Sharpshooting", "makes arrows pierce mobs and ricochet off blocks")
        translationBuilder.addEnchantment(LockedLoadedEnchantments.ROCKETRY, "Rocketry", "gives empty firework rockets explosions and allows rocket jumping and rocket riding")
        translationBuilder.addEnchantment(LockedLoadedEnchantments.RECOVERY, "Recovery", "prevents arrows from getting destroyed upon impact and being picked up by other players")
        translationBuilder.addEnchantment(LockedLoadedEnchantments.TWIRLING_CURSE, "Curse of Twirling", "makes you spin loaded crossbows to fire them")
    }


}