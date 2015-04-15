package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultFormatter;

public class MapFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private MapPanel mapPanel;
	private TileSet tileset;
	
	private JMenuBar menubar;
	private JToolBar toolbar;
	
	private JPanel contentPane;
	
	public MapFrame() {
		super("Tile Map Maker");
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		mapPanel = new MapPanel();
		tileset = new TileSet(mapPanel);
		
		menubar = new JMenuBar();
		toolbar = new JToolBar();
		
		JScrollPane scrollpane1 = new JScrollPane(mapPanel);
		JScrollPane scrollpane2 = new JScrollPane(tileset);
		
		contentPane = new JPanel(new BorderLayout());
		contentPane.add(scrollpane1, BorderLayout.CENTER);
		contentPane.add(scrollpane2, BorderLayout.EAST);
		setContentPane(contentPane);
		
		initialise();

		pack();
		setLocationRelativeTo(null);
	}
	
	public void initialise() {
		/* setup file menu and add action listeners */
		JMenu fileMenu = new JMenu("File");
		JMenuItem fileNew = new JMenuItem("New");
		fileNew.addActionListener(e -> {
			LoadTilesetDialog ltd = new LoadTilesetDialog(this);
			ltd.setAlwaysOnTop(true);
			ltd.setVisible(true);
		});
		JMenuItem fileClose = new JMenuItem("Close");
		fileClose.addActionListener(e -> {
			int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit", 
						 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(result == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		});
		fileMenu.add(fileNew);
		fileMenu.addSeparator();
		fileMenu.add(fileClose);		
		menubar.add(fileMenu);	
		
		/* setup view menu and add action listeners */
		JMenu viewMenu = new JMenu("View");
		JCheckBoxMenuItem viewTileLayer = new JCheckBoxMenuItem("View Tile Layer");
		viewTileLayer.setSelected(true);
		viewTileLayer.addActionListener(e -> {
			mapPanel.setDrawTileLayer(viewTileLayer.isSelected());
		});
		JCheckBoxMenuItem viewCollisionLayer = new JCheckBoxMenuItem("View Collision Layer");
		viewCollisionLayer.setSelected(true);
		viewCollisionLayer.addActionListener(e -> {
			mapPanel.setDrawCollisionLayer(viewCollisionLayer.isSelected());
		});
		JCheckBoxMenuItem viewGrid = new JCheckBoxMenuItem("View Grid");
		viewGrid.addActionListener(e -> {
			mapPanel.setDrawGrid(viewGrid.isSelected());
		});
		viewGrid.setSelected(true);
		
		viewMenu.add(viewTileLayer);		
		viewMenu.add(viewCollisionLayer);
		
		viewMenu.add(viewGrid);
		menubar.add(viewMenu);
		
		setJMenuBar(menubar);
		
		/* setup tool bar and add action listeners */		
		toolbar.setFloatable(false);
		toolbar.setBorder(new LineBorder(Color.DARK_GRAY));
		JButton addColumnButton = new JButton(new ImageIcon(getClass().getResource("/AddColumnIcon.png")));
		addColumnButton.setFocusable(false);
		addColumnButton.addActionListener(e -> {
			mapPanel.addColumn();
		});
		JButton addRowButton = new JButton(new ImageIcon(getClass().getResource("/AddRowIcon.png")));
		addRowButton.setFocusable(false);
		addRowButton.addActionListener(e -> {
			mapPanel.addRow();
		});
		JButton removeColumnButton = new JButton(new ImageIcon(getClass().getResource("/RemoveColumnIcon.png")));
		removeColumnButton.setFocusable(false);
		removeColumnButton.addActionListener(e -> {
			mapPanel.removeColumn();
		});
		JButton removeRowButton = new JButton(new ImageIcon(getClass().getResource("/RemoveRowIcon.png")));
		removeRowButton.setFocusable(false);
		removeRowButton.addActionListener(e -> {
			mapPanel.removeRow();
		});
		
		JButton export = new JButton("Export Map");
		export.setFocusable(false);
		export.addActionListener(e -> {
			saveToFile();
		});
		
		JButton autofill = new JButton("Auto Collision Layer");
		autofill.setFocusable(false);
		autofill.addActionListener(e -> {
			mapPanel.autoFillCollision();
			if(!viewCollisionLayer.isSelected()) {
				viewCollisionLayer.doClick();
			}
		});
		
		JToggleButton drawTileLayer = new JToggleButton("Draw Tiles");
		drawTileLayer.setFocusable(false);
		drawTileLayer.addActionListener(e -> {
			mapPanel.drawTileLayer();
		});
		JToggleButton drawCollisionLayer = new JToggleButton("Draw Collisions");
		drawCollisionLayer.setFocusable(false);
		drawCollisionLayer.addActionListener(e -> {
			mapPanel.drawCollisionLayer();
		});
		MyButtonGroup bg = new MyButtonGroup();
		bg.add(drawTileLayer);
		bg.add(drawCollisionLayer);
		
		toolbar.add(addColumnButton);
		toolbar.add(addRowButton);
		toolbar.add(removeColumnButton);
		toolbar.add(removeRowButton);
		toolbar.addSeparator();
		toolbar.add(export);
		toolbar.addSeparator();
		toolbar.add(autofill);
		toolbar.addSeparator();
		toolbar.add(drawTileLayer);
		toolbar.add(drawCollisionLayer);
		add(toolbar, BorderLayout.NORTH);
		
		/* setup frame icon */
		ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
		setIconImage(icon.getImage());
	}
	
	/* save map to file */
	private void saveToFile() {
		String map = mapPanel.mapToString();
		String collision = mapPanel.collisionToString();
		
		JFileChooser fchooser = new JFileChooser();
		int result = fchooser.showSaveDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			try {
				FileWriter fw = new FileWriter(fchooser.getSelectedFile() + ".txt");
				fw.write((map + collision).trim());
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
	/* load tileset */
	public void loadTileset(int tileWidth, int tileHeight, BufferedImage tileset) {
		this.tileset.loadTileset(tileWidth, tileHeight, tileset);
	}
	
	/* load a new empty map */
	public void loadNewMap(int columns, int rows, int tileWidth, int tileHeight) {
		mapPanel.loadNewMap(columns, rows, tileWidth, tileHeight);
		mapPanel.setCanDraw(true);
	}

}

class MyButtonGroup extends ButtonGroup {

	private static final long serialVersionUID = 1L;

	@Override
	public void setSelected(ButtonModel model, boolean selected) {

		if(selected) {
			super.setSelected(model, selected);
		} 
		else {
			clearSelection();
		}
	}
}

class LoadTilesetDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private MapFrame mapFrame;
	
	public LoadTilesetDialog(MapFrame parent) {
		super(parent, "Load Tileset");
		
		this.mapFrame = parent;
		
		initialise();
		
		setResizable(false);
		pack();
		setLocationRelativeTo(parent);
		setModalityType(DEFAULT_MODALITY_TYPE);
	}
	
	private void initialise() {
		JPanel contentPane = new JPanel(new GridLayout(4, 1));
		JButton pathButton = new JButton("Load...");
		JTextField pathField = new JTextField(10);
		JButton okButton = new JButton("OK");
		
		JSpinner spinnerWidth = new JSpinner(new SpinnerNumberModel(32, 1, 9999, 1));
		JSpinner.NumberEditor jsEditor1 = (JSpinner.NumberEditor)spinnerWidth.getEditor();
		DefaultFormatter formatter1 = (DefaultFormatter) jsEditor1.getTextField().getFormatter();
		formatter1.setAllowsInvalid(false);
		
		JSpinner spinnerHeight = new JSpinner(new SpinnerNumberModel(32, 1, 9999, 1));
		JSpinner.NumberEditor jsEditor2 = (JSpinner.NumberEditor)spinnerHeight.getEditor();
		DefaultFormatter formatter2 = (DefaultFormatter) jsEditor2.getTextField().getFormatter();
		formatter2.setAllowsInvalid(false);
		
		JSpinner spinnerMapWidth = new JSpinner(new SpinnerNumberModel(24, 1, 9999, 1));
		JSpinner.NumberEditor jsEditor3 = (JSpinner.NumberEditor)spinnerMapWidth.getEditor();
		DefaultFormatter formatter3 = (DefaultFormatter) jsEditor3.getTextField().getFormatter();
		formatter3.setAllowsInvalid(false);
		
		JSpinner spinnerMapHeight = new JSpinner(new SpinnerNumberModel(18, 1, 9999, 1));
		JSpinner.NumberEditor jsEditor4 = (JSpinner.NumberEditor)spinnerMapHeight.getEditor();
		DefaultFormatter formatter4 = (DefaultFormatter) jsEditor4.getTextField().getFormatter();
		formatter4.setAllowsInvalid(false);
		
		pathButton.addActionListener(e -> {
			JFileChooser fc = new JFileChooser();
			int result = fc.showOpenDialog(this);
			if(result == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				pathField.setText(file.getAbsolutePath());
			}
		});
		
		okButton.addActionListener(e -> {
			try {
				int columns = Integer.parseInt(spinnerMapWidth.getValue().toString());
				int rows = Integer.parseInt(spinnerMapHeight.getValue().toString());;
				int tileWidth = Integer.parseInt(spinnerWidth.getValue().toString());;
				int tileHeight = Integer.parseInt(spinnerHeight.getValue().toString());;
				BufferedImage tileset = ImageIO.read(new File(pathField.getText()));
				
				mapFrame.loadTileset(tileWidth, tileHeight, tileset);
				mapFrame.loadNewMap(columns, rows, tileWidth, tileHeight);
				dispose();
				
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(this, "Please check path and/or values","Error", JOptionPane.ERROR_MESSAGE);				
			}
		});
		
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();
		
		panel1.add(pathButton);
		panel1.add(pathField);
		
		panel2.add(new JLabel("Tile Width: "));
		panel2.add(spinnerWidth);
		panel2.add(new JLabel("Tile Height: "));
		panel2.add(spinnerHeight);
		
		panel3.add(new JLabel("Map Width: "));
		panel3.add(spinnerMapWidth);
		panel3.add(new JLabel("Map Height: "));
		panel3.add(spinnerMapHeight);
		
		panel4.add(okButton);
		
		contentPane.add(panel1);
		contentPane.add(panel2);
		contentPane.add(panel3);
		contentPane.add(panel4);
		
		setContentPane(contentPane);
	}
	
}