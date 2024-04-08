package dk.theori.nestbox.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;


@Slf4j
public final class ImportUtil {

    /**
     * @param inputText
     * @return json for creating corresponding java Objects
     */
    public static String TSV2Json(String inputText) {
        log.debug("json encoding CSV formatted response");
        //replace inconvenient field name
        inputText = inputText.replace("Kasse nummer", "Kasse_nummer");
        String lines[] = inputText.split("\r?\n");
        log.info("headers: \n {}", lines[0]);
        String headers[] = lines[0].split("\t");
        if (lines.length >= 3) {
            log.info("first line: \t {}", lines[1]);
            log.info("second line: \t {}", lines[2]);
        }
        StringBuilder jSonObject = new StringBuilder();
        jSonObject.append("[");
        for (int j = 1; j < lines.length; j++) {
            String[] values = lines[j].split("\t");
            if (values.length > 0) { //else empty linge
                jSonObject.append("{");
                for (int i = 0; i < headers.length; i++) {
                    if (!values[i].isEmpty()) {
                        jSonObject.append("\"");
                        jSonObject.append(headers[i].toLowerCase(new Locale("Da", "DK")));
                        jSonObject.append("\":\"");
                        jSonObject.append(values[i]);
                        jSonObject.append("\"");
                        if (i < headers.length - 1) {
                            jSonObject.append(",");
                        }
                    }
                }
                jSonObject.append("}");

                if (j < lines.length - 1) {
                    jSonObject.append(",");
                }
            }

        }
        jSonObject.append("]");
        return jSonObject.toString();
    }

}
