package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import models.ScanInfo;
import models.WifiNetwork;
import utils.Four;
import utils.Pair;
import utils.ScansService;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JPanel mainPanel;
	private final ScansService scanService;
	// Algo panel
	private final JTextField mac;
	private final JButton submitAlgo1;
	private final JLabel algo1results;
	private final JTextField mac1;
	private final JTextField sig1;
	private final JTextField mac2;
	private final JTextField sig2;
	private final JTextField mac3;
	private final JTextField sig3;
	private final JButton submitAlgo2;
	private final JLabel algo2results;

	// Filter panel
	private final JLabel timeLabel;
	private final JTextField timeMin;
	private final JTextField timeMax;
	private final JRadioButton timeRadio;
	private final JLabel latLabel;
	private final JTextField latMin;
	private final JTextField latMax;
	private final JRadioButton latRadio;
	private final JLabel lonLabel;
	private final JTextField lonMin;
	private final JTextField lonMax;
	private final JRadioButton lonRadio;
	private final JLabel altLabel;
	private final JTextField altMin;
	private final JTextField altMax;
	private final JRadioButton altRadio;
	private final JButton submitFilterButton;
	private final ButtonGroup group;

	// Data panel
	private final JButton addDir;
	private final JButton addCsv;
	private final JButton clearData;
	private final JButton saveToCsv;
	private final JLabel dataSizeLabel;
	private final JLabel numberOfApsLabel;

	public MainFrame() {
		scanService = new ScansService();
		mainPanel = new JPanel();
		// Algo panel
		mac = new JTextField(12);
		setDefaultTextFeature(mac, "MAC");
		submitAlgo1 = new JButton("Submit");
		algo1results = new JLabel("Lat,Lon,Alt,Acc", JLabel.TRAILING);
		mac1 = new JTextField(12);
		setDefaultTextFeature(mac1, "MAC1");
		sig1 = new JTextField(12);
		setDefaultTextFeature(sig1, "SIG1");
		mac2 = new JTextField(12);
		setDefaultTextFeature(mac2, "MAC2");
		sig2 = new JTextField(12);
		setDefaultTextFeature(sig2, "SIG2");
		mac3 = new JTextField(12);
		setDefaultTextFeature(mac3, "MAC3");
		sig3 = new JTextField(12);
		setDefaultTextFeature(sig3, "SIG3");
		submitAlgo2 = new JButton("Submit");
		algo2results = new JLabel("Lat,Lon,Alt,Acc", JLabel.TRAILING);

		// Filter panel
		group = new ButtonGroup();
		timeLabel = new JLabel("Time: ", JLabel.TRAILING);
		timeMin = new JTextField(15);
		timeMax = new JTextField(15);
		timeRadio = new JRadioButton();
		latLabel = new JLabel("Lat: ", JLabel.TRAILING);
		latMin = new JTextField(15);
		latMax = new JTextField(15);
		latRadio = new JRadioButton();
		lonLabel = new JLabel("Lon: ", JLabel.TRAILING);
		lonMin = new JTextField(15);
		lonMax = new JTextField(15);
		lonRadio = new JRadioButton();
		altLabel = new JLabel("Alt: ", JLabel.TRAILING);
		altMin = new JTextField(15);
		altMax = new JTextField(15);
		altRadio = new JRadioButton();
		submitFilterButton = new JButton("Submit Filter");
		setDefaultTextFeature(timeMin, "Min [dd/MM/yyyy HH:mm]");
		setDefaultTextFeature(latMin, "Min");
		setDefaultTextFeature(lonMin, "Min");
		setDefaultTextFeature(altMin, "Min");
		setDefaultTextFeature(timeMax, "Max [dd/MM/yyyy HH:mm]");
		setDefaultTextFeature(latMax, "Max");
		setDefaultTextFeature(lonMax, "Max");
		setDefaultTextFeature(altMax, "Max");

		// Data panel
		addDir = new JButton("Add Dir");
		addCsv = new JButton("Add CSV");
		clearData = new JButton("Clear Data");
		saveToCsv = new JButton("Save To CSV");
		dataSizeLabel = new JLabel("", JLabel.TRAILING);
		numberOfApsLabel = new JLabel("", JLabel.TRAILING);
		updateScansInfo();
		//
		addListeners();
		initialize();
		clear();
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

	private void updateScansInfo() {
		dataSizeLabel.setText("# of scans: " + scanService.scans.keySet().size());
		numberOfApsLabel.setText("# of AP: "
				+ scanService.scans.values().stream().flatMap(x -> x.stream()).map(x -> x.mac).distinct().count());
	}

	public void addListeners() {
		addDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Choose directory");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					scanService.addDir(chooser.getSelectedFile().getAbsolutePath());
				}
				updateScansInfo();
			}
		});
		addCsv.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File", "csv");
				chooser.setFileFilter(filter);
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Choose CSV file");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					scanService.addCsv(chooser.getSelectedFile().getAbsolutePath());
				}
				updateScansInfo();
			}
		});
		clearData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
				updateScansInfo();
			}
		});
		saveToCsv.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File", "csv");
				chooser.setFileFilter(filter);
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Choose CSV file");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					scanService.saveToCsv(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		submitFilterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (timeRadio.isSelected()) {
						SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm");

						Date min = timeMin.getText().equals("Min [dd/MM/yyyy HH:mm]") ? new Date(Long.MIN_VALUE)
								: parser.parse(timeMin.getText());

						Date max = timeMax.getText().equals("Max [dd/MM/yyyy HH:mm]") ? new Date(Long.MAX_VALUE)
								: parser.parse(timeMax.getText());
						scanService.scans = scanService.scans.entrySet().stream()
								.filter(p -> (p.getKey().time.after(min) || p.getKey().time.equals(min))
										&& (p.getKey().time.before(max) || p.getKey().time.equals(max)))
								.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
						updateScansInfo();

					}
					if (latRadio.isSelected()) {
						double min = latMin.getText().equals("Min") ? Double.MIN_VALUE
								: Double.parseDouble(latMin.getText());
						double max = latMax.getText().equals("Max") ? Double.MAX_VALUE
								: Double.parseDouble(latMax.getText());
						scanService.scans = scanService.scans.entrySet().stream()
								.filter(p -> p.getKey().latitude >= min && p.getKey().latitude <= max)
								.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
						updateScansInfo();
					}
					if (altRadio.isSelected()) {
						double min = altMin.getText().equals("Min") ? Double.MIN_VALUE
								: Double.parseDouble(altMin.getText());
						double max = altMax.getText().equals("Max") ? Double.MAX_VALUE
								: Double.parseDouble(altMax.getText());
						scanService.scans = scanService.scans.entrySet().stream()
								.filter(p -> p.getKey().altitude >= min && p.getKey().altitude <= max)
								.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
						updateScansInfo();

					}
					if (lonRadio.isSelected()) {
						double min = lonMin.getText().equals("Min") ? Double.MIN_VALUE
								: Double.parseDouble(lonMin.getText());
						double max = lonMax.getText().equals("Max") ? Double.MAX_VALUE
								: Double.parseDouble(lonMax.getText());
						scanService.scans = scanService.scans.entrySet().stream()
								.filter(p -> p.getKey().longitude >= min && p.getKey().longitude <= max)
								.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
						updateScansInfo();

					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(MainFrame.this, "Bad filter", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		submitAlgo1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				String macStr = mac.getText();
				List<Pair<ScanInfo, WifiNetwork>> macScans = scanService.scans.entrySet().stream()
						.filter(e -> e.getValue().stream().anyMatch(w -> w.mac.equals(macStr))) //
						.map(e -> new Pair<ScanInfo, WifiNetwork>(e.getKey(),
								e.getValue().stream().filter(w -> w.mac.equals(macStr)).findFirst().get())) //
						.sorted((x, y) -> (-1) * Double.compare(x.getRight().signal, y.getRight().signal)) //
						.limit(4).collect(Collectors.toList());
				if (macScans.size() <= 1) {
					JOptionPane.showMessageDialog(MainFrame.this,
							"There are only " + macScans.size() + " scans result for this mac", "Insufficient scans",
							JOptionPane.ERROR_MESSAGE);
					clearAlgo1Result();
					return;
				}
				double sumAlt = 0;
				double sumLon = 0;
				double sumLat = 0;
				double totalWeight = 0;
				for (Pair<ScanInfo, WifiNetwork> p : macScans) {
					double weight = 1.0 / Math.pow(p.getRight().signal, 2);
					totalWeight += weight;
					sumAlt += weight * p.getLeft().altitude;
					sumLat += weight * p.getLeft().latitude;
					sumLon += weight * p.getLeft().longitude;
				}
				DecimalFormat df = new DecimalFormat("#.######");
				df.setRoundingMode(RoundingMode.CEILING);

				algo1results.setText(df.format(sumLat / totalWeight) + " , " + df.format(sumLon / totalWeight) + " , "
						+ df.format(sumAlt / totalWeight));
			}
		});
		submitAlgo2.addActionListener(new ActionListener() {
			double power = 2;
			double norm = 10000.0;
			double sigdiff = 0.4;
			double mindiff = 3;
			double nosignal = -120;
			double diffnosig = 100;

			String inputMac1;
			double inputSig1;
			String inputMac2;
			double inputSig2;
			String inputMac3;
			double inputSig3;

			@Override
			public void actionPerformed(ActionEvent ae) {

				try {
					inputMac1 = mac1.getText();
					inputSig1 = Double.parseDouble(sig1.getText());
					inputMac2 = mac2.getText();
					inputSig2 = Double.parseDouble(sig2.getText());
					inputMac3 = mac3.getText();
					inputSig3 = Double.parseDouble(sig3.getText());

					List<Pair<ScanInfo, Double>> pairs = scanService.scans.entrySet().stream()
							.filter(e -> e.getValue().stream().anyMatch(w -> w.mac.equals(inputMac1))
									&& e.getValue().stream().anyMatch(w -> w.mac.equals(inputMac2))
									&& e.getValue().stream().anyMatch(w -> w.mac.equals(inputMac3))) //
							.map(e -> new Four<ScanInfo, WifiNetwork>(e.getKey(),
									e.getValue().stream().filter(w -> w.mac.equals(inputMac1)).findFirst().get(),
									e.getValue().stream().filter(w -> w.mac.equals(inputMac2)).findFirst().get(),
									e.getValue().stream().filter(w -> w.mac.equals(inputMac3)).findFirst().get()))
							.map(f -> new Pair<Four<ScanInfo, WifiNetwork>, Double>(f, calculateWeight(f))) //
							.map(p -> new Pair<ScanInfo, Double>(p.getLeft().getLeft(), p.getRight()))
							.sorted((p1, p2) -> (-1) * Double.compare(p1.getRight(), p2.getRight())) //
							.limit(3) //
							.collect(Collectors.toList());
					if (pairs.isEmpty()) {
						JOptionPane.showMessageDialog(MainFrame.this, "There are no scans that includes those 3 macs",
								"Insufficient scans", JOptionPane.ERROR_MESSAGE);
						clearAlgo2Result();
						return;
					}
					double sumAlt = 0;
					double sumLon = 0;
					double sumLat = 0;
					double totalWeight = 0;
					for (Pair<ScanInfo, Double> p : pairs) {
						double weight = p.getRight();
						totalWeight += weight;
						sumAlt += weight * p.getLeft().altitude;
						sumLat += weight * p.getLeft().latitude;
						sumLon += weight * p.getLeft().longitude;
					}
					DecimalFormat df = new DecimalFormat("#.######");
					df.setRoundingMode(RoundingMode.CEILING);

					algo2results.setText(df.format(sumLat / totalWeight) + " , " + df.format(sumLon / totalWeight)
							+ " , " + df.format(sumAlt / totalWeight));

				} catch (Exception ex) {

					JOptionPane.showMessageDialog(MainFrame.this, "Bad signal values", "Input error",
							JOptionPane.ERROR_MESSAGE);
					clearAlgo1Result();
					return;
				}
			}

			private double calculateWeight(Four<ScanInfo, WifiNetwork> f) {
				return //
				calculateWeight(calculateDiff(f.getRight1().signal, inputSig1), inputSig1) * //
				calculateWeight(calculateDiff(f.getRight2().signal, inputSig2), inputSig2) * //
				calculateWeight(calculateDiff(f.getRight3().signal, inputSig3), inputSig3);
			}

			private double calculateWeight(double diff, double inputsig) {
				return norm / (Math.pow(diff, sigdiff) * Math.pow(inputsig, power));
			}

			public double calculateDiff(double sig, double inputsig) {
				return doubleEquals(sig, nosignal) ? diffnosig : Math.max(Math.abs(sig - inputsig), mindiff);
			}

			public boolean doubleEquals(double x, double y) {
				return Double.doubleToLongBits(x) == Double.doubleToLongBits(y);
			}
		});
	}

	public void clear() {
		timeMin.setText("Min [dd/MM/yyyy HH:mm]");
		timeMax.setText("Max [dd/MM/yyyy HH:mm]");
		latMin.setText("Min");
		latMax.setText("Max");
		lonMin.setText("Min");
		lonMax.setText("Max");
		altMin.setText("Min");
		altMax.setText("Max");
		mac.setText("MAC");
		mac1.setText("MAC1");
		sig1.setText("SIG1");
		mac2.setText("MAC2");
		sig2.setText("SIG2");
		mac3.setText("MAC3");
		sig3.setText("SIG3");
		scanService.clear();
		clearAlgo1Result();
		clearAlgo2Result();
		group.clearSelection();
	}

	public void clearAlgo1Result() {
		algo1results.setText("Lat , Lon , Alt");

	}

	public void clearAlgo2Result() {
		algo2results.setText("Lat , Lon , Alt");

	}

	public void initialize() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(new Dimension(800, 900));
		this.setTitle("WiFi scans analyzer");
		this.setResizable(false);

		Font font = new Font("Courier", Font.LAYOUT_LEFT_TO_RIGHT, 15);
		altMin.setFont(font);
		altMax.setFont(font);
		timeMin.setFont(font);
		timeMax.setFont(font);
		lonMin.setFont(font);
		lonMax.setFont(font);
		latMin.setFont(font);
		latMax.setFont(font);

		JPanel dataPanel = new JPanel();
		{
			dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
			dataPanel.add(addDir);
			dataPanel.add(addCsv);
			dataPanel.add(clearData);
			dataPanel.add(saveToCsv);
			dataPanel.add(dataSizeLabel);
			dataPanel.add(numberOfApsLabel);

			TitledBorder border = new TitledBorder("Data");
			border.setTitleJustification(TitledBorder.LEFT);
			border.setTitlePosition(TitledBorder.TOP);
			dataPanel.setBorder(border);
			dataPanel.setPreferredSize(dataPanel.getPreferredSize());
			dataPanel.setBackground(Color.WHITE);
		}

		JPanel filterPanel = new JPanel();
		{
			JPanel timePanel = new JPanel();
			timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
			timePanel.add(timeLabel);
			timePanel.add(timeMin);
			timePanel.add(timeMax);
			timePanel.add(timeRadio);
			JPanel latPanel = new JPanel();
			latPanel.setLayout(new BoxLayout(latPanel, BoxLayout.X_AXIS));
			latPanel.add(latLabel);
			latPanel.add(latMin);
			latPanel.add(latMax);
			latPanel.add(latRadio);
			JPanel lonPanel = new JPanel();
			lonPanel.setLayout(new BoxLayout(lonPanel, BoxLayout.X_AXIS));
			lonPanel.add(lonLabel);
			lonPanel.add(lonMin);
			lonPanel.add(lonMax);
			lonPanel.add(lonRadio);
			JPanel altPanel = new JPanel();
			altPanel.setLayout(new BoxLayout(altPanel, BoxLayout.X_AXIS));
			altPanel.add(altLabel);
			altPanel.add(altMin);
			altPanel.add(altMax);
			altPanel.add(altRadio);

			filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
			filterPanel.add(timePanel);
			filterPanel.add(latPanel);
			filterPanel.add(lonPanel);
			filterPanel.add(altPanel);
			filterPanel.add(submitFilterButton);

			group.add(altRadio);
			group.add(lonRadio);
			group.add(latRadio);
			group.add(timeRadio);

			TitledBorder border = new TitledBorder("Filter");
			border.setTitleJustification(TitledBorder.LEFT);
			border.setTitlePosition(TitledBorder.TOP);
			filterPanel.setBorder(border);
			filterPanel.setPreferredSize(filterPanel.getPreferredSize());
			filterPanel.setBackground(Color.WHITE);
		}

		JPanel algoPanel = new JPanel();
		{
			JPanel algo1Panel = new JPanel();
			{
				JPanel algo1 = new JPanel();
				algo1.setLayout(new BoxLayout(algo1, BoxLayout.X_AXIS));
				algo1.add(mac);
				algo1.add(submitAlgo1);

				JPanel algo12 = new JPanel();
				algo12.setLayout(new BoxLayout(algo12, BoxLayout.Y_AXIS));
				algo12.add(algo1results);

				algo1Panel.setLayout(new BoxLayout(algo1Panel, BoxLayout.Y_AXIS));
				algo1Panel.add(algo1);
				algo1Panel.add(algo12);

				TitledBorder border = new TitledBorder("Algorithm 1");
				border.setTitleJustification(TitledBorder.LEFT);
				border.setTitlePosition(TitledBorder.TOP);
				algo1Panel.setBorder(border);
				algo1Panel.setBackground(Color.WHITE);
			}
			JPanel algo2Panel = new JPanel();
			{
				JPanel algo21 = new JPanel();
				algo21.setLayout(new BoxLayout(algo21, BoxLayout.X_AXIS));
				algo21.add(mac1);
				algo21.add(sig1);

				JPanel algo22 = new JPanel();
				algo22.setLayout(new BoxLayout(algo22, BoxLayout.X_AXIS));
				algo22.add(mac2);
				algo22.add(sig2);

				JPanel algo23 = new JPanel();
				algo23.setLayout(new BoxLayout(algo23, BoxLayout.X_AXIS));
				algo23.add(mac3);
				algo23.add(sig3);

				JPanel algo24 = new JPanel();
				algo24.setLayout(new BoxLayout(algo24, BoxLayout.Y_AXIS));
				submitAlgo2.setAlignmentX(Component.CENTER_ALIGNMENT);
				algo2results.setAlignmentX(Component.CENTER_ALIGNMENT);
				algo24.add(submitAlgo2);
				algo24.add(algo2results);

				algo2Panel.setLayout(new BoxLayout(algo2Panel, BoxLayout.Y_AXIS));
				algo2Panel.add(algo21);
				algo2Panel.add(algo22);
				algo2Panel.add(algo23);
				algo2Panel.add(algo24);

				TitledBorder border = new TitledBorder("Algorithm 2");
				border.setTitleJustification(TitledBorder.LEFT);
				border.setTitlePosition(TitledBorder.TOP);
				algo2Panel.setBorder(border);
				algo2Panel.setBackground(Color.WHITE);
			}

			algoPanel.setLayout(new BoxLayout(algoPanel, BoxLayout.Y_AXIS));
			algoPanel.add(algo1Panel);
			algoPanel.add(algo2Panel);

			TitledBorder border = new TitledBorder("Algorithms");
			border.setTitleJustification(TitledBorder.LEFT);
			border.setTitlePosition(TitledBorder.TOP);
			algoPanel.setBorder(border);
			algoPanel.setBackground(Color.WHITE);

		}

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		mainPanel.add(dataPanel);
		mainPanel.add(filterPanel);
		mainPanel.add(algoPanel);

		mainPanel.setBackground(Color.WHITE);
		dataPanel.setBackground(Color.WHITE);
		filterPanel.setBackground(Color.WHITE);

		Container contentPane = getContentPane();
		contentPane.setBackground(Color.WHITE);
		contentPane.add(mainPanel, BorderLayout.CENTER);

		this.pack();
	}

}
