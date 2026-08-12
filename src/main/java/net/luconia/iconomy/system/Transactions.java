package net.luconia.iconomy.system;

import net.luconia.iconomy.iConomyRevamped;
import net.luconia.iconomy.settings.Settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Transactions {
	
	public void insert(String from, String to, double from_balance, double to_balance, double set, double gain, double loss) {
		if (!Settings.transactionLoggingEnabled()) {
			return;
		}
        int i = 1;
        long timestamp = System.currentTimeMillis() / 1000L;

        Object[] data = { from, to, Double.valueOf(from_balance), Double.valueOf(to_balance), Long.valueOf(timestamp), Double.valueOf(set), Double.valueOf(gain), Double.valueOf(loss) };

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = iConomyRevamped.getBackEnd().getConnection();
            ps = conn.prepareStatement("INSERT INTO " + Settings.getDBTable() + "_Transactions(account_from, account_to, account_from_balance, account_to_balance, `timestamp`, `set`, gain, loss) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            for (Object obj : data) {
                ps.setObject(i, obj);
                i++;
            }

            ps.executeUpdate();
        } catch (SQLException ex) {} finally {
        	iConomyRevamped.getBackEnd().close(conn, ps);
        }
    }
}
