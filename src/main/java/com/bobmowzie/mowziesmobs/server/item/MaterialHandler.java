package com.bobmowzie.mowziesmobs.server.item;

import com.bobmowzie.mowziesmobs.MMCommon;
import com.bobmowzie.mowziesmobs.server.config.ConfigHandler;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class MaterialHandler {
    public static final DeferredRegister<ArmorMaterial> MM_ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, MMCommon.MODID);

    // FIXME 1.21 :: does not respect config values since its loaded too early
    // FIXME 1.21 :: there is 'ArmorItem#getToughness' and the values also get stored in 'ArmorItem#defaultModifiers' in the constructor (attribute modification event to overwrite the values?)
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SOL_VISAGE_MATERIAL = MM_ARMOR_MATERIALS.register("sol_visage", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.HELMET, (int) (ArmorMaterials.GOLD.value().getDefense(ArmorItem.Type.HELMET) * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.SOL_VISAGE.armorConfig.damageReductionMultiplierValue));
    }), ArmorMaterials.GOLD.value().enchantmentValue(),
            ArmorMaterials.GOLD.value().equipSound(),
            ArmorMaterials.GOLD.value().repairIngredient(),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(MMCommon.MODID, "sol_visage"))), // FIXME 1.21 :: maybe doesn't need to exist since 'getArmorTexture' returns a custom texture anyway?
            ArmorMaterials.GOLD.value().toughness() * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.SOL_VISAGE.armorConfig.toughnessMultiplierValue,
            ArmorMaterials.GOLD.value().knockbackResistance()
    ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> UMVUTHANA_MASK_MATERIAL = MM_ARMOR_MATERIALS.register("umvuthana_mask", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.HELMET, (int) (ArmorMaterials.LEATHER.value().getDefense(ArmorItem.Type.HELMET) * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.UMVUTHANA_MASK.armorConfig.damageReductionMultiplierValue));
    }), ArmorMaterials.LEATHER.value().enchantmentValue(),
            ArmorMaterials.LEATHER.value().equipSound(),
            ArmorMaterials.LEATHER.value().repairIngredient(),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(MMCommon.MODID, "umvuthana_mask"))), // FIXME 1.21 :: maybe doesn't need to exist since 'getArmorTexture' returns a custom texture anyway?
            ArmorMaterials.LEATHER.value().toughness() * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.UMVUTHANA_MASK.armorConfig.toughnessMultiplierValue,
            ArmorMaterials.LEATHER.value().knockbackResistance()
    ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ARMOR_WROUGHT_HELM = MM_ARMOR_MATERIALS.register("wrought_helm", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.HELMET, (int) (ArmorMaterials.IRON.value().getDefense(ArmorItem.Type.HELMET) * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.WROUGHT_HELM.armorConfig.damageReductionMultiplierValue));
    }), ArmorMaterials.IRON.value().enchantmentValue(),
            ArmorMaterials.IRON.value().equipSound(),
            ArmorMaterials.IRON.value().repairIngredient(),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(MMCommon.MODID, "wrought_helm"))), // FIXME 1.21 :: maybe doesn't need to exist since 'getArmorTexture' returns a custom texture anyway?
            ArmorMaterials.IRON.value().toughness() * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.WROUGHT_HELM.armorConfig.toughnessMultiplierValue,
            0.1f
    ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> GEOMANCER_ARMOR_MATERIAL = MM_ARMOR_MATERIALS.register("geomancer_armor", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.HELMET, (int) (ArmorMaterials.DIAMOND.value().getDefense(ArmorItem.Type.HELMET) * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.GEOMANCER_ARMOR.armorConfig.damageReductionMultiplierValue));
        map.put(ArmorItem.Type.BODY, (int) (ArmorMaterials.DIAMOND.value().getDefense(ArmorItem.Type.BODY) * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.GEOMANCER_ARMOR.armorConfig.damageReductionMultiplierValue));
        map.put(ArmorItem.Type.LEGGINGS, (int) (ArmorMaterials.DIAMOND.value().getDefense(ArmorItem.Type.LEGGINGS) * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.GEOMANCER_ARMOR.armorConfig.damageReductionMultiplierValue));
        map.put(ArmorItem.Type.BOOTS, (int) (ArmorMaterials.DIAMOND.value().getDefense(ArmorItem.Type.BOOTS) * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.GEOMANCER_ARMOR.armorConfig.damageReductionMultiplierValue));
    }), ArmorMaterials.DIAMOND.value().enchantmentValue(),
            ArmorMaterials.DIAMOND.value().equipSound(),
            ArmorMaterials.DIAMOND.value().repairIngredient(),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(MMCommon.MODID, "geomancer_armor"))), // FIXME 1.21 :: maybe doesn't need to exist since 'getArmorTexture' returns a custom texture anyway?
            ArmorMaterials.DIAMOND.value().toughness() * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.GEOMANCER_ARMOR.armorConfig.toughnessMultiplierValue,
            0
    ));
}
