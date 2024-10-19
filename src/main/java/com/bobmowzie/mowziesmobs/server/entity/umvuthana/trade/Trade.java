package com.bobmowzie.mowziesmobs.server.entity.umvuthana.trade;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class Trade {
    private final ItemStack input;

    private final ItemStack output;

    private final int weight;

    public Trade(ItemStack input, ItemStack output, int weight) {
        this.input = input.copy();
        this.output = output.copy();
        this.weight = weight;
    }

    public Trade(Trade trade) {
        this(trade.input, trade.output, trade.weight);
    }

    public ItemStack getInput() {
        return input.copy();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Trade) {
            Trade trade = (Trade) o;
            return weight == trade.weight && ItemStack.matches(input, trade.input) && ItemStack.matches(output, trade.output);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 961 * input.hashCode() + 31 * output.hashCode() + weight;
    }

    public CompoundTag serialize(RegistryAccess access) {
        CompoundTag compound = new CompoundTag();
        compound.put("input", input.save(access, new CompoundTag()));
        compound.put("output", output.save(access, new CompoundTag()));
        compound.putInt("weight", weight);
        return compound;
    }

    public static Trade deserialize(RegistryAccess access, CompoundTag compound) {
        ItemStack input = ItemStack.parseOptional(access, compound.getCompound("input"));
        ItemStack output = ItemStack.parseOptional(access, compound.getCompound("output"));
        int weight = compound.getInt("weight");
        if (input.isEmpty() || output.isEmpty() || weight < 1) {
            return null;
        }
        return new Trade(input, output, weight);
    }
}
