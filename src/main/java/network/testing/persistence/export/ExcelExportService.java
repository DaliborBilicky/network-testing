package network.testing.persistence.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import network.testing.app.ProjectContext;
import network.testing.core.utils.StatisticsUtils;
import network.testing.domain.model.dto.ExperimentSummary;
import network.testing.domain.model.result.SnapshotData;

public class ExcelExportService {

	private static final String[] SNAPSHOT_COLUMNS = {
			"k", "objective", "sum", "irregularity", "min", "max", "avg", "mode"
	};

	public static void exportExperiment(ExperimentSummary info, Map<Integer, List<SnapshotData>> data,
			ProjectContext context, Path target)
			throws IOException {

		try (Workbook workbook = new XSSFWorkbook()) {
			String safeName = prepareSheetName(info.name());
			Sheet sheet = workbook.createSheet(safeName);
			CellStyle headerStyle = createHeaderStyle(workbook);

			int currentRow = 0;

			currentRow = writeExtendedMetadata(sheet, currentRow, info, context);
			currentRow++;

			int maxSnapshots = data.values().stream().mapToInt(List::size).max().orElse(0);

			currentRow = writeDynamicHeader(sheet, currentRow, maxSnapshots, headerStyle);

			writeHorizontalResults(sheet, currentRow, data);

			finalizeSheet(sheet, maxSnapshots);
			saveToFile(workbook, target);
		}
	}

	private static int writeExtendedMetadata(Sheet sheet, int startRow, ExperimentSummary info,
			ProjectContext context) {
		int row = startRow;
		writeMetadataRow(sheet, row++, "Experiment Name", info.name());
		writeMetadataRow(sheet, row++, "Strategy Type", info.type());
		writeMetadataRow(sheet, row++, "Created At", info.date());
		writeMetadataRow(sheet, row++, "Base speed", String.valueOf(info.baseSpeed()));
		writeMetadataRow(sheet, row++, "kLim", String.format("%f", context.elongator().getKLim()));

		double graphIrr = StatisticsUtils.variance(context.network().copyVertexWeights());
		writeMetadataRow(sheet, row++, "graph irregularity", String.format("%f", graphIrr));

		return row;
	}

	private static void writeMetadataRow(Sheet sheet, int rowIdx, String label, String value) {
		Row row = sheet.createRow(rowIdx);
		row.createCell(0).setCellValue(label);
		row.createCell(1).setCellValue(value);
	}

	private static int writeDynamicHeader(Sheet sheet, int rowIdx, int maxSnapshots, CellStyle style) {
		Row headerRow = sheet.createRow(rowIdx);

		Cell pCell = headerRow.createCell(0);
		pCell.setCellValue("p");
		pCell.setCellStyle(style);

		for (int i = 0; i < maxSnapshots; i++) {
			int snapshotIndex = i + 1;
			for (int j = 0; j < SNAPSHOT_COLUMNS.length; j++) {
				int colIdx = 1 + (i * SNAPSHOT_COLUMNS.length) + j;
				Cell cell = headerRow.createCell(colIdx);
				cell.setCellValue(SNAPSHOT_COLUMNS[j] + snapshotIndex);
				cell.setCellStyle(style);
			}
		}
		return rowIdx + 1;
	}

	private static void writeHorizontalResults(Sheet sheet, int startRow, Map<Integer, List<SnapshotData>> data) {
		int currentRow = startRow;
		for (Map.Entry<Integer, List<SnapshotData>> entry : data.entrySet()) {
			Row row = sheet.createRow(currentRow++);

			row.createCell(0).setCellValue(entry.getKey());

			List<SnapshotData> snapshots = entry.getValue();
			for (int i = 0; i < snapshots.size(); i++) {
				SnapshotData snap = snapshots.get(i);
				int baseCol = 1 + (i * SNAPSHOT_COLUMNS.length);

				row.createCell(baseCol + 0).setCellValue(snap.k());
				row.createCell(baseCol + 1).setCellValue(snap.objective());
				row.createCell(baseCol + 2).setCellValue(snap.medianSum());
				row.createCell(baseCol + 3).setCellValue(snap.medianIrregularity());
				row.createCell(baseCol + 4).setCellValue(snap.declineMin());
				row.createCell(baseCol + 5).setCellValue(snap.declineMax());
				row.createCell(baseCol + 6).setCellValue(snap.declineAvg());
				row.createCell(baseCol + 7).setCellValue(snap.declineMode());
			}
		}
	}

	private static String prepareSheetName(String name) {
		return name.replaceAll("[\\\\/:*?\"<>|]", "_");
	}

	private static CellStyle createHeaderStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		style.setFont(font);
		style.setBorderBottom(BorderStyle.THIN);
		return style;
	}

	private static void finalizeSheet(Sheet sheet, int maxSnapshots) {
		int totalCols = 1 + (maxSnapshots * SNAPSHOT_COLUMNS.length);
		for (int i = 0; i < totalCols; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private static void saveToFile(Workbook workbook, Path target) throws IOException {
		try (FileOutputStream fileOut = new FileOutputStream(target.toFile())) {
			workbook.write(fileOut);
		}
	}
}
