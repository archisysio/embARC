package com.portalmedia.embarc.parser;

import java.io.FileNotFoundException;

public class FileFormatDetection {
	
	public static FileFormat getFileFormat(String file)
	{
		if(isDPX(file)) return FileFormat.DPX;
				
		return FileFormat.OTHER;
	}
	
	public static boolean isDPX(String file) {
		BinaryFileReader f;
		try {
			f = new BinaryFileReader(file);
			String firstFourBytes = f.readAscii(4);
			
			f.skip(4);
			
			String nextSequence = f.readAscii(4);

			f.close();
			if(firstFourBytes.equals("SDPX") && nextSequence.matches("[vV][1-9][.][0-9]"))
				return true;
			else if(firstFourBytes.equals("XPDS") && nextSequence.matches("[vV][1-9][.][0-9]"))
				return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
