package com.shipmentEvents.handlers;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class TestBean {

	public void doBusiness() {
		Connection con = null;
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/oracle");
			con = ds.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select col1 from test where col1 = 1");
			int recordCount = 0;
			while (rs.next()) {
				System.out.println(rs.getInt("col1"));
				recordCount++;
			}
			System.out.println("recordCount:" + recordCount);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				//con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
