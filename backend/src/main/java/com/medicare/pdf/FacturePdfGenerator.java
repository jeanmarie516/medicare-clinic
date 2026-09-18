package com.medicare.pdf;

import com.medicare.entity.Facture;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class FacturePdfGenerator {

    @Value("${pdf.storage.path}")
    private String pdfStoragePath;

    public String generateFacture(Facture facture) throws Exception {
        Path storagePath = Path.of(pdfStoragePath, "factures");
        Files.createDirectories(storagePath);

        String fileName = "facture_" + facture.getNumeroFacture() + "_" +
                System.currentTimeMillis() + ".pdf";
        String filePath = storagePath.resolve(fileName).toString();
        String relativePath = "factures/" + fileName;

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);

        try (FileOutputStream fos = new FileOutputStream(filePath);
             PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fos))) {

            Document document = new Document(pdfDoc);

            // En-tête
            document.add(new Paragraph("CLINIQUE MEDICARE")
                    .setBold()
                    .setFontSize(22)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("123 Avenue de la Santé, Dakar - Sénégal")
                    .setFontSize(10)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("Tél: +221 33 123 45 67 | Email: contact@medicare.sn")
                    .setFontSize(10)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph(""));

            // Titre facture
            document.add(new Paragraph("FACTURE")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("N° " + facture.getNumeroFacture())
                    .setFontSize(14)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph(""));

            // Informations
            document.add(new Paragraph("Date d'émission: " +
                    facture.getDateFacture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new Paragraph("Date d'échéance: " +
                    facture.getDateEcheance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new Paragraph(""));

            // Patient
            document.add(new Paragraph("FACTURÉ À:")
                    .setBold().setFontSize(12));
            document.add(new Paragraph(facture.getPatient().getPrenom() + " " +
                    facture.getPatient().getNom()));
            if (facture.getPatient().getAdresse() != null) {
                document.add(new Paragraph(facture.getPatient().getAdresse()));
            }
            document.add(new Paragraph(""));

            // Tableau des lignes
            Table table = new Table(UnitValue.createPercentArray(new float[]{50, 15, 20, 15}));
            table.setWidth(UnitValue.createPercentValue(100));

            // En-têtes du tableau
            table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantité").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Prix unitaire").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Montant").setBold()));

            // Lignes de facturation (simplifié)
            String items = facture.getLigneItemsJson();
            if (items != null && !items.isEmpty()) {
                table.addCell(new Cell().add(new Paragraph("Consultation médicale")));
                table.addCell(new Cell().add(new Paragraph("1")));
                table.addCell(new Cell().add(new Paragraph(
                        format.format(facture.getMontantTotal().doubleValue() / 1.18))));
                table.addCell(new Cell().add(new Paragraph(
                        format.format(facture.getMontantTotal().doubleValue() / 1.18))));
            }

            document.add(table);
            document.add(new Paragraph(""));

            // Totaux
            if (facture.getTva() != null && facture.getTva().compareTo(java.math.BigDecimal.ZERO) > 0) {
                document.add(new Paragraph("TVA (18%): " + format.format(facture.getTva()))
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            }
            document.add(new Paragraph("TOTAL: " + format.format(facture.getMontantTotal()))
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            document.add(new Paragraph("Montant payé: " + format.format(facture.getMontantPaye()))
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            document.add(new Paragraph("Reste à payer: " +
                    format.format(facture.getMontantTotal().subtract(facture.getMontantPaye())))
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            document.add(new Paragraph(""));

            // Statut
            document.add(new Paragraph("Statut: " + facture.getStatutPaiement().name())
                    .setBold().setFontSize(12));
            document.add(new Paragraph(""));

            if (facture.getNotes() != null && !facture.getNotes().isEmpty()) {
                document.add(new Paragraph("Notes: " + facture.getNotes()).setFontSize(10));
                document.add(new Paragraph(""));
            }

            // Conditions
            document.add(new Paragraph("CONDITIONS DE PAIEMENT")
                    .setBold().setFontSize(10));
            document.add(new Paragraph("Paiement à réception de facture.")
                    .setFontSize(10));
            document.add(new Paragraph("Mode de paiement accepté: Espèces, Orange Money, Wave, Carte bancaire")
                    .setFontSize(10));
            document.add(new Paragraph(""));

            // Pied de page
            document.add(new Paragraph("Merci de votre confiance.")
                    .setFontSize(10)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("CLINIQUE MEDICARE - Votre santé, notre priorité")
                    .setFontSize(8)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            document.close();
        }

        return relativePath;
    }
}
