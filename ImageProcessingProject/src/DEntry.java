

public class DEntry {
	int tag;
	int type;
	int count;
	int valueOrOff;
	static String dataType[] = {"","BYTE","ASCII","SHORT","LONG",
			"RATIONAL","SBYTE","UNDEFINEDED","SSHORT","SLONG",
			"SRATIONAL","FLOAT","DOUBLE"};
	static int dataSize[] = {0,1,1,2,4,4,16,1,1,2,4,16,4,8};
	DEntry(int arr[], int x, String ending){
		if(ending == "II") {
			tag =decode2(arr[x+1],arr[x]);
			type = decode2(arr[x+3],arr[x+2]);
			count = decode4(arr[x+7],arr[x+6],arr[x+5],arr[x+4]);
			valueOrOff = decode4(arr[x+11],arr[x+10],arr[x+9],arr[x+8]);
		}
		else if(ending == "MM") {
			tag =decode2(arr[x],arr[x+1]);
			type = decode2(arr[x+2],arr[x+3]);
			count = decode4(arr[x+4],arr[x+5],arr[x+6],arr[x+7]);
			if(dataSize[type]*count>4) 
				valueOrOff = decode4(arr[x+8],arr[x+9],arr[x+10],arr[x+11]);
			else if(dataSize[type] == 2) {
				valueOrOff = decode2(arr[x+8],arr[x+9]);
			}
			else if(dataSize[type] == 3) {
				valueOrOff = decode3(arr[x+8],arr[x+9],arr[x+10]);
			}
			else if(dataSize[type] == 4) {
				valueOrOff = decode4(arr[x+8],arr[x+9],arr[x+10],arr[x+11]);
			}
		}
//		System.out.println("Tag:"+tag);
//		System.out.println("Type:"+type);
//		System.out.println("count:"+count);
//		System.out.println("valueOrOff:"+valueOrOff);
//		System.out.println();
	}
	public int getTag() {
		return tag;
	}
	public int getType() {
		return type;
	}
	public int getCount() {
		return count;
	}
	public int getValue() {
		return valueOrOff;
	}
	public int getDataSize() {
		return dataSize[type];
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
	public int decode3(int x,int y, int z) {
		String s = Integer.toHexString(x);
		String t = Integer.toHexString(y);
		String v = Integer.toHexString(z);
		while(t.length()<2) 
			t = "0"+t;
		while(s.length()<2) 
			s = "0"+s;
		while(v.length()<2) 
			v = "0"+v;
		return Integer.parseInt(s+t+v,16);
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
	public void showEntry() {
		System.out.println("Tag:" + decodeTag(tag));
		System.out.println("Type:" + dataType[type]);
		System.out.println("Count:" + count);
		System.out.println("Value/Offset:" + valueOrOff);
	}
	public Boolean isOffset() {
		if(count * dataSize[type] >4) return true;
		else return false;
	}
	public String decodeTag(double x) {
		if(x==256) return "ImageWidth";
		else if(x==257) return "ImageLength";
		else if(x==258) return "BitsPerSample";
		else if(x==259) return "Compression";
		else if(x==262) return "PhotometricInterpretation";
		else if(x==273) return "StripOffsets";
		else if(x==277) return "SamplesPerPixel";
		else if(x==278) return "RowsPerStrip";
		else if(x==279) return "StripByteCounts";
		else if(x==282) return "XResolution";
		else if(x==283) return "YResolution";
		else if(x==296) return "ResolutionUnit";
		else return "Undefined";
	
	}
}