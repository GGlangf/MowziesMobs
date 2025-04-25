package com.bobmowzie.mowziesmobs.server.item;

import com.bobmowzie.mowziesmobs.client.render.item.RenderGeomancerArmor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class ItemGeomancerArmor extends ArmorItem implements GeoItem {
    public String controllerName = "controller";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ItemGeomancerArmor(Type slot, Properties builderIn) {
        super(MaterialHandler.GEOMANCER_ARMOR_MATERIAL, slot, builderIn);
    }

    private PlayState predicate(AnimationState<ItemGeomancerArmor> state) {
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, controllerName, 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(Component.translatable(getDescriptionId() + ".text.0").setStyle(ItemHandler.TOOLTIP_STYLE));
    }

    private static class GeomancerArmorMaterial implements ArmorMaterial {
        private static final EnumMap<Type, Integer> DEFENSE_MAP = Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
            map.put(ArmorItem.Type.BOOTS, 2);
            map.put(ArmorItem.Type.LEGGINGS, 6);
            map.put(ArmorItem.Type.CHESTPLATE, 7);
            map.put(ArmorItem.Type.HELMET, 2);
        });

        @Override
        public int getDurabilityForType(Type equipmentSlotType) {
            return ArmorMaterials.DIAMOND.getDurabilityForType(equipmentSlotType);
        }

        @Override
        public int getDefenseForType(Type equipmentSlotType) {
            return (int) (DEFENSE_MAP.get(equipmentSlotType) * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.GEOMANCER_ARMOR.armorConfig.damageReductionMultiplierValue);
        }

        @Override
        public int getEnchantmentValue() {
            return ArmorMaterials.IRON.getEnchantmentValue();
        }

        @Override
        public SoundEvent getEquipSound() {
            return ArmorMaterials.IRON.getEquipSound();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ItemHandler.BLUFF_ROD.get());
        }

        @Override
        public String getName() {
            return "geomancer_armor";
        }

        @Override
        public float getToughness() {
            return 1 * ConfigHandler.COMMON.TOOLS_AND_ABILITIES.WROUGHT_HELM.armorConfig.toughnessMultiplierValue;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    }

    public static class ClientExtensions implements IClientItemExtensions {
        @Override
        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot
        equipmentSlot, HumanoidModel<?> original) {
            if (this.armorRenderer == null)
                this.armorRenderer = new RenderGeomancerArmor();
            armorRenderer.prepForRender(entityLiving, itemStack, equipmentSlot, original);
            return armorRenderer;
        }
        private GeoArmorRenderer<?> armorRenderer;
    }
}
