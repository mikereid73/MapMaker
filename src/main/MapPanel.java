package main;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MapPanel extends JPanel implements MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_COLUMNS = 24;
	private static final int DEFAULT_ROWS = 18;
	
	private static final int DEFAULT_TILE_WIDTH = 32;
	private static final int DEFAULT_TILE_HEIGHT = 32;
	
	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 600;
	
	private static final int MINIMUM_COLUMNS = 2;
	private static final int MINIMUM_ROWS = 2;
	
	private static final int MAXIMUM_COLUMS = 1024;
	private static final int MAXIMUM_ROWS = 1024;
	
	private int columns;
	private int rows;
	
	private int tileWidth;
	private int tileHeight;
	
	private int mapWidth;
	private int mapHeight;
	
	private boolean drawGrid;
	private boolean drawTileLayer;
	private boolean drawCollisionLayer;
	
	private int[][] tileLayer;
	private int[][] collisionLayer;
	
	private BufferedImage[][] tileImages;
	private BufferedImage paintbrush;
	private int paintID;
	private DrawLayer drawLayer;
	
	private int mouseX;
	private int mouseY;
	
	private boolean canDraw;
	
	private enum DrawLayer {
		tileLayer,
		collisionLayer
	}
	
	public MapPanel() {
		this(DEFAULT_COLUMNS, DEFAULT_ROWS, DEFAULT_TILE_WIDTH, DEFAULT_TILE_HEIGHT);
	}
	
	public MapPanel(int columns, int rows, int tileWidth, int tileHeight) {
		this.columns = columns;
		this.rows = rows;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		
		mapWidth = DEFAULT_WIDTH;
		mapHeight = DEFAULT_HEIGHT;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		initialise();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(mapWidth, mapHeight);
	}
		
	public void initialise() {
		tileLayer = new int[rows][columns];
		collisionLayer = new int[rows][columns];
		
		tileImages = new BufferedImage[rows][columns];
		
		drawTileLayer = true;
		drawCollisionLayer = true;
		drawGrid = true;
	}	
	
	public void setPaintbrush(BufferedImage paintbrush, int paintID) {
		this.paintbrush = paintbrush;
		this.paintID = paintID;
	}
	
	public void setDrawTileLayer(boolean drawTileLayer) {
		this.drawTileLayer = drawTileLayer;
		repaint();
	}
	
	public void setDrawCollisionLayer(boolean drawCollisionLayer) {
		this.drawCollisionLayer = drawCollisionLayer;
		repaint();
	}
	
	public void setDrawGrid(boolean drawGrid) {
		this.drawGrid = drawGrid;
		repaint();
	}
	
	public void drawTileLayer() {
		if(drawLayer == DrawLayer.tileLayer) {
			drawLayer = null;
		}
		else {
			drawLayer = DrawLayer.tileLayer;
		}
		repaint();
	}
	
	public void drawCollisionLayer() {
		if(drawLayer == DrawLayer.collisionLayer){
			drawLayer = null;
		}
		else {
			drawLayer = DrawLayer.collisionLayer;
		}
		repaint();
	}
	
	private void drawToLayer(MouseEvent e) {
		if(!canDraw) { return; }
		
		if(drawLayer == DrawLayer.collisionLayer) {
			drawToCollisionLayer(e);
		}
		else if(drawLayer == DrawLayer.tileLayer) {
			drawToTileLayer(e);
		}
	}
	
	private void drawToTileLayer(MouseEvent e) {
		int x = e.getX() / tileWidth;
		int y = e.getY() / tileHeight;
		if(x >= 0 && x < columns && y >= 0 && y < rows) {
			if(SwingUtilities.isLeftMouseButton(e)) {
				tileLayer[y][x] = paintID;
				tileImages[y][x] = paintbrush;
			}
			else if(SwingUtilities.isRightMouseButton(e)) {
				tileImages[y][x] = null;
				tileLayer[y][x] = 0;
			}
		}
		repaint();
	}
	
	private void drawToCollisionLayer(MouseEvent e) {
		int x = e.getX() / tileWidth;
		int y = e.getY() / tileHeight;
		
		if(x >= 0 && x < columns && y >= 0 && y < rows) {
			if(SwingUtilities.isLeftMouseButton(e)) {
				collisionLayer[y][x] = 1;
			}
			else if(SwingUtilities.isRightMouseButton(e)) {
				collisionLayer[y][x] = 0;
			}
		}
		repaint();
	}
	
	public void addColumn() {
		if(columns >= MAXIMUM_COLUMS) { return; }
		
		columns++;
		mapWidth = columns * tileWidth;
		
		int[][] tileLayer = new int[rows][columns];
		int[][] collisionLayer = new int[rows][columns];
		BufferedImage[][] tileImages = new BufferedImage[rows][columns];
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < columns - 1; x++) {
				tileLayer[y][x] = this.tileLayer[y][x];
				collisionLayer[y][x] = this.collisionLayer[y][x];
				tileImages[y][x] = this.tileImages[y][x];
			}
		}
		
		this.tileLayer = tileLayer;
		this.collisionLayer = collisionLayer;
		this.tileImages = tileImages;
		
		repaint();
		revalidate();
	}
	
	public void addRow() {
		if(rows >= MAXIMUM_ROWS) { return; }
		
		rows++;
		mapHeight = rows * tileHeight;
		
		int[][] tileLayer = new int[rows][columns];
		int[][] collisionLayer = new int[rows][columns];
		BufferedImage[][] tileImages = new BufferedImage[rows][columns];
		for(int y = 0; y < rows - 1; y++) {
			for(int x = 0; x < columns; x++) {
				tileLayer[y][x] = this.tileLayer[y][x];
				collisionLayer[y][x] = this.collisionLayer[y][x];
				tileImages[y][x] = this.tileImages[y][x];
			}
		}
		
		this.tileLayer = tileLayer;
		this.collisionLayer = collisionLayer;
		this.tileImages = tileImages;
		
		repaint();
		revalidate();
	}
	
	public void removeColumn() {		
		if(columns <= MINIMUM_COLUMNS) { return; }
		
		columns--;
		mapWidth = columns * tileWidth;
		
		int[][] tileLayer = new int[rows][columns];
		int[][] collisionLayer = new int[rows][columns];
		BufferedImage[][] tileImages = new BufferedImage[rows][columns];
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < columns; x++) {
				tileLayer[y][x] = this.tileLayer[y][x];
				collisionLayer[y][x] = this.collisionLayer[y][x];
				tileImages[y][x] = this.tileImages[y][x];
			}
		}
		
		this.tileLayer = tileLayer;
		this.collisionLayer = collisionLayer;
		this.tileImages = tileImages;
		
		repaint();
		revalidate();
	}
	
	public void removeRow() {
		if(rows <= MINIMUM_ROWS) { return; }
		
		rows--;
		mapHeight = rows * tileHeight;
		
		int[][] tileLayer = new int[rows][columns];
		int[][] collisionLayer = new int[rows][columns];
		BufferedImage[][] tileImages = new BufferedImage[rows][columns];
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < columns; x++) {
				tileLayer[y][x] = this.tileLayer[y][x];
				collisionLayer[y][x] = this.collisionLayer[y][x];
				tileImages[y][x] = this.tileImages[y][x];
			}
		}
		
		this.tileLayer = tileLayer;
		this.collisionLayer = collisionLayer;
		this.tileImages = tileImages;
		
		repaint();
		revalidate();
	}
	
	public void loadNewMap(int columns, int rows, int tileWidth, int tileHeight) {
		this.columns = columns;
		this.rows = rows;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		
		mapWidth = columns * tileWidth;
		mapHeight = rows * tileHeight;
		
		tileLayer = new int[rows][columns];
		collisionLayer = new int[rows][columns];
		tileImages = new BufferedImage[rows][columns];
		
		revalidate();
	}
	
	public void autoFillCollision() {
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < columns; x++) {
				if(tileLayer[y][x] != 0) {
					collisionLayer[y][x] = 1;
				}
			}
		}
		repaint();
	}
	
	public void setMousePosition(MouseEvent e) {
		int x = e.getX() / tileWidth;
		int y = e.getY() / tileHeight;
		
		x = Math.min(x, columns - 1);
		x = Math.max(x, 0);
		y = Math.min(y, rows - 1);
		y = Math.max(y, 0);
		mouseX = x;
		mouseY = y;
	}
	
	public void setCanDraw(boolean canDraw) {
		this.canDraw = canDraw;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);		
		
		Graphics2D g2d = (Graphics2D)g;
		Composite originalComposite = g2d.getComposite();
		
		if(drawTileLayer) {
			for(int y = 0; y < rows; y++) {
				for(int x = 0; x < columns; x++) {
					BufferedImage image = tileImages[y][x];
					if(image != null) {
						g2d.drawImage(image, x * tileWidth, y * tileHeight, tileWidth, tileHeight, null);
					}					
				}
			}
		}
		
		if(drawCollisionLayer) {			
			g2d.setColor(Color.RED);
			g2d.setStroke(new BasicStroke(3));
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			for(int y = 0; y < rows; y++) {
				for(int x = 0; x < columns; x++) {
					int value = collisionLayer[y][x];
					if(value == 1) {
						g2d.drawRect(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
						g2d.setColor(Color.RED.brighter());
						g2d.fillRect(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
					}
				}
			}
			g2d.setStroke(new BasicStroke(1));
			g2d.setComposite(originalComposite);
		}
		
		if(drawLayer == DrawLayer.tileLayer) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2d.drawImage(paintbrush, mouseX * tileWidth, mouseY * tileHeight, tileWidth, tileHeight, null);
			g2d.setComposite(originalComposite);
		}
		else if(drawLayer == DrawLayer.collisionLayer) {
			g2d.setColor(Color.RED);
			g2d.setStroke(new BasicStroke(3));
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2d.drawRect(mouseX * tileWidth, mouseY * tileHeight, tileWidth, tileHeight);
			g2d.setColor(Color.RED.brighter());
			g2d.fillRect(mouseX * tileWidth, mouseY * tileHeight, tileWidth, tileHeight);
			g2d.setStroke(new BasicStroke(1));
			g2d.setComposite(originalComposite);
		}
		
		if(drawGrid) {
			g2d.setColor(Color.BLACK);
			// draw outer box
			g2d.drawRect(0, 0, columns * tileWidth, rows * tileHeight);
			// draw vertical lines
			for(int i = tileWidth; i < columns * tileWidth; i += tileWidth) {
				g2d.drawLine(i, 0, i, rows * tileHeight);
			}
			// draw horizontal lines
			for(int i = tileHeight; i < rows * tileHeight; i += tileHeight) {
				g2d.drawLine(0, i, columns * tileWidth, i);
			}
		}
		
	}
		

	@Override
	public void mouseDragged(MouseEvent e) {
		drawToLayer(e);
		setMousePosition(e);
		repaint();
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		drawToLayer(e);		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		drawToLayer(e);		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setMousePosition(e);
		repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {			
	}

	@Override
	public void mouseEntered(MouseEvent e) {		
	}

	@Override
	public void mouseExited(MouseEvent e) {				
	}	
	
	public String mapToString() {
		String output = "";
		output += columns + "\n" +  rows + "\n";
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < columns; x++) {
				output += tileLayer[y][x] + " ";
			}
			output += "\n";
		}
		
		return output;
	}
	
	public String collisionToString() {
		String output = "";
		output += columns + "\n" + rows + "\n";
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < columns; x++) {
				output += collisionLayer[y][x] + " ";
			}
			output += "\n";
		}
		
		return output;
	}

}
