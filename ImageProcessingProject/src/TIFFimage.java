


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class TIFFimage {
	BufferedImage image;
	TIFFHeader header;
	static int dataSize[] = {0,1,1,2,4,16,1,1,2,4,16,4,8};
	static String dataType[] = {"","BYTE","ASCII","SHORT","LONG",
			"RATIONAL","SBYTE","UNDEFINEDED","SSHORT","SLONG",
			"SRATIONAL","FLOAT","DOUBLE"};
	DEntry entry[];
	int raw[];
	Color rgb[];
	int stripOffset[];
	int stripByteCount[];
	int stripCount;
	int imageW;
	int imageL;
	
	TIFFimage(String path) throws IOException {
		readFile(path);
		header = new TIFFHeader(raw);
		entry = new DEntry[(int) getEntryCount()];
		readIFD();
		showInfo();
		readInfo();
		
		int countRGB=0;
		for(int i = 0;i<stripCount;i++) {
			countRGB =  countRGB + stripByteCount[i];
		}
		rgb = new Color[ countRGB/3];
		loadRGB();
		loadImage();
	}
	public int getWidth() {
		return imageW;
	}
	public int getHeight() {
		return imageL;
	}
	public Color[] getRGB() {
		return rgb.clone();
	}
	private void loadImage() {
		int k=0;
		
		image = new BufferedImage(imageW,imageL,BufferedImage.TYPE_INT_ARGB_PRE);
		for(int y = 0;y<imageL;y++) {
			for(int x = 0;x<imageW;x++) {
				image.setRGB(x, y, rgb[k++].getRGB());
			}
		}
	}
	public BufferedImage getImage() {
		return image;
	}
	private void loadRGB() {
		
		int k =0;
		for(int i = 0;i<stripCount;i++) {
			int x =  stripOffset[i];
			for(int j = 0;j < stripByteCount[i];j+=3,x+=3) {
				if(header.ending =="MM") {
					rgb[k] = new Color(raw[x], raw[x+1], raw[x+2]);
				}
				else if(header.ending =="II")
					rgb[k] = new Color(raw[x], raw[x+1], raw[x+2]);
				k++;
			}
		}
		
	}
	
	private void readInfo() {
		for(int i = 0; i<entry.length;i++) {
			int tag = entry[i].getTag();
			if(tag == 256) imageW = entry[i].getValue();
			else if(tag == 257) imageL = entry[i].getValue();
			else if(tag == 273)
			{
				stripCount = entry[i].getCount();
				if(true){//entry[i].isOffset()) {
					int x = entry[i].getValue();
					int offset = 0;
					int type = entry[i].getType();
					stripOffset = new int[stripCount];
					//System.out.println("strip offsets:"+x);
					for(int j =0;j<stripCount;j++) {
						if(type == 3) {
							if(header.ending =="MM") {
								offset = decode2(raw[x],raw[x+1]);
							}
							else if(header.ending =="II")
								offset = decode2(raw[x+1],raw[x]);
							x = x + 2;
						}
						else if(type == 4) {
							if(header.ending =="MM") {
								if(entry[i].isOffset()) {
									offset = decode4(raw[x],raw[x+1],raw[x+2],raw[x+3]);
								}
								else offset = entry[i].getValue();
							}
							else if(header.ending =="II")
								offset = decode4(raw[x+3],raw[x+2],raw[x+1],raw[x]);
							x = x + 4;
						}
						stripOffset[j] = offset;
						//System.out.println(j+": "+stripOffset[j]);
					}
				}
				else {
//					stripOffset = new long[1];
//					stripOffset[0] = entry[i].getValue();
				}
			}
			else if(tag == 279) {
				int count = entry[i].getCount();
				if(true){//{
					int x = entry[i].getValue();
					
					stripByteCount = new int[count];
					int offset = 0;
					int type = entry[i].getType();
					for(int j = 0;j<count;j++) {
						if(header.ending =="MM") {
							if(!entry[i].isOffset()) {
								offset = entry[i].getValue();
							}
							else if(type == 3) {
								offset = decode2(raw[x],raw[x+1]);
								x+=2;
							}
							else if(type == 4) {
								offset = decode4(raw[x],raw[x+1],raw[x+2],raw[x+3]);
								x+=4;
							}
						}
						else if(header.ending =="II") {
							
							if(type == 3) {
								offset = decode2(raw[x+1],raw[x]);
								x+=2;
							}
							else if(type == 4) {
								offset = decode4(raw[x+3],raw[x+2],raw[x+1],raw[x]);
								x+=4;
							}	
						}
						stripByteCount[j] = offset;
					}
				
				}
				else {
//					strupByteCount = new long[1];
//					strupByteCount[0] = entry[i].getValue();
				}
			}
		}
	}
	private void readIFD() {
		int x = header.firstIFD+2;
		for(int i = 0; i < entry.length; i++,x+=12 ) {
			entry[i]= new DEntry(raw,x,header.ending);
		}
		
	}
	private void readFile(String path) throws IOException {
		InputStream in = null;
		in = new FileInputStream(path);
		raw = new int[in.available()];
		int count = 0;
		int data = in.read();
		while(data != -1) {
			raw[count]=data;
			count++;
			data = in.read();
		}
		in.close();
		
		
//		FileWriter myWriter = new FileWriter("lenaRaw.txt");
//		for(int i = 0;i<count;i++) {
//			String s = Integer.toHexString(raw[i]);
//			if(s.length()<2) s= "0"+s;
//			myWriter.write(i +" " + s + "\n");
//		}
//		myWriter.close();
//		System.out.println("read");
	}
	public int decode2(int x,int y) {
		String s = Integer.toHexString(x);
		String t = Integer.toHexString(y);
		while(t.length()<2) 
			t = "0"+t;
		while(s.length()<2) 
			s = "0"+s;
		return Integer.parseInt(s+t,16);
	}
	
	private long getEntryCount() {
		int x = header.firstIFD;
		if(header.ending=="MM") {
			return decode2(raw[x],raw[x+1]);
		}
		else if(header.ending =="II") {
			return decode2(raw[x+1],raw[x]);
		}
		return 0;
	}

	public int decode4(int w, int x,int y,int z) {
		String s = Integer.toHexString(w);
		String t = Integer.toHexString(x);
		String u = Integer.toHexString(y);
		String v = Integer.toHexString(z);
		for(int i =1;i<=2;i++) {
			if(s.length()<i) s = "0" + s;
			if(t.length()<i) t = "0" + t;
			if(u.length()<i) u = "0" + u;
			if(v.length()<i) v = "0" + v;
		}
		s= s+t+u+v;
		return Integer.parseInt(s,16);
	}
	
	public void showInfo() {
		System.out.println("[\n\"ifdEntries\"\n:");
		for(int i =0;i<entry.length;i++) {
			entry[i].showEntry();	
			if(i != entry.length-1) System.out.println();
		}
		System.out.println("]");
	}
	
	public class TIFFHeader{
		String ending;
		String format;
		int firstIFD;
		TIFFHeader(final int rawData[]){
			if(rawData[0]==rawData[1] && rawData[1] == 73 && rawData[2] ==42 ) {
				ending = "II";
				format = "TIFF";
			}
			else if(rawData[0]==rawData[1] && rawData[1] == 77 && rawData[3] ==42 )  {
				ending ="MM";
				format = "TIFF";
			}
			else {
				format = "undefined";
				ending = "undefined";
			}
			if(ending == "II") {
				firstIFD = decode4(rawData[7],rawData[6],rawData[5],rawData[4]);
			}
			else if(ending == "MM") {
				firstIFD = decode4(rawData[4],rawData[5],rawData[6],rawData[7]);
			}
		}
		
	}
//	long ImageWidth;
//	long ImageLength;
//	short BitsPerSample;
//	short Compression;
//	short PhtotmetricInterpretation;
//	long StripOffsets;
//	short SamplesPerPixel;
//	long RowsPerStrip;
//	long StripByteCounts;
//	double XResolution;
//	double YResolution;
//	short ResolutionUnit;
}
