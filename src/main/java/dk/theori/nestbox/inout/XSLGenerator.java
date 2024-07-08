package dk.theori.nestbox.inout;

import dk.theori.nestbox.entities.NestBox;
import dk.theori.nestbox.entities.NestBoxPropertyCheckList;
import dk.theori.nestbox.entities.NestBoxProperties;
import dk.theori.nestbox.entities.NestBoxRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class XSLGenerator {

    public static ByteArrayOutputStream generateXSLCheckList(NestBoxPropertyCheckList checkList, Integer beforeInDays) {
        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //sheet names and titles
            String toCheckTitle = String.format("Tjekkes inden %d dage", beforeInDays);
            String notCheckedTitle =  "Ikke tjekket";
            String recentlyCheckedTitle = "Tjekket for nyligt";
            //create sheets
            Sheet skalTjekkes = workbook.createSheet(toCheckTitle);
            Sheet ikkeTjekket = workbook.createSheet(notCheckedTitle);
            Sheet tjekket = workbook.createSheet(recentlyCheckedTitle);
            //generate content on each sheet
            generateSheetContent(skalTjekkes, checkList.getBoxesForChecking(), toCheckTitle);
            generateSheetContent(ikkeTjekket, checkList.getBoxesNotChecked(), notCheckedTitle);
            generateSheetContent(tjekket, checkList.getBoxesChecked(),recentlyCheckedTitle);
            workbook.write(outputStream);
            return outputStream;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return new ByteArrayOutputStream();
        }

    }


    private static void generateSheetContent(Sheet wbSheet, List<NestBoxProperties> boxes, String sheetTitle){

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

    public static ByteArrayOutputStream generateXSLRecords(List<NestBox> boxes){
        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String sheetTitle = "Udførte Redekassetjek";
            Sheet wbSheet = workbook.createSheet();
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
            // Create header row
            Row headerRow = wbSheet.createRow(0);
            String[] headers = {
                    "Zone",
                    "Kasse nummer",
                    "Tidsstempel",
                    "Status",
                    "Dato",
                    "Art",
                    "Æg",
                    "Unger",
                    "Ringe",
                    "Bemærkninger",
            };
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Populate data rows
            for (NestBox obj : boxes) {

                List<NestBoxRecord> records = obj.getRecords();
                String name = obj.getBoxId() + (obj.getOrientation() == null? "": obj.getOrientation());
                //create a box row which is a group header if there are more
                Row boxRow = wbSheet.createRow(rowNum++);
                boxRow.createCell(0).setCellValue(obj.getZone());
                boxRow.createCell(1).setCellValue(name);

                if(records.size() == 1){
                    fillRecordRow(records.get(0),2,boxRow, dateTimeFormatter, dateFormatter);
                }
                else
                {
                    for(NestBoxRecord r : records){
                        Row recRow = wbSheet.createRow(rowNum++);
                        fillRecordRow(r,2,recRow, dateTimeFormatter, dateFormatter);
                    }
                }


            }
            workbook.write(outputStream);
            // Write to OutputStream
            return outputStream;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return new ByteArrayOutputStream();
        }
    }

    private static int fillRecordRow(NestBoxRecord r, int colIdx, Row recRow, DateTimeFormatter formatter, DateTimeFormatter dateFormatter){
        int idx = colIdx;
        String dateTimeFormat = r.getDatetime().format(formatter);
        recRow.createCell(idx++).setCellValue(dateTimeFormat);
        recRow.createCell(idx++).setCellValue(r.getStatus().getStatusName());
        String dateFormat = r.getRecorddate().format(dateFormatter);
        recRow.createCell(idx++).setCellValue(dateFormat);
        if(r.getNesting() != null){
            recRow.createCell(idx++).setCellValue(r.getNesting().getSpecies());
            recRow.createCell(idx++).setCellValue(r.getNesting().getEggs() == null ? 0 : r.getNesting().getEggs());
            recRow.createCell(idx++).setCellValue(r.getNesting().getChicks() == null ? 0 : r.getNesting().getChicks());
        }
        else {
            idx = idx+3;
        }
        String rings = r.getRings() == null ? "" : String.join("-",r.getRings());
        recRow.createCell(idx++).setCellValue(rings);
        recRow.createCell(idx++).setCellValue(r.getComment());
        return idx;
    }

}
