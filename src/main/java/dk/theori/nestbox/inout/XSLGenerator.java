package dk.theori.nestbox.inout;

import dk.theori.nestbox.entities.NestBox;
import dk.theori.nestbox.entities.NestBoxCheckList;
import dk.theori.nestbox.entities.NestBoxProperties;
import dk.theori.nestbox.entities.NestBoxRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.OutputStream;
import java.util.List;

@Slf4j
public class XSLGenerator {

    public static Workbook generateXSLCheckList(NestBoxCheckList checkList, Integer beforeInDays) {
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet1 = workbook.createSheet();
            Row headerRow = sheet1.createRow(0);
            headerRow.setHeight(Short.parseShort("15"));
            CellStyle cellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerRow.setRowStyle(cellStyle);
            cellStyle.setFont(headerFont);
            headerRow.createCell(0).setCellValue(String.format("Redekassetjek inden %d dage", beforeInDays));

            // Create header row
            Row headerBoxesForChecking = sheet1.createRow(1);
            String[] headers = {
                    "Kasse nummer",
                    "Højde",
                    "Zone",
            };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerBoxesForChecking.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Populate data rows
            int rowNum = 1;
            for (NestBoxProperties obj : checkList.getBoxesForChecking()) {
                Row row = sheet1.createRow(rowNum++);
                String name = obj.getBoxId() + (obj.getOrientation().isEmpty()? "" : obj.getOrientation());
                String alt = obj.getAltitude() == 1 ? "Lav" : "Stige";
                String zone = obj.getZone();

                row.createCell(0).setCellValue(name);
                row.createCell(1).setCellValue(alt);
                row.createCell(2).setCellValue(zone);
            }
            return workbook;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    public static Workbook generateXSLRecords(List<NestBox> boxes){
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet1 = workbook.createSheet("Redekasse checks");

            // Create header row
            Row headerRow = sheet1.createRow(0);
            String[] headers = {
                    "Kasse nummer",
                    "Zone",
                    "Tidsstempel",
                    "Status",
                    //"Dato",
                    "Bemærkninger",
                    "Art",
                    "Unger",
                    "Æg",
                    "Ringe"
            };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Populate data rows
            int rowNum = 1;
            for (NestBox obj : boxes) {
                Row row = sheet1.createRow(rowNum++);
                List<NestBoxRecord> records = obj.getRecords();
                String name = obj.getBoxId() + (obj.getOrientation().isEmpty()? "" : obj.getOrientation());
                row.createCell(0).setCellValue(obj.getBoxId());
                row.createCell(1).setCellValue(obj.getZone());
                if(records.size()>1) //go to next row
                {
                    row = sheet1.createRow(rowNum++);
                    row.createCell(0).setCellValue("");
                    row.createCell(1).setCellValue("");
                }
                for(NestBoxRecord r : records){
                    row.createCell(3).setCellValue(r.getDatetime());
                    row.createCell(4).setCellValue(r.getStatus().getStatusName());
                    row.createCell(5).setCellValue(r.getComment());
                    if(r.getNesting() != null){
                        row.createCell(6).setCellValue(r.getNesting().getSpecies());
                        row.createCell(7).setCellValue(r.getNesting().getEggs());
                        row.createCell(8).setCellValue(r.getNesting().getChicks());
                        String rings = String.join("-",r.getRings());
                        row.createCell(9).setCellValue(rings);
                    }
                }

            }

            // Write to OutputStream
            return workbook;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }
}
