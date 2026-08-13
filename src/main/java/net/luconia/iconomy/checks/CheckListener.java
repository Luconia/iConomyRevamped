package net.luconia.iconomy.checks;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luconia.iconomy.iConomyRevamped;
import net.luconia.iconomy.settings.LangStrings;
import net.luconia.iconomy.settings.Settings;
import net.luconia.iconomy.system.Account;
import net.luconia.iconomy.util.Messaging;
import net.luconia.iconomy.util.Permissions;

/**
 * Redeem path mirrors TNE currency notes:
 * {@link PlayerInteractEvent} on click, identify via {@link PlayerInteractEvent#getItem()}.
 * No food/consumable components; air clicks only fire when the client sends a packet.
 */
public class CheckListener implements Listener {

	private static final Set<UUID> OPEN_DIALOGS = ConcurrentHashMap.newKeySet();

	/**
	 * Warm reward palette — gold, soft yellow, mint, sky.
	 * Avoids harsh rainbow vomit while still reading as a happy payoff.
	 */
	private static final Color[] BURST_COLORS = {
			Color.fromRGB(255, 215, 64),   // gold
			Color.fromRGB(255, 200, 40),   // deep gold
			Color.fromRGB(255, 236, 140),  // soft yellow
			Color.fromRGB(120, 220, 130),  // mint / money green
			Color.fromRGB(110, 200, 230)   // sky
	};

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK
				&& action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK)
			return;

		ItemStack item = event.getItem();
		if (!CheckItem.matchesMaterial(item))
			return;
		if (!CheckItem.isCheck(item))
			return;

		EquipmentSlot hand = event.getHand();
		if (hand == null)
			return;

		event.setCancelled(true);
		tryOpenRedeemDialog(event.getPlayer(), hand, item);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		OPEN_DIALOGS.remove(event.getPlayer().getUniqueId());
	}

	private static void tryOpenRedeemDialog(Player player, EquipmentSlot hand, ItemStack item) {
		if (!Permissions.hasPermission(player, "iConomy.check", true))
			return;

		if (!OPEN_DIALOGS.add(player.getUniqueId()))
			return;

		double amount = CheckItem.getAmount(item);
		if (amount <= 0.0D) {
			OPEN_DIALOGS.remove(player.getUniqueId());
			Messaging.sendMoneyPrefixedMsg(player, LangStrings.checkInvalid());
			return;
		}

		showRedeemDialog(player, hand, amount);
	}

	private static void showRedeemDialog(Player player, EquipmentSlot hand, double amount) {
		UUID uuid = player.getUniqueId();
		player.getScheduler().runDelayed(iConomyRevamped.getPlugin(), task -> OPEN_DIALOGS.remove(uuid), null, 20L * 30);

		String formatted = Settings.format(amount);
		MiniMessage mm = MiniMessage.miniMessage();
		Component title = mm.deserialize(LangStrings.checkDialogTitle());
		Component body = mm.deserialize(LangStrings.checkDialogBody(formatted));

		ActionButton redeem = ActionButton.create(
				mm.deserialize(LangStrings.checkDialogConfirm()),
				Component.empty(),
				100,
				DialogAction.customClick(
						(view, audience) -> {
							OPEN_DIALOGS.remove(uuid);
							redeem(player, hand, amount);
						},
						ClickCallback.Options.builder().uses(1).build()));

		ActionButton cancel = ActionButton.create(
				mm.deserialize(LangStrings.checkDialogCancel()),
				Component.empty(),
				100,
				DialogAction.customClick(
						(view, audience) -> OPEN_DIALOGS.remove(uuid),
						ClickCallback.Options.builder().uses(1).build()));

		Dialog dialog = Dialog.create(factory -> factory.empty()
				.base(DialogBase.builder(title)
						.body(List.of(DialogBody.plainMessage(body)))
						.canCloseWithEscape(true)
						.afterAction(DialogBase.DialogAfterAction.CLOSE)
						.build())
				.type(DialogType.confirmation(redeem, cancel)));

		player.showDialog(dialog);
	}

	private static void redeem(Player player, EquipmentSlot hand, double expectedAmount) {
		if (!Permissions.hasPermission(player, "iConomy.check", true))
			return;

		PlayerInventory inv = player.getInventory();
		ItemStack stack = hand == EquipmentSlot.OFF_HAND ? inv.getItemInOffHand() : inv.getItemInMainHand();
		if (!CheckItem.isCheck(stack) || Math.abs(CheckItem.getAmount(stack) - expectedAmount) > 0.0001D) {
			Messaging.sendMoneyPrefixedMsg(player, LangStrings.checkInvalid());
			return;
		}

		if (!CheckItem.consumeOne(stack)) {
			Messaging.sendMoneyPrefixedMsg(player, LangStrings.checkInvalid());
			return;
		}

		if (hand == EquipmentSlot.OFF_HAND) {
			if (stack.getAmount() <= 0)
				inv.setItemInOffHand(null);
			else
				inv.setItemInOffHand(stack);
		} else {
			if (stack.getAmount() <= 0)
				inv.setItemInMainHand(null);
			else
				inv.setItemInMainHand(stack);
		}

		Account account = Account.getAccount(player.getUniqueId());
		if (account == null) {
			Messaging.sendMoneyPrefixedMsg(player, LangStrings.checkInvalid());
			return;
		}

		account.getHoldings().add(expectedAmount);
		double balance = account.getHoldings().balance();
		iConomyRevamped.getTransactions().insert("[Check]", account.getName(), 0.0D, balance, 0.0D, expectedAmount, 0.0D);
		Messaging.sendMoneyPrefixedMsg(player, LangStrings.checkRedeemed(Settings.format(expectedAmount)));
		playRedeemParticles(player, expectedAmount);
	}

	/**
	 * Amount anchors for redeem FX. Denser early on; flattens toward 1M.
	 * Scale {@code 1.0} ≈ current baseline at 200.
	 */
	private static final double[] EFFECT_TIER_AMOUNTS = {
			10,
			50,
			100,
			200,
			500,
			800,
			1_000,
			2_000,
			5_000,
			10_000,
			25_000,
			50_000,
			100_000,
			250_000,
			500_000,
			1_000_000
	};

	private static final double[] EFFECT_TIER_SCALES = {
			0.40, // 10
			0.55, // 50
			0.75, // 100
			1.00, // 200 — baseline
			1.18, // 500
			1.28, // 800
			1.38, // 1k
			1.52, // 2k
			1.68, // 5k
			1.82, // 10k
			1.95, // 25k
			2.05, // 50k
			2.15, // 100k
			2.25, // 250k
			2.35, // 500k
			2.50  // 1M+
	};

	/**
	 * Tiered intensity with log-lerp between anchors — more perceptible steps
	 * at small amounts, smaller deltas as you approach a million.
	 */
	private static double redeemEffectScale(double amount) {
		amount = Math.max(0.0D, amount);
		double first = EFFECT_TIER_AMOUNTS[0];
		if (amount < first) {
			if (amount <= 0.0D)
				return EFFECT_TIER_SCALES[0] * 0.5D;
			return EFFECT_TIER_SCALES[0] * (amount / first);
		}

		int last = EFFECT_TIER_AMOUNTS.length - 1;
		if (amount >= EFFECT_TIER_AMOUNTS[last])
			return EFFECT_TIER_SCALES[last];

		for (int i = 0; i < last; i++) {
			double a0 = EFFECT_TIER_AMOUNTS[i];
			double a1 = EFFECT_TIER_AMOUNTS[i + 1];
			if (amount < a1) {
				double s0 = EFFECT_TIER_SCALES[i];
				double s1 = EFFECT_TIER_SCALES[i + 1];
				double t = (Math.log(amount) - Math.log(a0)) / (Math.log(a1) - Math.log(a0));
				return s0 + (s1 - s0) * t;
			}
		}
		return EFFECT_TIER_SCALES[last];
	}

	private static int scaledCount(int base, double scale) {
		return Math.max(1, (int) Math.round(base * scale));
	}

	private static void playRedeemParticles(Player player, double amount) {
		if (!Settings.isCheckRedeemParticles())
			return;

		final double scale = redeemEffectScale(amount);
		final double minScale = EFFECT_TIER_SCALES[0];
		final double maxScale = EFFECT_TIER_SCALES[EFFECT_TIER_SCALES.length - 1];
		final float dustSize = (float) (1.0D + 0.35D * (scale - minScale) / (maxScale - minScale));
		final double spread = 0.28D + 0.12D * (scale - 1.0D);
		final float volume = (float) Math.min(1.0D, 0.45D + 0.35D * scale);

		// Dialog clicks are not always on the entity/region thread.
		player.getScheduler().run(iConomyRevamped.getPlugin(), task -> {
			if (!player.isOnline())
				return;

			Location loc = player.getLocation().add(0, 1.05, 0);
			double ox = Math.max(0.18D, 0.32D * (0.85D + 0.15D * scale));
			double oy = Math.max(0.22D, 0.4D * (0.85D + 0.15D * scale));

			for (Color color : BURST_COLORS) {
				player.spawnParticle(Particle.DUST, loc, scaledCount(4, scale), ox, oy, ox, 0.01,
						new Particle.DustOptions(color, 1.05f * dustSize), true);
			}

			player.spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, scaledCount(10, scale),
					Math.max(0.2D, spread), Math.max(0.25D, spread + 0.05D), Math.max(0.2D, spread), 0.02,
					new Particle.DustTransition(
							Color.fromRGB(255, 215, 64),
							Color.fromRGB(120, 220, 130),
							1.1f * dustSize), true);
			player.spawnParticle(Particle.DUST, loc, scaledCount(18, scale), 0.18 * scale, 0.22 * scale, 0.18 * scale, 0.08,
					new Particle.DustOptions(Color.fromRGB(255, 215, 64), 0.95f * dustSize), true);
			player.spawnParticle(Particle.FIREWORK, loc, scaledCount(14, scale),
					0.1 * Math.min(scale, 1.6D), 0.14 * Math.min(scale, 1.6D), 0.1 * Math.min(scale, 1.6D),
					0.18 + 0.06 * Math.min(scale, 1.8D), null, true);

			player.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, volume * 0.7f, (float) (1.35D - 0.08D * scale));
			player.playSound(loc, Sound.ITEM_ARMOR_EQUIP_GOLD, volume, (float) (1.4D - 0.05D * scale));
			player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, volume * 0.55f, (float) (1.65D - 0.1D * scale));
		}, null);
	}
}
