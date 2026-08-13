package net.luconia.iconomy.settings;

import java.nio.file.Path;

import io.github.townyadvanced.commentedconfiguration.CommentedConfiguration;
import net.luconia.iconomy.util.FileMgmt;

public class LangStrings {
	private static CommentedConfiguration config, newConfig;

	public static void loadLangFile(Path configPath) throws Exception {
		if (FileMgmt.checkOrCreateFile(configPath.toString())) {

			// read the lang.yml into memory
			config = new CommentedConfiguration(configPath);
			if (!config.load())
				throw new Exception("Failed to load lang.yml!");

			setDefaults(configPath);
			config.save();
		}
	}

	/**
	 * Builds a new config reading old config data.
	 */
	private static void setDefaults(Path configPath) {
		newConfig = new CommentedConfiguration(configPath);
		newConfig.load();

		for (LangFile root : LangFile.values()) {
			if (root.getComments().length > 0)
				newConfig.addComment(root.getRoot(), root.getComments());
			else
				setNewProperty(root.getRoot(), (config.get(root.getRoot().toLowerCase()) != null)
					? config.get(root.getRoot().toLowerCase())
					: root.getDefault());
		}

		config = newConfig;
		newConfig = null;
	}

	private static void setNewProperty(String root, Object value) {
		if (value == null) {
			value = "";
		}
		newConfig.set(root.toLowerCase(), value.toString());
	}

	private static String getString(LangFile node) {
		return config.getString(node.getRoot().toLowerCase(), node.getDefault());
	}

	public static String moneyPrefix() {
		return getString(LangFile.LANG_MONEY_PREFIX);
	}

	public static String personalBalance(String balance) {
		return String.format(getString(LangFile.LANG_PERSONAL_BALANCE), balance);
	}

	public static String personalRank(String rank) {
		return String.format(getString(LangFile.LANG_PERSONAL_RANK), rank);
	}

	public static String personalCredit(String amount) {
		return String.format(getString(LangFile.LANG_PERSONAL_CREDIT), amount);
	}

	public static String personalDebit(String amount) {
		return String.format(getString(LangFile.LANG_PERSONAL_DEBIT), amount);
	}

	public static String personalSet(String amount) {
		return String.format(getString(LangFile.LANG_PERSONAL_SET), amount);
	}

	public static String playerBalance(String name, String balance) {
		return String.format(getString(LangFile.LANG_PLAYER_BALANCE), name, balance);
	}

	public static String playerRank(String name, String rank) {
		return String.format(getString(LangFile.LANG_PLAYER_RANK), name, rank);
	}

	public static String playerDebit(String name, String amount) {
		return String.format(getString(LangFile.LANG_PLAYER_DEBIT), name, amount);
	}

	public static String playerCredit(String name, String amount) {
		return String.format(getString(LangFile.LANG_PLAYER_CREDIT), name, amount);
	}

	public static String playerSet(String name, String amount) {
		return String.format(getString(LangFile.LANG_PLAYER_SET), name, amount);
	}

	public static String playerReset(String name) {
		return String.format(getString(LangFile.LANG_PLAYER_RESET), name);
	}

	public static String accountAlreadyExist() {
		return getString(LangFile.LANG_ERROR_ACCOUNT_ALREADY_EXISTS);
	}

	public static String noAccountFound(String name) {
		return String.format(getString(LangFile.LANG_ERROR_NO_ACCOUNT_FOUND), name);
	}

	public static String cannotSendSelf() {
		return getString(LangFile.LANG_PAYMENT_SELF);
	}

	public static String paymentTo(String amount, String name) {
		return String.format(getString(LangFile.LANG_PAYMENT_TO), amount, name);
	}

	public static String paymentFrom(String name, String amount) {
		return String.format(getString(LangFile.LANG_PAYMENT_FROM), name, amount);
	}

	public static String notEnoughFunds() {
		return getString(LangFile.LANG_ERROR_CANNOT_AFFORD);
	}

	public static String accountCreated(String name) {
		return String.format(getString(LangFile.LANG_ACCOUNTS_CREATE), name);
	}

	public static String accountRemoved(String name) {
		return String.format(getString(LangFile.LANG_ACCOUNTS_REMOVED), name);
	}

	public static String accountsEmptied() {
		return getString(LangFile.LANG_ACCOUNTS_EMPTIED);
	}

	public static String accountsPurged() {
		return getString(LangFile.LANG_ACCOUNTS_PURGED);
	}

	public static String accountHiddenStatus(String status) {
		return String.format(getString(LangFile.LANG_ACCOUNTS_STATUS), status);
	}

	public static String statsHeader() {
		return getString(LangFile.LANG_STATS_HEADER);
	}

	public static String statsTotal(String curr, String total) {
		return String.format(getString(LangFile.LANG_STATS_TOTAL), curr, total);
	}

	public static String statsAverage(String curr, String avg) {
		return String.format(getString(LangFile.LANG_STATS_AVERAGE), curr, avg);
	}

	public static String statsAccounts(String num) {
		return String.format(getString(LangFile.LANG_STATS_ACCOUNTS), num);
	}

	public static String topHeader(String num) {
		return String.format(getString(LangFile.LANG_TOP_HEADER), num);
	}

	public static String topEmpty() {
		return getString(LangFile.LANG_TOP_EMPTY);
	}

	public static String topLine(String num, String name, String amount) {
		return String.format(getString(LangFile.LANG_TOP_LINE), num, name, amount);
	}

	public static String checkItemName(String amount) {
		return String.format(getString(LangFile.LANG_CHECK_ITEM_NAME), amount);
	}

	public static String checkItemLore() {
		return getString(LangFile.LANG_CHECK_ITEM_LORE);
	}

	public static String checkWritten(String quantity, String amount) {
		return String.format(getString(LangFile.LANG_CHECK_WRITTEN), quantity, amount);
	}

	public static String checkRedeemed(String amount) {
		return String.format(getString(LangFile.LANG_CHECK_REDEEMED), amount);
	}

	public static String checkDialogTitle() {
		return getString(LangFile.LANG_CHECK_DIALOG_TITLE);
	}

	public static String checkDialogBody(String amount) {
		return String.format(getString(LangFile.LANG_CHECK_DIALOG_BODY), amount);
	}

	public static String checkDialogConfirm() {
		return getString(LangFile.LANG_CHECK_DIALOG_CONFIRM);
	}

	public static String checkDialogCancel() {
		return getString(LangFile.LANG_CHECK_DIALOG_CANCEL);
	}

	public static String checkInvalid() {
		return getString(LangFile.LANG_CHECK_INVALID);
	}

	public static String checkAmountBounds(String min, String max) {
		return String.format(getString(LangFile.LANG_CHECK_AMOUNT_BOUNDS), min, max);
	}

	public static String checkQuantityBounds(String max) {
		return String.format(getString(LangFile.LANG_CHECK_QUANTITY_BOUNDS), max);
	}

	public static String checkInventoryFull() {
		return getString(LangFile.LANG_CHECK_INVENTORY_FULL);
	}
}
