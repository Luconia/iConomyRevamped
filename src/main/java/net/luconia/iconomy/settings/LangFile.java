package net.luconia.iconomy.settings;

public enum LangFile {

	LANG_ROOT("lang", "", ""),

	LANG_MONEY_PREFIX("lang.money_prefix", "<green>[<white>Money<green>] "),

	LANG_PERSONAL_ROOT("lang.personal","",""),
	LANG_PERSONAL_BALANCE("lang.personal.balance","<green>Balance: <white>%s"),
	LANG_PERSONAL_RANK("lang.personal.rank","<green>Current rank: <white>%s"),
	LANG_PERSONAL_SET("lang.personal.set","<green>Your balance has been changed to <white>%s."),
	LANG_PERSONAL_CREDIT("lang.personal.credit","<white>%s<green> was credited into your account."),
	LANG_PERSONAL_DEBIT("lang.personal.debit","<rose>Your account had <white>%s<rose> debited."),

	LANG_PLAYER_ROOT("lang.player","",""),
	LANG_PLAYER_BALANCE("lang.player.balance","<green>%s's Balance: <white>%s"),
	LANG_PLAYER_RANK("lang.player.rank","<green>%s's rank: <white>%s"),
	LANG_PLAYER_RESET("lang.player.reset","<white>%s's <rose>account has been reset."),
	LANG_PLAYER_SET("lang.player.reset","<white>%s's <rose>account has been reset."),
	LANG_PLAYER_DEBIT("lang.player.debit","<white>%s's <rose>account had <white>%s<rose> debited."),
	LANG_PLAYER_CREDIT("lang.player.credit","<white>%s's <green>account had <white>%s<green> credited."),
	
	LANG_PAYMENT_ROOT("lang.payment","",""),
	LANG_PAYMENT_SELF("lang.payment.self","Sorry, you cannot send money to yourself."),
	LANG_PAYMENT_TO("lang.payment.to","<green>You have sent <white>%s<green> to <white>%s<green>."),
	LANG_PAYMENT_FROM("lang.payment.from","<white>%s<green> has sent you <white>%s<green>."),
	
	LANG_ACCOUNTS_ROOT("lang.accounts","",""),
	LANG_ACCOUNTS_EMPTIED("lang.accounts.emptied", "<rose>Deleted <white>all<rose> accounts."),
	LANG_ACCOUNTS_CREATE("lang.accounts.create", "<green>Created account with the name <white>%s<green>."),
	LANG_ACCOUNTS_STATUS("lang.accounts.hidden_status", "<green>Account status is now <white>%s<green>."),
	LANG_ACCOUNTS_PURGED("lang.accounts.purged", "<rose>All inactive accounts were purged."),
	LANG_ACCOUNTS_REMOVED("lang.accounts.removed", "<green>Deleted account: <white>%s<green>."),

	LANG_STATS_HEADER("lang.stats.header", "<green>-----[ <white>iConomy Stats <green>]-----"),
	LANG_STATS_TOTAL("lang.stats.total", "<gray>Total %s: <white>%s"),
	LANG_STATS_AVERAGE("lang.stats.average", "<gray>Average %s: <white>%s"),
	LANG_STATS_ACCOUNTS("lang.stats.accounts", "<gray>Total Accounts: <white>%s"),
	
	LANG_TOP_ROOT("lang.top","",""),
	LANG_TOP_HEADER("lang.top.header","<green>Top <white>%s<green> Richest Players:"),
	LANG_TOP_EMPTY("lang.top.empty","<white>   Nobody yet!"),
	LANG_TOP_LINE("lang.top.line","<white>   %s.<green> %s <white>(<green>%s<white>)"),
	
	
	LANG_ERROR_ROOT("lang.error","",""),
	LANG_ERROR_ACCOUNT_ALREADY_EXISTS("lang.error.already_exists", "Account already exists."),
	LANG_ERROR_NO_ACCOUNT_FOUND("lang.error.no_account_found", "<rose>No account found belonging to <white>%s<rose>."),
	LANG_ERROR_CANNOT_AFFORD("lang.error.no_funds", "Sorry, you do not have enough funds to do that.");

	private final String Root;
	private final String Default;
	private String[] comments;

	LangFile(String root, String def, String... comments) {

		this.Root = root;
		this.Default = def;
		this.comments = comments;
	}

	/**
	 * Retrieves the root for a config option
	 *
	 * @return The root for a config option
	 */
	public String getRoot() {

		return Root;
	}

	/**
	 * Retrieves the default value for a config path
	 *
	 * @return The default value for a config path
	 */
	public String getDefault() {

		return Default;
	}

	/**
	 * Retrieves the comment for a config path
	 *
	 * @return The comments for a config path
	 */
	public String[] getComments() {

		if (comments != null) {
			return comments;
		}

		String[] comments = new String[1];
		comments[0] = "";
		return comments;
	}

}
