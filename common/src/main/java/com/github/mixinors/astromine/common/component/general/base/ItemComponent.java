/*
 * MIT License
 *
 * Copyright (c) 2020, 2021 Mixinors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.mixinors.astromine.common.component.general.base;

import com.github.mixinors.astromine.common.component.general.miscellaneous.NamedComponent;
import com.github.mixinors.astromine.common.component.block.entity.TransferComponent;
import com.github.mixinors.astromine.common.component.general.compatibility.ItemComponentFromInventory;
import com.github.mixinors.astromine.common.component.general.compatibility.ItemComponentFromSidedInventory;
import com.github.mixinors.astromine.common.component.general.provider.ItemComponentProvider;
import com.github.mixinors.astromine.common.util.StackUtils;
import me.shedaniel.architectury.utils.NbtType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

import com.github.mixinors.astromine.common.component.general.compatibility.InventoryFromItemComponent;
import com.github.mixinors.astromine.registry.common.AMItems;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Integer.min;

/**
 * A {@link NamedComponent} representing an item reserve.
 *
 * Serialization and deserialization methods are provided for:
 * - {@link CompoundTag} - through {@link #toTag(CompoundTag)} and {@link #fromTag(CompoundTag)}.
 */
public interface ItemComponent extends Iterable<ItemStack>, NamedComponent {
	/** Instantiates an {@link ItemComponent}. */
	static ItemComponent of(int size) {
		return new SimpleItemComponent(size);
	}

	/** Instantiates an {@link ItemComponent}. */
	static ItemComponent of(ItemStack... stacks) {
		return new SimpleItemComponent(stacks);
	}
	
	/** Instantiates an {@link ItemComponent} associated with a {@link TransferComponent} provider. */
	static <V> ItemComponent of(V v, int size) {
		return new SimpleDirectionalItemComponent(v, size);
	}
	
	/** Instantiates an {@link ItemComponent} associated with a {@link TransferComponent} provider. */
	public static <V> ItemComponent of(V v, ItemStack... stacks) {
		return new SimpleDirectionalItemComponent(v, stacks);
	}
	
	/** Instantiates an {@link ItemComponent} with automatic synchronization. */
	static ItemComponent ofSynced(int size) {
		return new SimpleAutoSyncedItemComponent(size);
	}
	
	/** Instantiates an {@link ItemComponent} with automatic synchronization. */
	static ItemComponent ofSynced(ItemStack... stacks) {
		return new SimpleAutoSyncedItemComponent(stacks);
	}
	
	/** Instantiates an {@link ItemComponent} associated with a {@link TransferComponent} provider,
	 *  with automatic synchronization. */
	static <V> ItemComponent ofSynced(V v, int size) {
		return new SimpleAutoSyncedDirectionalItemComponent(v, size);
	}
	
	/** Instantiates an {@link ItemComponent} associated with a {@link TransferComponent} provider,
	 *  with automatic synchronization. */
	static <V> ItemComponent ofSynced(V v, ItemStack... stacks) {
		return new SimpleAutoSyncedDirectionalItemComponent(v, stacks);
	}

	/** Returns this component's {@link Item} symbol. */
	default Item getSymbol() {
		return AMItems.ITEM.get();
	}

	/** Returns this component's {@link Text} name. */
	default Text getName() {
		return new TranslatableText("text.astromine.item");
	}

	/** Returns this component's size. */
	int getSize();

	/** Returns this component's listeners. */
	List<Runnable> getListeners();

	/** Adds a listener to this component. */
	default void addListener(Runnable listener) {
		this.getListeners().add(listener);
	}

	/** Removes a listener from this component. */
	default void removeListener(Runnable listener) {
		this.getListeners().remove(listener);
	}

	/** Triggers this component's listeners. */
	default void updateListeners() {
		getListeners().forEach(Runnable::run);
	}

	/** Returns this component with an added listener. */
	default ItemComponent withListener(Consumer<ItemComponent> listener) {
		addListener(() -> listener.accept(this));
		return this;
	}

	/** Returns this component's contents. */
	Map<Integer, ItemStack> getContents();

	/** Returns this component's contents matching the given predicate. */
	default List<ItemStack> getStacks(Predicate<ItemStack> predicate) {
		return getContents().values().stream().filter(predicate).collect(Collectors.toList());
	}

	/** Returns this component's contents extractable through the given direction. */
	default List<ItemStack> getExtractableStacks(@Nullable Direction direction) {
		return getContents().entrySet().stream().filter((entry) -> canExtract(direction, entry.getValue(), entry.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
	}

	/** Returns this component's contents matching the given predicate
	 * extractable through the specified direction. */
	default List<ItemStack> getExtractableStacks(@Nullable Direction direction, Predicate<ItemStack> predicate) {
		return getExtractableStacks(direction).stream().filter(predicate).collect(Collectors.toList());
	}

	/** Returns this component's contents insertable through the given direction. */
	default List<ItemStack> getInsertableStacks(@Nullable Direction direction) {
		return getContents().entrySet().stream().filter((entry) -> canInsert(direction, entry.getValue(), entry.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
	}

	/** Returns this component's contents insertable through the given direction
	 * which accept the specified stack. */
	default List<ItemStack> getInsertableStacks(@Nullable Direction direction, ItemStack Stack) {
		return getContents().entrySet().stream().filter((entry) -> canInsert(direction, Stack, entry.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
	}

	/** Returns this component's contents matching the given predicate
	 * insertable through the specified direction which accept the supplied stack. */
	default List<ItemStack> getInsertableStacks(@Nullable Direction direction, ItemStack Stack, Predicate<ItemStack> predicate) {
		return getInsertableStacks(direction, Stack).stream().filter(predicate).collect(Collectors.toList());
	}

	/** Returns the first stack extractable through the given direction. */
	@Nullable
	default ItemStack getFirstExtractableStack(@Nullable Direction direction) {
		var stacks = getExtractableStacks(direction);
		
		stacks.removeIf(ItemStack::isEmpty);
		if (!stacks.isEmpty())
			return stacks.get(0);
		else return null;
	}

	/** Returns the first stack matching the given predicate
	 * extractable through the specified direction. */
	@Nullable
	default ItemStack getFirstExtractableStack(@Nullable Direction direction, Predicate<ItemStack> predicate) {
		var stacks = getExtractableStacks(direction, predicate);
		
		if (!stacks.isEmpty())
			return stacks.get(0);
		else return null;
	}

	/** Returns the first stack insertable through the given direction
	 * which accepts the specified volume. */
	@Nullable
	default ItemStack getFirstInsertableStack(@Nullable Direction direction, ItemStack Stack) {
		var stacks = getInsertableStacks(direction, Stack);
		
		if (!stacks.isEmpty())
			return stacks.get(0);
		else return null;
	}

	/** Returns the first volume matching the given predicate
	 * insertable through the specified direction which accepts the supplied stack. */
	@Nullable
	default ItemStack getFirstInsertableStack(@Nullable Direction direction, ItemStack Stack, Predicate<ItemStack> predicate) {
		var stacks = getInsertableStacks(direction, Stack, predicate);
		
		if (!stacks.isEmpty())
			return stacks.get(0);
		else return null;
	}

	/** Transfers all transferable content from this component
	 * to the target component. */
	default void into(ItemComponent target, int count, Direction extractionDirection, Direction insertionDirection) {
		for (var sourceSlot = 0; sourceSlot < getSize(); ++sourceSlot) {
			var sourceStack = getStack(sourceSlot);

			if (canExtract(extractionDirection, sourceStack, sourceSlot)) {
				for (var targetSlot = 0; targetSlot < target.getSize(); ++targetSlot) {
					var targetStack = target.getStack(targetSlot);

					if (!sourceStack.isEmpty() && count > 0) {
						var insertionStack = sourceStack.copy();
						insertionStack.setCount(min(min(count, insertionStack.getCount()), targetStack.getMaxCount() - targetStack.getCount()));

						int insertionCount = insertionStack.getCount();

						if (target.canInsert(insertionDirection, insertionStack, targetSlot)) {
							var merge = StackUtils.merge(insertionStack, targetStack);

							sourceStack.decrement(insertionCount - merge.getLeft().getCount());
							setStack(sourceSlot, sourceStack);
							target.setStack(targetSlot, merge.getRight());
						}
					} else {
						break;
					}
				}
			}
		}
	}

	/** Transfers all transferable content from this component
	 * to the target component. */
	default void into(ItemComponent target, int count, Direction extractionDirection) {
		into(target, count, extractionDirection, extractionDirection.getOpposite());
	}

	/** Transfers all transferable content from this component
	 * to the target component. */
	default void into(ItemComponent target, int count) {
		into(target, count, null, null);
	}

	/** Transfers all transferable content from this component
	 * to the target component. */
	default void into(ItemComponent target) {
		into(target, 1, null, null);
	}

	/** Asserts whether the given stack can be inserted through the specified
	 * direction into the supplied slot. */
	default boolean canInsert(@Nullable Direction direction, ItemStack stack, int slot) {
		return getStack(slot).isEmpty() || (ItemStack.areItemsEqual(stack, getStack(slot)) && ItemStack.areTagsEqual(stack, getStack(slot)) && getStack(slot).getMaxCount() - getStack(slot).getCount() >= stack.getCount());
	}

	/** Asserts whether the given stack can be extracted through the specified
	 * direction from the supplied slot. */
	default boolean canExtract(Direction direction, ItemStack stack, int slot) {
		return true;
	}

	/* Returns the {@link ItemStack} at the given slot. */
	default ItemStack getStack(int slot) {
		if (!getContents().containsKey(slot)) throw new ArrayIndexOutOfBoundsException("Slot " + slot + " not found in ItemComponent!");
		return getContents().get(slot);
	}

	/** Sets the {@link ItemStack} at the given slot to the specified value. */
	default void setStack(int slot, ItemStack stack) {
		getContents().put(slot, stack);

		updateListeners();
	}

	/** Removes the {@link ItemStack} at the given slot, returning it. */
	default ItemStack removeStack(int slot) {
		var stack = getContents().remove(slot);

		updateListeners();

		return stack;
	}

	/** Asserts whether this component's contents are all empty or not. */
	default boolean isEmpty() {
		return getContents().values().stream().allMatch(ItemStack::isEmpty);
	}

	/** Asserts whether this component's contents are not all empty or not. */
	default boolean isNotEmpty() {
		return !isEmpty();
	}

	/** Clears this component's contents. */
	default void clear() {
		getContents().forEach((slot, stack) -> setStack(slot, ItemStack.EMPTY));
	}

	/** Returns an {@link Inventory} wrapped over this component. */
	default Inventory asInventory() {
		return InventoryFromItemComponent.of(this);
	}

	/** Serializes this {@link ItemComponent} to a {@link CompoundTag}. */
	@Override
	default void toTag(CompoundTag tag) {
		var listTag = new ListTag();

		for (var i = 0; i < getSize(); ++i) {
			var stack = getStack(i);

			listTag.add(i, stack.toTag(new CompoundTag()));
		}

		var dataTag = new CompoundTag();

		dataTag.putInt("Size", getSize());
		dataTag.put("Stacks", listTag);

		tag.put("ItemComponent", dataTag);
	}

	/** Deserializes this {@link ItemComponent} from  a {@link CompoundTag}. */
	@Override
	default void fromTag(CompoundTag tag) {
		var dataTag = tag.getCompound("ItemComponent");

		var size = dataTag.getInt("Size");

		var stacksTag = dataTag.getList("Stacks", NbtType.COMPOUND);

		for (var i = 0; i < size; ++i) {
			var stackTag = stacksTag.getCompound(i);

			setStack(i, ItemStack.fromTag(stackTag));
		}
	}

	/** Returns the {@link ItemComponent} of the given {@link V}. */
	@Nullable
	static <V> ItemComponent get(V v) {
		if (v instanceof ItemComponentProvider) {
			return ((ItemComponentProvider) v).getItemComponent();
		} else {
			if (v instanceof SidedInventory) {
				return ItemComponentFromSidedInventory.of((SidedInventory) v);
			}
			
			if (v instanceof Inventory) {
				return ItemComponentFromInventory.of((Inventory) v);
			}
		}
		
		return null;
	}

	/** Returns an iterator of this component's contents. */
	@Override
	default  Iterator<ItemStack> iterator() {
		return getContents().values().iterator();
	}

	/** Applies the given action to all of this component's contents. */
	@Override
	default void forEach(Consumer<? super ItemStack> action) {
		getContents().values().forEach(action);
	}

	/** Applies the given action to all of this component's contents. */
	default void forEachIndexed(BiConsumer<Integer, ? super ItemStack> action) {
		getContents().forEach(action);
	}

	/** Returns the first stack in this component. */
	default ItemStack getFirst() {
		return getStack(0);
	}

	/** Sets the first stack in this component to the specified value. */
	default void setFirst(ItemStack stack) {
		setStack(0, stack);
	}

	/** Returns the second stack in this component. */
	default ItemStack getSecond() {
		return getStack(1);
	}

	/** Sets the second stack in this component to the specified value. */
	default void setSecond(ItemStack stack) {
		setStack(1, stack);
	}

	/** Returns the third stack in this component. */
	default ItemStack getThird() {
		return getStack(2);
	}

	/** Sets the third stack in this component to the specified value. */
	default void setThird(ItemStack stack) {
		setStack(2, stack);
	}

	/** Returns the fourth stack in this component. */
	default ItemStack getFourth() {
		return getStack(3);
	}

	/** Sets the fourth stack in this component to the specified value. */
	default void setFourth(ItemStack stack) {
		setStack(3, stack);
	}

	/** Returns the fifth stack in this component. */
	default ItemStack getFifth() {
		return getStack(4);
	}

	/** Sets the fifth stack in this component to the specified value. */
	default void setFifth(ItemStack stack) {
		setStack(4, stack);
	}

	/** Returns the sixth stack in this component. */
	default ItemStack getSixth() {
		return getStack(5);
	}

	/** Sets the sixth stack in this component to the specified value. */
	default void setSixth(ItemStack stack) {
		setStack(5, stack);
	}

	/** Returns the seventh stack in this component. */
	default ItemStack getSeventh() {
		return getStack(6);
	}

	/** Sets the seventh stack in this component to the specified value. */
	default void setSeventh(ItemStack stack) {
		setStack(6, stack);
	}

	/** Returns the eighth stack in this component. */
	default ItemStack getEighth() {
		return getStack(7);
	}

	/** Sets the eighth stack in this component to the specified value. */
	default void setEight(ItemStack stack) {
		setStack(7, stack);
	}

	/** Returns the ninth stack in this component. */
	default ItemStack getNinth() {
		return getStack(8);
	}

	/** Sets the ninth stack in this component to the specified value. */
	default void setNinth(ItemStack stack) {
		setStack(8, stack);
	}
}
