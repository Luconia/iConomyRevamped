package net.luconia.iconomy.checks;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luconia.iconomy.iConomyRevamped;
import net.luconia.iconomy.settings.LangStrings;
import net.luconia.iconomy.settings.Settings;

/**
 * Stackable bearer check items. Value is stored in PDC; identical amounts stack.
 * <p>
 * Same model as TNE currency notes: plain configured material + metadata, no food/consumable
 * components. Redeem is driven by {@link org.bukkit.event.player.PlayerInteractEvent}.
 */
public final class CheckItem {

	private static final AtomicReference<Material> CACHED_MATERIAL = new AtomicReference<>();
	private static final AtomicReference<NamespacedKey> MARKER_KEY = new AtomicReference<>();
	private static final AtomicReference<NamespacedKey> AMOUNT_KEY = new AtomicReference<>();
	private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

	private CheckItem() {}

	private static NamespacedKey markerKey() {
		NamespacedKey cached = MARKER_KEY.get();
		if (cached != null)
			return cached;
		NamespacedKey key = new NamespacedKey(iConomyRevamped.getPlugin(), "check");
		MARKER_KEY.compareAndSet(null, key);
		return MARKER_KEY.get();
	}

	private static NamespacedKey amountKey() {
		NamespacedKey cached = AMOUNT_KEY.get();
		if (cached != null)
			return cached;
		NamespacedKey key = new NamespacedKey(iConomyRevamped.getPlugin(), "check_amount");
		AMOUNT_KEY.compareAndSet(null, key);
		return AMOUNT_KEY.get();
	}

	/** Call after config reload so material changes take effect. */
	public static void invalidateMaterialCache() {
		CACHED_MATERIAL.set(null);
	}

	public static @NotNull Material getMaterial() {
		Material cached = CACHED_MATERIAL.get();
		if (cached != null)
			return cached;

		Material material = Material.matchMaterial(Settings.getCheckMaterialName());
		if (material == null)
			material = Material.PAPER;
		CACHED_MATERIAL.compareAndSet(null, material);
		return CACHED_MATERIAL.get();
	}

	public static boolean matchesMaterial(@Nullable ItemStack item) {
		return item != null && !item.getType().isAir() && item.getType() == getMaterial();
	}

	public static boolean isCheck(@Nullable ItemStack item) {
		if (!matchesMaterial(item))
			return false;
		return item.getPersistentDataContainer().has(markerKey(), PersistentDataType.BYTE);
	}

	public static double getAmount(@NotNull ItemStack item) {
		PersistentDataContainerView pdc = item.getPersistentDataContainer();
		Double amount = pdc.get(amountKey(), PersistentDataType.DOUBLE);
		return amount != null ? amount : 0.0D;
	}

	/**
	 * Creates a stack of identical checks for the given face value.
	 */
	public static @NotNull ItemStack create(double amount, int quantity) {
		ItemStack stack = new ItemStack(getMaterial(), Math.max(1, quantity));
		ItemMeta meta = stack.getItemMeta();
		meta.getPersistentDataContainer().set(markerKey(), PersistentDataType.BYTE, (byte) 1);
		meta.getPersistentDataContainer().set(amountKey(), PersistentDataType.DOUBLE, amount);

		String formatted = Settings.format(amount);
		meta.displayName(MINI_MESSAGE.deserialize(LangStrings.checkItemName(formatted)));
		meta.lore(List.of(MINI_MESSAGE.deserialize(LangStrings.checkItemLore())));
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * Removes one check from the given stack. Returns true if a check was consumed.
	 */
	public static boolean consumeOne(@NotNull ItemStack stack) {
		if (!isCheck(stack) || stack.getAmount() < 1)
			return false;
		if (stack.getAmount() == 1) {
			stack.setAmount(0);
		} else {
			stack.setAmount(stack.getAmount() - 1);
		}
		return true;
	}
}
