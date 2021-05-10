import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ImageLabel extends JLabel  {
	BufferedImage img;
	ImageLabel(){
		this.setPreferredSize(new Dimension(512,512));
		
	}
	ImageLabel(BufferedImage ig){
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		changeImage(ig);
	}
	public void changeImage(BufferedImage ig) {
		img = ig;
		this.setPreferredSize(new Dimension(img.getWidth(),img.getHeight()));
		this.updateUI();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g); 
		Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(img,0,0, null);
	}

}