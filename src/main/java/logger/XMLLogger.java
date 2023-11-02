package logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import xml.Initializer;
import java.util.Calendar;

public class XMLLogger {
	private String filePath = null;
	public final int INFO = 1;
	public final int DEBUG = 2;
	public final int ERROR = 3;
	private int debugLevel;
	private File file = null;
	private String fileName = null;
	private static XMLLogger single_instance = null;
	private String currentDate = null;

	public static XMLLogger getInstance() {
		if (single_instance == null) {
			single_instance = new XMLLogger();
		}
		return single_instance;
	}

	public void init() {
		boolean fileStatus=false;
		this.currentDate = (new SimpleDateFormat("ddMMMMyyyy")).format(Calendar.getInstance().getTime());
		filePath = Initializer.getInstance().getProperty("Log_Path").trim();
		fileName = Initializer.getInstance().getProperty("Log_File_Name").trim();
		debugLevel = Integer.parseInt(Initializer.getInstance().getProperty("Debug_Level"));
		fileName = filePath + File.separatorChar + fileName + "-" + currentDate + ".log";
		file = new File(fileName);
		if(!file.exists()) {
			try {
				fileStatus=file.createNewFile();
				this.info("Log file create status "+fileStatus);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.info("Log file already created.");
		}
		
	}


	private void printToLog(int level, String className, String message, String type) {
		if (file==null) {
			init();
		}
		try {
			FileOutputStream fos = new FileOutputStream(file, true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			PrintWriter print = new PrintWriter(bos, true);
			if (Initializer.getInstance().getProperty("Debug").trim().equalsIgnoreCase("Y") && restrictLogs(level,debugLevel)) {
				String currentTime = (new SimpleDateFormat("MMMM dd HH:mm:ss SSS"))
						.format(Calendar.getInstance().getTime());
				message = className + type + currentTime + "---->" + message;
				print.println(message);
				print.flush();
				print.close();
				bos.close();
				fos.close();
			}

		} catch (Exception e) {

			this.error("Error while writing logs. " + e);
		}
	}
	
	private boolean restrictLogs(int level, int debugLevel) {
		if(debugLevel==0 || debugLevel== level) {
			return true;
		}
		return false;
	}

	public void info(String message) {
		printToLog(INFO, this.toString(), message, "[INFO]");
	}

	public void debug(String message) {
		printToLog(DEBUG, this.toString(), message, "[DEBUG]");
	}

	public void error(String message) {
		printToLog(ERROR, this.toString(), message, "[ERROR]");
	}
}
