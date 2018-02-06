package ca.manitoulin.mtd.web.utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.apache.commons.collections.MapUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class QuoteGenerator {
    public static final float TITLE_FONT_SIZE = 14;
    public static final float CELL_FONT_SIZE = 10;
    public static final float HEADER_FONT_SIZE = 10;
    public static final float HEADER_BLOCK_FONT_SIZE = 12;
    public static final Font TITLE_FONT = new Font(Font.HELVETICA, TITLE_FONT_SIZE, Font.BOLD);
    public static final Font CELL_FONT = new Font(Font.HELVETICA, CELL_FONT_SIZE, Font.NORMAL);
    public static final Font HEADER_FONT = new Font(Font.HELVETICA, HEADER_FONT_SIZE, Font.BOLD);
    public static final Font HEADER_BLOCK_FONT = new Font(Font.HELVETICA, HEADER_BLOCK_FONT_SIZE, Font.NORMAL);
    public static final Font HEADER_BLOCK_FONT_LABEL = new Font(Font.HELVETICA, HEADER_BLOCK_FONT_SIZE, Font.BOLD);
    public static final float CELL_PADDING = 3;
    public static final float SPACE_BEFORE_TITLE = 0;
    public static final float SPACE_BEFORE_LOGO =  5;
    public static final float SPACE_BEFORE_TABLE = 0;
    public static final float DOCUMENT_MARGIN = 5;

    public static void generate(File file, Map<String, String> headers, Map<String, Field> fields, List<Map<String, Object>> freightList) throws DocumentException, MalformedURLException, IOException {
        System.out.println("Generating pdf file");

        Document document = new Document(PageSize.LETTER);
        OutputStream os = new FileOutputStream(file);
        PdfWriter pdfWriter = PdfWriter.getInstance(document, os);

        document.open();
        PdfContentByte cb = pdfWriter.getDirectContent();

        // Load existing no signature pdf template
        InputStream inputStream = new FileInputStream(Thread.currentThread().getContextClassLoader().getResource("quotation_no_signature_template.pdf").getFile());
        PdfReader pdfReader = new PdfReader(inputStream);
        PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, 1);

        // Load existing signature pdf template
        InputStream finalInputStream = new FileInputStream(Thread.currentThread().getContextClassLoader().getResource("quotation_signature_template.pdf").getFile());
        PdfReader finalPdfReader = new PdfReader(finalInputStream);
        PdfImportedPage finalPage = pdfWriter.getImportedPage(finalPdfReader, 1);

        // Add table
        PdfPTable quotationTable = new PdfPTable(new float[]{25, 75});
        quotationTable.setWidthPercentage(100);
        quotationTable.setTotalWidth( PageSize.LETTER.getWidth() - 50);
        quotationTable.setSpacingBefore(SPACE_BEFORE_TABLE);
        quotationTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        /*
        // Add title
        Phrase title = new Phrase(typeDesp, TITLE_FONT);
        PdfPCell titleCell = new PdfPCell(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setColspan(2);
        titleCell.setPadding(CELL_PADDING);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        quotationTable.addCell(titleCell);
        row += 1;
        */
        int startRow = 0;
        int row = 0;
        float previousHeight = 0f;

        int x_offset = 20;
        // Add fields
        for (Field field : fields.values()) {
            float heightBeforeAdd = quotationTable.getTotalHeight();

            // only append ":" if label is not blank
            StringBuilder labeltext = new StringBuilder(field.getLabel());
            if (labeltext.length() >= 1) {
                labeltext.append(":");
            }

            Phrase label = new Phrase(labeltext.toString(), HEADER_FONT);
            PdfPCell labelCell = new PdfPCell(label);
            labelCell.setBorder(Rectangle.NO_BORDER);
            labelCell.setPadding(CELL_PADDING);
            quotationTable.addCell(labelCell);

            if( labeltext.toString().equals("Cargo Details:" ) ){
                // adding freight grid
                PdfPTable freightTable = new PdfPTable(4);
                freightTable.setSplitLate(false);

                freightTable.addCell(  addTitleCell( "Desc Goods" )  );
                freightTable.addCell(  addTitleCell( "Dims") );
                freightTable.addCell(  addTitleCell( "Pieces") );
                freightTable.addCell(  addTitleCell( "Weight")  );
                for (Map<String, Object> freight : freightList) {
                    freightTable.addCell(addNormalCell(String.valueOf(freight.get("item"))));
                    freightTable.addCell(addNormalCell(String.valueOf(freight.get("estimatedDimension"))));
                    if (freight.get("estimatedPieces") != null)
                        freightTable.addCell(  addNormalCell(String.valueOf(freight.get("estimatedPieces"))));
                    else
                        freightTable.addCell(  addNormalCell(""));

                    String weight = "";
                    if (freight.get("estimatedWeight") != null)
                        weight = String.valueOf(freight.get("estimatedWeight"));
                    if (freight.get("estimatedUOM") != null)
                        weight = weight + " " + String.valueOf(freight.get("estimatedUOM"));
                    freightTable.addCell(  addNormalCell(weight));
                }

                quotationTable.addCell( freightTable );
            } else {
                PdfPCell valueCell = new PdfPCell(field.getValue());
                valueCell.setBorder(Rectangle.NO_BORDER);
                valueCell.setPadding(CELL_PADDING);
                quotationTable.addCell(valueCell);
                row += 1;
            }

            // Break to a new page
            if (quotationTable.getTotalHeight() - previousHeight > 560) {
                previousHeight = heightBeforeAdd;
                // Copy first page of existing PDF into output PDF
                document.newPage();
                cb.addTemplate(page, 0, 0);
                quotationTable.writeSelectedRows(startRow, row, x_offset, 640, cb);
                startRow = row;
            }
        }

        // Writing out the rest to the last page
        float lastPageHeight = quotationTable.getTotalHeight() - previousHeight;
        System.out.println("Height of last page: " + String.valueOf(lastPageHeight));
        if (lastPageHeight > 525) {
            document.newPage();
            cb.addTemplate(page, 0, 0);
            quotationTable.writeSelectedRows(startRow, row, x_offset, 640, cb);

            document.newPage();
            cb.addTemplate(finalPage, 0, 0);
            quotationTable.writeSelectedRows(row, row+1, x_offset, 640, cb);
        } else {
            document.newPage();
            cb.addTemplate(finalPage, 0, 0);
            quotationTable.writeSelectedRows(startRow, row+1, x_offset, 640, cb);
        }

        document.close();
        addHeaders(file, headers);
        System.out.println("Done generating pdf file");
    }

    private static void addHeaders(File file, Map<String, String> headers) throws IOException, DocumentException {
        InputStream inputStream = new FileInputStream(file);
        PdfReader pdfReader = new PdfReader(inputStream);
        OutputStream os = new FileOutputStream(file);

        PdfStamper pdfStamper = new PdfStamper(pdfReader, os);

        String quoteNo = MapUtils.getString(headers, "quoteNo", "");
        String version = MapUtils.getString(headers, "version", "1.0");
        String validUntil = MapUtils.getString(headers, "validUntil", "");
        String quoteType = MapUtils.getString(headers, "quoteType", "");

        int n = pdfReader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            //header
            float Y = 760;
            float X = 0;

            PdfContentByte cb = pdfStamper.getOverContent(i);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(quoteNo, HEADER_FONT), 60, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(version, HEADER_FONT), 207, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(validUntil, HEADER_FONT), 330, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(String.valueOf(i), HEADER_FONT), 511, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(String.valueOf(n), HEADER_FONT), 531, Y, 0);

            //footer

            Y = 18;
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(quoteNo, HEADER_FONT), 60, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(version, HEADER_FONT), 207, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(validUntil, HEADER_FONT), 330, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(String.valueOf(i), HEADER_FONT), 511, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(String.valueOf(n), HEADER_FONT), 531, Y, 0);

            // title block beside logo
            //
            Y = 726;
            X = 285;
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(quoteType + " Quote#:", HEADER_BLOCK_FONT_LABEL), X, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(quoteNo, HEADER_BLOCK_FONT), X+182, Y, 0);
            Y = Y - 19;
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Version:", HEADER_BLOCK_FONT_LABEL), X, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(version, HEADER_BLOCK_FONT), X+182, Y, 0);
            Y = Y - 19;
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Valid Until:", HEADER_BLOCK_FONT_LABEL), X, Y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(validUntil, HEADER_BLOCK_FONT), X+182, Y, 0);
        }
        pdfStamper.close();
    }

    public static Map<String, String> initHeaders(String quoteNum, String version, String expireDate, String quoteType) {
        Map<String, String> headers = newLinkedHashMap();

        headers.put("quoteNo", trimToEmpty(quoteNum));
        headers.put("version", version);
        headers.put("validUntil", trimToEmpty(expireDate));
        headers.put("quoteType", trimToEmpty(quoteType));

        return headers;
    }

    public static Map<String, Field> initFields(String readTime, String pickupCity, String destination, String cost, String duties, String schedule, List<Map<String, Object>> termList) {
        Map<String, Field> fields = newLinkedHashMap();

        //fields.put("readyTime", new Field("Ready Date/Time", new Phrase(readTime, QuoteGenerator.CELL_FONT)));
        //fields.put("pickupCity", new Field("Pickup City", new Phrase(pickupCity, QuoteGenerator.CELL_FONT)));
        //fields.put("destination", new Field("Destination/Delivery", new Phrase(destination, QuoteGenerator.CELL_FONT)));
        //fields.put("cost", new Field("Cost", new Phrase(cost, QuoteGenerator.CELL_FONT)));
        // FIELDS.put("cargo", new Field("Cargo Details", new Phrase("1 /5 LBS 6x6x10” Car parts", QuoteGenerator.CELL_FONT)));

        fields.put("readyTime", new Field("Ready Date/Time", new Phrase(readTime, QuoteGenerator.CELL_FONT)));
        fields.put("pickupCity", new Field("Pickup City", new Phrase(pickupCity, QuoteGenerator.CELL_FONT)));
        fields.put("destination", new Field("Destination/Delivery", new Phrase(destination, QuoteGenerator.CELL_FONT)));
        fields.put("cost", new Field("Hand Carry Cost", new Phrase(cost, QuoteGenerator.CELL_FONT)));
        fields.put("cargo", new Field("Cargo Details", new Phrase("1 /5 LBS 6x6x10” Car parts", QuoteGenerator.CELL_FONT)));

        // make each item in termList an individual phrase to break on phrase in place of block
        int i=0;
        if (null != termList && 0 != termList.size()) {
            for (Map<String, Object> map : termList) {
                StringBuilder key = new StringBuilder("terms");
                StringBuilder label = new StringBuilder();
                if (i == 0) {
                    label.append("Terms");
                }
                key.append(i++);
                String description = (String) map.get("desc");
                fields.put(key.toString(), new Field(label.toString(), new Phrase(description, QuoteGenerator.CELL_FONT)));
            }
        }

        fields.put("duties", new Field("Duties & Taxes", new Phrase(duties, QuoteGenerator.CELL_FONT)));
        fields.put("flightSchedule", new Field("Flight Schedule", new Phrase(schedule, QuoteGenerator.CELL_FONT)));
        return fields;
    }

    private static PdfPCell addTitleCell( String title ){
        PdfPCell cell = new PdfPCell(new Phrase(title.toString(), CELL_FONT ) );
        cell.setBorder(Rectangle.BOX);
        return cell;
    }
    private static PdfPCell addNormalCell( String title ){
        PdfPCell cell = new PdfPCell(new Phrase(title.toString(), CELL_FONT ) );
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
