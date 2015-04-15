package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class TileSet extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	
	private final int DEFAULT_TILE_WIDTH = 32;
	private final int DEFAULT_TILE_HEIGHT = 32;
	
	private final int DEFAULT_WIDTH = 256;
	private final int DEFAULT_HEIGHT = 600;
	
	private final int DEFAULT_PADDING = 5;
	
	private BufferedImage tileset;
	private BufferedImage[][] tiles;
	
	private int columns;
	private int rows;
	
	private int panelWidth;
	private int panelHeight;
	
	private int tileWidth;
	private int tileHeight;
	
	private int paddingWidth;
	private int paddingHeight;
	
	private MapPanel mapPanel;
	private BufferedImage paintbrush;
	private int px;
	private int py;
	
	public TileSet(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
		
		tileWidth = DEFAULT_TILE_WIDTH;
		tileHeight = DEFAULT_TILE_HEIGHT;
		
		panelWidth = DEFAULT_WIDTH;
		panelHeight = DEFAULT_HEIGHT;
		
		paddingWidth = DEFAULT_PADDING;
		paddingHeight = DEFAULT_PADDING;

		addMouseListener(this);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(panelWidth, panelHeight);
	}
	
	public void loadTileset(int tileWidth, int tileHeight, BufferedImage tileset) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.tileset = tileset;
		
		columns = tileset.getWidth() / tileWidth;
		rows = tileset.getHeight() / tileHeight;
		
		tiles = new BufferedImage[rows][columns];
		
		panelWidth = columns * tileWidth + paddingWidth * columns;
		panelHeight = rows * tileHeight + paddingHeight * rows;
		
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < columns; x++) {
				BufferedImage image = tileset.getSubimage(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
				tiles[y][x] = image;
			}
		}
		
		revalidate();
		repaint();
	}
	
	private void applyPaintBrush(int x, int y) {		
		x = x / (tileWidth + DEFAULT_PADDING);
		y = y / (tileHeight + DEFAULT_PADDING);
		if(x >= 0 && x < columns && y >= 0 && y < rows) {
			int id = x + y * columns;
			px = x;
			py = y;
			paintbrush = tiles[y][x];
			mapPanel.setPaintbrush(paintbrush, id);
		}
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setColor(Color.GRAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < columns; x++) {
				BufferedImage image = tiles[y][x];
				if(image != null) {
					g2d.drawImage(image, x * (tileWidth + DEFAULT_PADDING), y * (tileHeight + DEFAULT_PADDING), tileWidth, tileHeight, null);
				}
			}
		}
		if(tileset != null) {
			g2d.setColor(Color.CYAN);
			g2d.setStroke(new BasicStroke(3));
			g2d.drawRect(px * (tileWidth + DEFAULT_PADDING), py * (tileHeight + DEFAULT_PADDING), tileWidth, tileHeight);
			g2d.setStroke(new BasicStroke(1));
		}
			
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		applyPaintBrush(x, y);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		applyPaintBrush(x, y);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
