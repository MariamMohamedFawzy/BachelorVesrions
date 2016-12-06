package GUI;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import json.Business;

public class BusinessInfoRenderer extends JLabel implements TableCellRenderer {
	Border unselectedBorder = null;
	Border selectedBorder = null;
	boolean isBordered = true;

	public BusinessInfoRenderer(boolean isBordered) {
		this.isBordered = isBordered;
		setOpaque(true); // MUST do this for background to show up.
	}

	public Component getTableCellRendererComponent(JTable table,
			Object business, boolean isSelected, boolean hasFocus, int row,
			int column) {
		Business newBusiness = (Business) business;

		JLabel jlabelName = new JLabel(newBusiness.getName());
		StarRater starRater = new StarRater(5, Float.valueOf(String
				.valueOf(newBusiness.getStars())), 0);
		JLabel jLabelFullAddress = new JLabel(newBusiness.getFullAddress());
		JLabel jLabelCity = new JLabel("<html><b>" + newBusiness.getCity() + "</b></html>");
		
		String info = "<html>";
		info += "<br>";
		info += "<b>" + newBusiness.getName() + "</b>";
		info += "<br>";
		info += "<br>";
		info += newBusiness.getFullAddress();
		info += "<br>";
		info += "<br>";
		info += "<b>" + newBusiness.getCity() + "</b>";
		info += "<br>";
		info += "<br>";
		info  += "</html>";
		
		setText(info);
		
		setHorizontalAlignment(LEFT);
		

		if (isBordered) {
			if (isSelected) {
				if (selectedBorder == null) {
					selectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
							5, table.getSelectionBackground());
				}
				setBorder(selectedBorder);
			} else {
				if (unselectedBorder == null) {
					unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
							5, table.getBackground());
				}
				setBorder(unselectedBorder);
			}
		}

		return this;
	}

}
