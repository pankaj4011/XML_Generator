package xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Generator {
	private static int rowCounter = 0;
	private static int columnCounter = 0;
	private static List<String> tagData = new ArrayList<String>();
	private static List<String> columnNames = new ArrayList<String>();
	private static int dataCounter = 0;

	public static void main(String[] args) {
		// Reading the data from the excel file
		getXMLInformation();
		// generating the xml's from the input data
		generateXML();
	}

	public static void getXMLInformation() {
		String fileName = null;
		String filePath = null;
		try {

			filePath = Initializer.getInstance().getProperty("Input_Data_File_Path");
			fileName = Initializer.getInstance().getProperty("Input_Data_File_Name");
			FileInputStream file = new FileInputStream(new File(filePath + "\\" + fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			// Getting the Column Headings
			Row firstRow = rowIterator.next();
			Iterator<Cell> firstCell = firstRow.cellIterator();

			while (firstCell.hasNext()) {
				columnCounter++;
				Cell cell = firstCell.next();
				columnNames.add(cell.getStringCellValue());
			}

			// getting the data from the rows
			while (rowIterator.hasNext()) {
				rowCounter++;
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {

					Cell cell = cellIterator.next();

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						tagData.add(String.valueOf((int) cell.getNumericCellValue()));
						break;
					case Cell.CELL_TYPE_STRING:
						tagData.add(String.valueOf((String) cell.getStringCellValue()));
						break;
					}

				}

			}
			file.close();
		} catch (IOException e) {
			System.err.println("Error reading the input excel file" + e.getLocalizedMessage());
		}
	}

	public static void generateXML() {
		String outputFilePath = null;
		String sampleFilePath = null;
		String inputFileName = null;
		String outputFileName = null;
		try {
			for (int i = 0; i < rowCounter; i++) {
				DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
				DocumentBuilder b = f.newDocumentBuilder();
				sampleFilePath = Initializer.getInstance().getProperty("Input_XML_File_Path");
				outputFilePath = Initializer.getInstance().getProperty("Output_File_Path");
				inputFileName = Initializer.getInstance().getProperty("Input_XML_File_Name");
				Document doc = b.parse(new File(sampleFilePath + "\\" + inputFileName));
				outputFileName = inputFileName + "_" + i + ".xml";

				for (int j = 0; j < columnCounter; j++) {
					Element element1 = (Element) doc.getElementsByTagName(columnNames.get(j)).item(0);

					String value = tagData.get(dataCounter++);
					element1.setTextContent(value);
				}
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(outputFilePath + "\\" + outputFileName);
				transformer.transform(source, result);
			}

		} catch (Exception e) {
			System.err.println("Error creating the xml file" + e.getLocalizedMessage());
		}

	}
}
