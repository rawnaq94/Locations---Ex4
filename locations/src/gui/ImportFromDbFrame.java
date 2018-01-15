package gui;

import filters.*;
import models.ScanInfo;
import models.WifiNetwork;
import utils.Four;
import utils.INeedToReloadData;
import utils.Pair;
import utils.ScansService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/*
 * the main window of the app
 */
public class ImportFromDbFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private final MainFrame mainFrame;
	private final JTextField ip;
	private final JTextField port;
	private final JTextField db;
	private final JTextField table;
	private final JTextField user;
	private final JTextField password;
	private final JButton importButton;

	public ImportFromDbFrame(MainFrame mainFrame) {
		this.mainFrame=mainFrame;
		ip = new JTextField(12);
		setDefaultTextFeature(ip, "IP");
		importButton = new JButton("Import");
		port = new JTextField(12);
		setDefaultTextFeature(port, "PORT");
		db = new JTextField(12);
		setDefaultTextFeature(db, "DB NAME");
		table = new JTextField(12);
		setDefaultTextFeature(table, "TABLE NAME");
		user = new JTextField(12);
		setDefaultTextFeature(user, "USER");
		password = new JTextField(12);
		setDefaultTextFeature(password, "PASSWORD");
		addListeners();
		initialize();
		centerFrame();
	}

	private void setDefaultTextFeature(JTextField field, String defaultText) {
		field.setText(defaultText);
		field.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (field.getText().equals(defaultText)) {
					field.setText("");
				}
			}

			public void focusLost(FocusEvent e) {
				if (field.getText().isEmpty()) {
					field.setText(defaultText);
				}
			}
		});
	}

	private void centerFrame() {

		Dimension windowSize = getSize();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();

		int dx = centerPoint.x - windowSize.width / 2;
		int dy = centerPoint.y - windowSize.height / 2;
		setLocation(dx, dy);
	}


	public void addListeners() {
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String _ip = ip.getText();
				String _port = port.getText();
				String _db = db.getText();
				String _table = table.getText();
				 String _url = "jdbc:mysql://"+_ip+":"+_port+"/"+_db;
				 String _user = user.getText();
				 String _password = password.getText();
				 Connection _con = null;

				Statement st = null;
				ResultSet rs = null;

				try {
					_con = DriverManager.getConnection(_url, _user, _password);
					st = _con.createStatement();
					rs = st.executeQuery("SELECT UPDATE_TIME FROM information_schema.tables WHERE TABLE_SCHEMA = 'oop_course_ariel' AND TABLE_NAME = 'ex4_db'");

					PreparedStatement pst = _con.prepareStatement("SELECT * FROM "+_table);
					rs = pst.executeQuery();
					Map<ScanInfo, List<WifiNetwork>> scans = new HashMap<ScanInfo, List<WifiNetwork>>();
					while (rs.next()) {
						// read all lines
						WifiNetwork network = new WifiNetwork(rs.getString(1), rs.getString(0), rs.getString(4), Double.parseDouble(rs.getString(5)));

						SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						Date date = parser.parse(rs.getString(3));
						ScanInfo scanInfo = new ScanInfo(date, _db+_table, Double.parseDouble(rs.getString(6)), Double.parseDouble(rs.getString(7)),
								Double.parseDouble(rs.getString(8)));

						if (scans.containsKey(scanInfo)) {
							scans.get(scanInfo).add(network);
						} else {
							List<WifiNetwork> networks = new LinkedList<WifiNetwork>();
							networks.add(network);
							scans.put(scanInfo, networks);
						}
					}

				} catch (SQLException ex) {
					ex.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				} finally {
					try {
						if (rs != null) {rs.close();}
						if (st != null) { st.close(); }
						if (_con != null) { _con.close();  }
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}

	public void initialize() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(new Dimension(800, 900));
		this.setTitle("WiFi scans analyzer");
		this.setResizable(false);

		Font font = new Font("Courier", Font.LAYOUT_LEFT_TO_RIGHT, 15);
		ip.setFont(font);
		port.setFont(font);
		user.setFont(font);
		password.setFont(font);
		db.setFont(font);
		table.setFont(font);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		mainPanel.setBackground(Color.WHITE);

		mainPanel.add(ip);
		mainPanel.add(port);
		mainPanel.add(db);
		mainPanel.add(table);
		mainPanel.add(user);
		mainPanel.add(password);
		mainPanel.add(importButton);


		Container contentPane = getContentPane();
		contentPane.setBackground(Color.WHITE);
		contentPane.add(mainPanel, BorderLayout.CENTER);

		this.pack();
	}


}
