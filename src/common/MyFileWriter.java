package common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class MyFileWriter {

	public static void writeFile(String folder, String filename, String data) {
		FileWriter fileWriter = null;
		// String filename = withTransfer ? "pngValueFunction_transfer" : "pngValueFunction";
		try {
			fileWriter = new FileWriter(Paths.get(folder).resolve(filename).toFile());
			fileWriter.append(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeExcelFile(String folder, String filename, String[] headings, List<ResultData> dataList) {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("sheet1");

		int rowCount = 0;

		Row row = sheet.createRow(rowCount++);
		for (int i = 0; i < headings.length; i++) {
			row.createCell(i).setCellValue(headings[i]);
		}

		for (ResultData data : dataList) {
			row = sheet.createRow(rowCount++);

			row.createCell(0).setCellValue(data.getEpisode());
			row.createCell(1).setCellValue(data.getSteps());
			row.createCell(2).setCellValue(data.getReward());
		}

		writeWorkbook(folder, filename, wb);
	}

	public static void writeExcelValueFunction(String folder, String filename, double[][] data) {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("sheet1");

		int rowCount = 0;
		for (int y = data[0].length - 1; y >= 0; y--) {
			Row row = sheet.createRow(rowCount++);
			for (int x = 0; x < data.length; x++) {
				row.createCell(x).setCellValue(data[x][y]);
			}
		}

		writeWorkbook(folder, filename, wb);
	}

	private static void writeWorkbook(String folder, String filename, Workbook wb) {
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(Paths.get(folder).resolve(filename + ".xls").toFile());
			wb.write(fileOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOut.close();
				wb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
