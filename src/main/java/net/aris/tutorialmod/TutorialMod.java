package net.aris.tutorialmod;

import net.aris.tutorialmod.block.ModBlocks;
import net.aris.tutorialmod.component.ModDataComponentTypes;
import net.aris.tutorialmod.effect.ModEffects;
import net.aris.tutorialmod.enchantment.ModEnchantmentEffects;
import net.aris.tutorialmod.entity.ModEntities;
import net.aris.tutorialmod.entity.custom.MantisEntity;
import net.aris.tutorialmod.item.ModItems;
import net.aris.tutorialmod.item.ModItemsGroups;
import net.aris.tutorialmod.potion.ModPotions;
import net.aris.tutorialmod.sound.ModSounds;
import net.aris.tutorialmod.util.HammerUsageEvent;
import net.aris.tutorialmod.villager.ModVillagers;
import net.aris.tutorialmod.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutorialMod implements ModInitializer {
	public static final String MOD_ID = "tutorialmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemsGroups.registerItemsGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModDataComponentTypes.registerDataComponentTypes();
		ModSounds.registerSounds();
		ModEffects.registerEffects();
		ModPotions.registerPotions();
		ModEnchantmentEffects.registerEnchantmentEffects();
		ModWorldGeneration.generateModWorldGen();
		ModVillagers.registerVillagers();

		FuelRegistry.INSTANCE.add(ModItems.STARLIGHT_ASHES,600);

		PlayerBlockBreakEvents.BEFORE.register(new HammerUsageEvent());
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if(entity instanceof SheepEntity sheepEntity && !world.isClient()){
				if (player.getMainHandStack().getItem()== Items.END_ROD){
					player.sendMessage(Text.literal("The Player just hit a sheep with an END ROD! YOU SICK FREAK!*"));
					player.getMainHandStack().decrement(1);
					sheepEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON,600,6));
				}
				return ActionResult.PASS;
			}
			return ActionResult.PASS;
		});

		FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
			builder.registerPotionRecipe(Potions.AWKWARD, Items.SLIME_BALL, ModPotions.SLIMEY_POTION);
		});

		CompostingChanceRegistry.INSTANCE.add(ModItems.CAULIFLOWER,0.5f);
		CompostingChanceRegistry.INSTANCE.add(ModItems.CAULIFLOWER_SEEDS,0.25f);
		CompostingChanceRegistry.INSTANCE.add(ModItems.HONEY_BERRIES,0.15f);

		StrippableBlockRegistry.register(ModBlocks.DRIFTWOOD_LOG, ModBlocks.STRIPPED_DRIFTWOOD_LOG);
		StrippableBlockRegistry.register(ModBlocks.DRIFTWOOD_WOOD, ModBlocks.STRIPPED_DRIFTWOOD_WOOD);

		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_DRIFTWOOD_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_DRIFTWOOD_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_PLANKS, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_LEAVES, 30, 60);

		FabricDefaultAttributeRegistry.register(ModEntities.MANTIS, MantisEntity.createAttributes());

		TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1,factories -> {
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 3),
					new ItemStack(ModItems.CAULIFLOWER, 8), 7, 2, 0.04f));

			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 12),
					new ItemStack(ModItems.HONEY_BERRIES, 5), 4, 7, 0.04f));

			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.DIAMOND, 9),
					new ItemStack(ModItems.CAULIFLOWER_SEEDS, 2), 3, 4, 0.04f));

		});

		TradeOfferHelper.registerVillagerOffers(ModVillagers.KAUPENGER, 1,factories -> {

			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 16),
					new ItemStack(ModItems.RAW_PINK_GARNET, 1), 4, 7, 0.04f));

		});

		TradeOfferHelper.registerVillagerOffers(ModVillagers.KAUPENGER, 2,factories -> {
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 18),
					new ItemStack(ModItems.CHISEL, 1), 4, 7, 0.04f));

			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(ModItems.PINK_GARNET, 26),
					new ItemStack(ModItems.TOMAHAWK, 1), 3, 12, 0.09f));

		});

		TradeOfferHelper.registerWanderingTraderOffers( 1,factories -> {
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(ModItems.PINK_GARNET, 10),
					new ItemStack(ModItems.TOMAHAWK, 1), 3, 12, 0.09f));

			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 5),
					new ItemStack(ModItems.RAW_PINK_GARNET, 1), 4, 7, 0.04f));

		});
	}
}