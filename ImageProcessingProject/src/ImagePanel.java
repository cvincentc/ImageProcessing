import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel  {
	BufferedImage ig;
	JLabel imageLabel;
	JLabel labelTitle;
	int processed = 0;
	BufferedImage img;

	

	
	ImagePanel(BufferedImage img, String s){
		imageLabel = new JLabel();
		labelTitle = new JLabel();
		openImage(img,s);
		labelTitle.setSize(new Dimension(this.getWidth(),20));
		//imageLabel.setBorder(BorderFactory.createLineBorder(Color.green));
		labelTitle.setHorizontalAlignment(JLabel.CENTER);
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(new BorderLayout());
		//this.setBorder(BorderFactory.createLineBorder(Color.red));
		this.add(imageLabel,BorderLayout.CENTER);
		this.add(labelTitle,BorderLayout.SOUTH);
		
	}

	public void openImage(BufferedImage image, String s) {
			changeImage(image,s);
	}
	
	public void changeImage(BufferedImage ig, String s) {
		img=ig;
		labelTitle.setText(s);
		int viewW = (int) (ig.getWidth());
		int viewH = (int) (ig.getHeight() + labelTitle.getHeight());
		if(labelTitle.getHeight()==0) viewH += 20;
		this.setSize(new Dimension(viewW,viewH));
		this.updateUI();
	}
//	public void setViewSize(Dimension labelSize, Dimension imageSize) {
//		int viewW,viewH;
//		viewW = (int) (imageSize.getWidth());
//		viewH = (int) (imageSize.getHeight() + labelSize.getHeight());
//		if(labelSize.getHeight()==0) viewH += 20;
//		this.setPreferredSize(new Dimension(viewW,viewH));
//	}
//	
	public void paintComponent(Graphics g) {
		super.paintComponent(g); 
		Graphics2D g2d = (Graphics2D) g;
		int x = (this.getWidth() - img.getWidth()) / 2;
	    int y = (this.getHeight() - img.getHeight()) / 2;
		g2d.drawImage(img,x,y, null);
	}

	

}
