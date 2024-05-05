package dk.theori.nestbox.inout;

import dk.theori.nestbox.entities.NestBox;
import dk.theori.nestbox.entities.NestBoxCheckList;
import dk.theori.nestbox.entities.NestBoxProperties;
import dk.theori.nestbox.entities.NestBoxRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
public class XSLGenerator {

    public static ByteArrayOutputStream generateXSLCheckList(NestBoxCheckList checkList, Integer beforeInDays) {
        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Sheet skalTjekkes = workbook.createSheet(String.format("Tjekkes inden %d dage", beforeInDays));
            Sheet ikkeTjekket = workbook.createSheet("Ikke tjekket");
            Sheet tjekket = workbook.createSheet("Tjekket for nyligt");
            generateSheet(skalTjekkes, checkList.getBoxesForChecking(), String.format("Tjekkes inden %d dage", beforeInDays));
            generateSheet(ikkeTjekket, checkList.getBoxesNotChecked(), "Ikke tjekket");
            generateSheet(tjekket, checkList.getBoxesChecked(),"Tjekket for nyligt");
            workbook.write(outputStream);
            return outputStream;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return new ByteArrayOutputStream();
        }

    }


    private static void generateSheet(Sheet wbSheet, List<NestBoxProperties> boxes, String sheetTitle){

        // Create header row

        int rowNum = 0;
        Row titleRow = wbSheet.createRow(rowNum);
        titleRow.setHeight (Short.parseShort(String.valueOf(20*15)));
        CellStyle cellStyle = wbSheet.getWorkbook().createCellStyle();
        Font headerFont = wbSheet.getWorkbook().createFont();
        headerFont.setBold(true);
        cellStyle.setFont(headerFont);
        titleRow.setRowStyle(cellStyle);
        titleRow.createCell(0).setCellValue(sheetTitle);
        rowNum++;

        String[] headers = {
                "Kasse nummer",
                "Højde",
                "Zone",
        };
        Row headerRow = wbSheet.createRow(rowNum);
        headerRow.setHeight (Short.parseShort(String.valueOf(20*15)));
        headerRow.setRowStyle(cellStyle);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Populate data rows
        for (NestBoxProperties obj : boxes) {
            rowNum++;
            Row row = wbSheet.createRow(rowNum);
            String name = obj.getBoxId() + (obj.getOrientation() == null ? "" : obj.getOrientation());
            String alt = obj.getAltitude() == 1 ? "Lav" : "Stige";
            String zone = obj.getZone();

            row.createCell(0).setCellValue(name);
            row.createCell(1).setCellValue(alt);
            row.createCell(2).setCellValue(zone);
        }
    }

}
