package com.userservice.pdffile;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class PDF {
        public static ByteArrayOutputStream generatePdfStream() throws DocumentException {
            /**
             *
             * Map.put("Firstname",
             * list.add(
             *
             */
            Map<String, List<Object>> queryResults= new HashMap<>();
            List<String> firstnames= List.of("Lunga","Wanga","Bonile","Sisa");
            List<String> lastnames= List.of("Tsewu","Tsewu","Tsewu","Nqiwa");
            queryResults.put("Firstname", Collections.singletonList(firstnames));
            queryResults.put("Lastname",Collections.singletonList(lastnames));

            //create and open document
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Write column names
            for (String column : queryResults.keySet()) {
                Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                Paragraph paragraph = new Paragraph(column, boldFont);
                document.add(paragraph);
            }
            document.add(new Paragraph("\n"));
            // Write data rows
            for (var row : queryResults.keySet()) {
                for (Object value : queryResults.get(row)) {
                    Paragraph paragraph = new Paragraph(value.toString());
                    document.add(paragraph);
                }
                document.add(new Paragraph("\n"));
            }
            document.close();
            return outputStream;
        }
}

