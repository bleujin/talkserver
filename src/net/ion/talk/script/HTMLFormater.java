package net.ion.talk.script;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.ion.framework.db.bean.ResultSetHandler;

public class HTMLFormater implements ResultSetHandler<StringBuilder> {

	public StringBuilder handle(ResultSet rs) throws SQLException {
		StringWriter swriter = new StringWriter();
		swriter.append("<html><head><title>HTMLFormater</title></head><body>");
		toHTML(rs, swriter);
		swriter.append("</body></html>");
		return new StringBuilder(swriter.getBuffer()) ;
	}

	private void toHTML(ResultSet rs, Writer writer) throws SQLException {
		try {
			writer.append("\n<table border=1 cellpadding=3 cellspacing=0 width='100%' class='body_table'>\n");
			writer.append("\t<tr>");
			ResultSetMetaData meta = rs.getMetaData();
			int j = 1;
			for (int last = meta.getColumnCount(); j <= last; j++) {
				writer.append("<th>");
				writer.append(meta.getColumnName(j));
				writer.append("</th>");
			}

			writer.append("</tr>\n");
			for (; rs.next(); writer.append("</tr>\n")) {
				writer.append("\t<tr>");
				j = 1;
				for (int last = meta.getColumnCount(); j <= last; j++) {
					writer.append("<td class='text_center'>");
					writer.append(rs.getString(j));
					writer.append("</td>");
				}

			}

			writer.append("</table>\n");
		} catch (IOException ex) {
			throw new SQLException(ex.getMessage());
		}
	}
}
