package Helpers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public final class PDFHelper {

    public static void checkPdfContains(String fileName, String expectedContent) throws IOException {
        var pdfContent = getPdfContent(fileName);
        assertThat(pdfContent).contains(expectedContent);
    }

    public static void checkPdfDoesNotContain(String fileName, String notExpectedContent) throws IOException {
        var pdfContent = getPdfContent(fileName);
        assertThat(pdfContent).doesNotContain(notExpectedContent);
    }

    public static void checkPdfContainsValueNumberOfTimes(String fileName, String valueToFind, Integer expectedNumberOfTimes) throws IOException {
        var pdfContent = getPdfContent(fileName);
        int actualAmount = pdfContent.split(valueToFind, -1).length - 1;
        assertThat(actualAmount)
                .withFailMessage(String.format("Expected '%s' to contain '%s' value '%s' times but found '%s'", pdfContent, valueToFind, expectedNumberOfTimes, actualAmount))
                .isEqualTo(expectedNumberOfTimes);
    }

    public static void checkPdfNumberOfPages(String fileName, Integer expectedNumberOfPages) throws IOException {
        assertThat(getPageCount(fileName)).isEqualTo(expectedNumberOfPages);
    }

    private static PDDocument openPdf(String filePath) throws IOException {
        return PDDocument.load(new File(filePath));
    }

    private static String getPdfContent(String fileName) throws IOException {
        PDDocument doc = openPdf(fileName);
        String content = new PDFTextStripper().getText(doc);
        doc.close();
        return content;
    }

    private static int getPageCount(String fileName) throws IOException {
        PDDocument doc = openPdf(fileName);
        doc.close();
        return doc.getNumberOfPages();
    }}
