import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class ImageLabel extends JLabel implements ActionListener{
	BufferedImage img;
	ImageLabel(){
		this.setPreferredSize(new Dimension(512,512));
	}
	ImageLabel(BufferedImage ig){
		this.setBorder(BorderFactory.createLineBorder(Color.green));
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
		System.out.println("repainted"+img.getHeight());
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("performed2");
		repaint();
	}
	
}