package GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import json.Business;
import json.Review;
import DB.ManageReviews;
import Engine.Engine;
import Engine.WordTopic;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author apple
 */
public class Businesses extends javax.swing.JFrame {

	public WordTopic word;
	public ArrayList<Review> reviewsArr;

	// private ArrayList data = null;
	//
	private Object[][] tableData;
	//
	// public ArrayList getData() {
	// return data;
	// }
	//
	// public void setData(ArrayList data) {
	// this.data = data;
	// }

	private DefaultTableModel tableModel;

	Object[][] reviews = { { null, null, null, null, null, null, null, null,
			null, null } };

	/**
	 * Creates new form Businesses
	 */

	public Businesses(WordTopic word, ArrayList<Review> reviewsArr) {
		this.word = word;
		this.reviewsArr = reviewsArr;
		SwingUtilities.invokeLater(doWorkRunnable);
		initComponents();
		this.setSize(GUIFunctions.getWindowWidth(),
				GUIFunctions.getWindowHeight());
		GUIFunctions.addResizeEvent(this);
	}

	Object[] obj = null;

	public Businesses(Object[] obj) {
		this.obj = obj;
		initComponents();
		SwingUtilities.invokeLater(doWorkRunnable);
		this.setSize(GUIFunctions.getWindowWidth(),
				GUIFunctions.getWindowHeight());
		GUIFunctions.addResizeEvent(this);
	}

	Runnable doWorkRunnable = new Runnable() {
		@Override
		public void run() {
			initReviews();
//			tableModel = new javax.swing.table.DefaultTableModel(tableData,
//					new String[] { "#", "Name", "ID", "Lat", "Lng",
//							"Full address", "City", "Value", "Rating", "" });
			MyTableModel myTableModel = new MyTableModel();
			myTableModel.data = tableData;
			jTable1.setModel(myTableModel);
			jTable1.setDefaultRenderer(StarRater.class, new StarRenderer(true));
			jTable1.setDefaultRenderer(Business.class, new BusinessInfoRenderer(true));
			tableModel.fireTableDataChanged();
			updateRowHeights();
			resizeColumns();
		}
	};


	private void initReviews() {
		if (this.obj == null) {
			obj = GUIFunctions.doRankingOfBusinesses(word, reviewsArr);
		}
		List<Business> businessesList = (List<Business>) obj[0];
		Map<String, Business> businessesMap = new HashMap<String, Business>();
		Map<String, Double> businessesValues = (Map<String, Double>) obj[1];
		calcStattistics(businessesValues);
		for (Business business : businessesList) {
			businessesMap.put(business.getBusinessId(), business);
		}
		double minValue = Integer.MAX_VALUE;
		double maxValue = Integer.MIN_VALUE;
		for (Double value : businessesValues.values()) {
			if (value < minValue) {
				minValue = value;
			}
			if (value > maxValue) {
				maxValue = value;
			}
		}

		if (businessesList != null && businessesValues != null) {
			tableData = new Object[businessesList.size()][];
			int i = businessesValues.size() - 1;
			for (String businessId : businessesValues.keySet()) {
				Business business = businessesMap.get(businessId);
				double rating = (5 * (maxValue - minValue) + 4
						* businessesValues.get(business.getBusinessId()) - 4 * maxValue)
						/ (maxValue - minValue);
				rating = Math.round(rating * 100.0) / 100.0;
				double value = Math.round(businessesValues.get(business
						.getBusinessId()) * 100.0) / 100.0;
				tableData[i] = new Object[] { i + 1, 
						business,
						businessesValues.get(business.getBusinessId()), 
						new StarRater(5, Float.valueOf(String.valueOf(business.getStars())), 0),
						new StarRater(5, (float) rating, 0),
						"showMap" };

				i--;
			}
		} else {
			tableData = new Object[][] { { null, null, null, null } };
		}
		JFrame tempFrame = this;
		jTable1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// if (e.getClickCount() == 2) {
				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();
				if (column == 5) {
					GUIFunctions.showSpecificBusiness(tempFrame, businessesList
							.get(row).getLatitude(), businessesList.get(row)
							.getLongitude());
				}
			}
			// }
		});
	}


	public void calcStattistics(Map<String, Double> businessesValues) {
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		double variance = 0;
		double sum = 0;
		for (String key : businessesValues.keySet()) {
			Double value = businessesValues.get(key);
			sum += value;
			if (value < min) {
				min = value;
			}
			if (value > max) {
				max = value;
			}
		}
		double mean = sum / businessesValues.size();
		for (String key : businessesValues.keySet()) {
			Double value = businessesValues.get(key);
			variance += Math.pow(value - mean, 2);
		}
		variance = variance / businessesValues.size();
		
		System.out.println("max = " + max);
		System.out.println("min = " + min);
		System.out.println("mean = " + mean);
		System.out.println("variance = " + variance);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		setTitle("Businesses");

		jLabelWord = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();
		jMenuBar1 = new javax.swing.JMenuBar();
		jMenu1 = new javax.swing.JMenu();
		jMenuItemRestart = new javax.swing.JMenuItem();
		jMenuItemBackToTopics = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jLabelWord.setFont(new java.awt.Font("Monaco", 0, 14)); // NOI18N
		if (word != null) {
			jLabelWord.setText(word.word);
		} else {
			jLabelWord.setText("Word");
		}

		jTable1.setFont(new java.awt.Font("Monaco", 0, 14)); // NOI18N
		tableModel = new javax.swing.table.DefaultTableModel(reviews,
				new String[] { "#", "Business", "Value", "Yelp rating", "Rating", "" });
		jTable1.setRowHeight(30);
		jTable1.setModel(tableModel);
		jTable1.setDefaultRenderer(StarRater.class, new StarRenderer(true));
		// jTable1.getColumnModel().getColumn(1).setCellRenderer(new
		// ReviewRowRenderer());

		jScrollPane1.setViewportView(jTable1);

		jMenu1.setText("File");

		jMenuItemRestart.setFont(new java.awt.Font("Monaco", 0, 14)); // NOI18N
		jMenuItemRestart.setText("Restart");
		jMenuItemRestart.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemRestartActionPerformed(evt);
			}
		});
		jMenuItemBackToTopics.setFont(new java.awt.Font("Monaco", 0, 14)); // NOI18N
		jMenuItemBackToTopics.setText("Back to topics");
		jMenuItemBackToTopics.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemBackToTopicsActionPerformed(evt);
			}
		});
		
		jMenu1.add(jMenuItemRestart);
		jMenu1.add(jMenuItemBackToTopics);

		jMenuBar1.add(jMenu1);

		setJMenuBar(jMenuBar1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 586,
								Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 297,
								Short.MAX_VALUE).addContainerGap()));

		pack();
		setLocationRelativeTo(null);
	}// </editor-fold>

	private void updateRowHeights() {
		for (int row = 0; row < jTable1.getRowCount(); row++) {
			int rowHeight = jTable1.getRowHeight();

			Component comp = jTable1.prepareRenderer(
					jTable1.getCellRenderer(row, 1), row, 1);
			rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);

			jTable1.setRowHeight(row, rowHeight);
		}
	}

	private void resizeColumns() {
		float[] columnWidthPercentage = { 10.0f, 20.0f, 10.0f, 10.0f, 10.0f,
				10.0f, 10.0f, 10.0f, 5.0f, 5.0f };
		int tW = jTable1.getWidth();
		TableColumn column;
		TableColumnModel jTableColumnModel = jTable1.getColumnModel();
		int cantCols = jTableColumnModel.getColumnCount();
		for (int i = 0; i < cantCols; i++) {
			column = jTableColumnModel.getColumn(i);
			int pWidth = Math.round(columnWidthPercentage[i] * tW);
			column.setPreferredWidth(pWidth);
		}
	}

	private void jMenuItemRestartActionPerformed(java.awt.event.ActionEvent evt) {
		GUIFunctions.restart(this);
	}
	private void jMenuItemBackToTopicsActionPerformed(java.awt.event.ActionEvent evt) {
		GUIFunctions.backToTopics(this);
	}
	

	// Variables declaration - do not modify
	private javax.swing.JLabel jLabelWord;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTable1;
	private javax.swing.JMenu jMenu1;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JMenuItem jMenuItemRestart;
	private javax.swing.JMenuItem jMenuItemBackToTopics;

	// End of variables declaration
	
	
	
    class MyTableModel extends AbstractTableModel {
    	
    	boolean DEBUG;
    	
        private String[] columnNames = {"#", "Business", "Value", "Yelp rating", "Rating", ""};
        
        
        private Object[][] data;

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
//            if (col < 1) {
//                return false;
//            } else {
//                return true;
//            }
        	return false;
        }

        public void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of "
                                   + value.getClass() + ")");
            }

            data[row][col] = value;
            fireTableCellUpdated(row, col);

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    }

	
	
	
	
	
	
	
	
}
