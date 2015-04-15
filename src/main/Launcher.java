package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Launcher {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				try {
					//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				} 
				catch (Exception e) {

					e.printStackTrace();
				}
				final MapFrame map = new MapFrame();
				map.setVisible(true);
			}
		});
	}

}
