import java.awt.Color;
import java.awt.image.BufferedImage;

public class Func {
	static double rgb2yuv[][]= {{.299,.587,.114},
						{-.299,-.587,.886},
						{.701,-.587,-.114}};
	static double yuv2rgb[][]= {{1,0,1},
						{1,-.1942,-.5094},
						{1,1,0}};
	static int rgbMax = 16777216;

//	//2x2 dithering matrix
//	static int ditherMtx[][] = {{0,3},{2,1}};

	//4x4 dithering matrix
//	static int ditherMtx[][] = {{0,8,2,10},{12,4,14,6},
//								{3,11,1,9},{15,7,13,5}};

	//8x8 dithering matrix
	static int ditherMtx[][] = {
			{0,32,8,40,2,34,10,42},
			{48,16,56,24,50,18,58,26},
			{12,14,4,36,14,46,6,38},
			{60,28,52,20,60,30,54,22},
			{3,35,11,43,1,33,9,41},
			{51,19,59,27,49,17,57,25},
			{15,47,7,39,13,45,5,37},
			{63,31,55,23,61,29,53,21}}; 
//	
	//Make grayscale image
	public static BufferedImage grayscale(final BufferedImage img) {
		int width = img.getWidth(), height = img.getHeight();
		BufferedImage ret = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB_PRE);
		for(int i = 0;i<height;i++) 
			for(int j  = 0; j< width;j++) {
				Color rgb = new Color(img.getRGB(j,i));
				int y = (int)(rgb.getRed()*rgb2yuv[0][0] + rgb.getGreen()*rgb2yuv[0][1] + rgb.getBlue()*rgb2yuv[0][2]);
				ret.setRGB(j, i, new Color(y,y,y).getRGB());
			}
		return ret;
	}
	
	//To generate odered dithering image
	public static BufferedImage dithering(final BufferedImage img) {
		int width = img.getWidth(), height = img.getHeight();
		BufferedImage ret = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB_PRE);
		double r = 65536, g=256;
		double max=0,min=0;
		for(int i = 0;i<height;i++) {
			for(int j = 0 ; j <width;j++) {
				Color color =new Color(img.getRGB(j, i));
				double rgb = color.getRed()*r + color.getGreen() *g +color.getBlue();
				max = Math.max(max,rgb);
				min = Math.min(min, rgb);
			}
		}
		//System.out.println("min   :"+min+"  Max: "+max);
		
		double var = (max - min) / (Math.pow(ditherMtx.length,2)); 
		int len = ditherMtx[0].length;
		for(int i = 0;i<height;i++)
			for(int j = 0; j<width;j++) {
				Color color =new Color(img.getRGB(j, i));
				double rgb = color.getRed()*r + color.getGreen() *g +color.getBlue();
				//System.out.println("y   :"+y);
				if((rgb / var)>=ditherMtx[j%len][i%len]) ret.setRGB(j, i, new Color(255,255,255).getRGB());
				else ret.setRGB(j, i, new Color(0,0,0).getRGB());
			}
		return ret;
	}
	
	//To make dynamic adjustment to image
	public static BufferedImage dynamicRange(final BufferedImage img, double scale) {
		int width = img.getWidth(), height = img.getHeight();
		BufferedImage ret = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB_PRE);
		int mean[]= {0,0,0}; //r g b
		double meanY = 0;
		for(int i = 0;i<height;i++) {
			for(int j = 0;j<width;j++){
				Color rgb = new Color(img.getRGB(j, i));
				mean[0] += rgb.getRed();
				mean[1] += rgb.getGreen();
				mean[2] += rgb.getBlue();
				meanY = meanY + rgb.getRed()*rgb2yuv[0][0] + rgb.getGreen()*rgb2yuv[0][1] + rgb.getBlue()*rgb2yuv[0][2];
			}
		}
		for(int i = 0;i<mean.length;i++) {
			mean[i]=mean[i]/width/height;
		}
		meanY /= (width*height);
		for(int i = 0;i<height;i++) 
			for(int j  = 0; j< width;j++) {
				Color rgb = new Color(img.getRGB(j, i));
				int red = rgb.getRed();
				int green = rgb.getGreen();
				int blue = rgb.getBlue();
				double y = red*rgb2yuv[0][0] + green*rgb2yuv[0][1] + blue*rgb2yuv[0][2];
				double ratio = y*scale;
				if(y>meanY) {
					if((y-ratio)>meanY) {
						red -= ratio;
						green -= ratio;
						blue -= ratio;
					}
				}
				else {
					if((y+ratio)<meanY) {
						red += ratio;
						green += ratio;
						blue += ratio;
					}
					
				}
				red = Math.max(0, Math.min(255, red));
				green = Math.max(0, Math.min(255, green));
				blue = Math.max(0, Math.min(255, blue));
				ret.setRGB(j, i, new Color(red,green,blue).getRGB());
			}
		return ret;
	}
	

}
