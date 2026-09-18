package com.medicare.pdf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.medicare.entity.Prescription;
import com.medicare.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@Component
public class PrescriptionPdfGenerator {

    private final PrescriptionRepository prescriptionRepository;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_LONG_FMT = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final DeviceRgb PRIMARY = new DeviceRgb(99, 102, 241);
    private static final DeviceRgb PRIMARY_LIGHT = new DeviceRgb(238, 242, 255);
    private static final DeviceRgb GREEN = new DeviceRgb(34, 197, 94);
    private static final DeviceRgb GREEN_LIGHT = new DeviceRgb(236, 253, 245);
    private static final DeviceRgb RED = new DeviceRgb(239, 68, 68);
    private static final DeviceRgb RED_LIGHT = new DeviceRgb(254, 242, 242);
    private static final DeviceRgb ORANGE = new DeviceRgb(249, 115, 22);
    private static final DeviceRgb ORANGE_LIGHT = new DeviceRgb(255, 247, 237);
    private static final DeviceRgb AMBER = new DeviceRgb(245, 158, 11);
    private static final DeviceRgb AMBER_LIGHT = new DeviceRgb(255, 251, 235);
    private static final DeviceRgb BLUE_LIGHT = new DeviceRgb(239, 246, 255);
    private static final DeviceRgb BLUE = new DeviceRgb(59, 130, 246);
    private static final DeviceRgb GRAY = new DeviceRgb(107, 114, 128);
    private static final DeviceRgb GRAY_LIGHT = new DeviceRgb(243, 244, 246);
    private static final DeviceRgb DARK = new DeviceRgb(17, 24, 39);
    private static final DeviceRgb WHITE = new DeviceRgb(255, 255, 255);

    public PrescriptionPdfGenerator(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Value("${pdf.storage.path}")
    private String pdfStoragePath;

    public String generatePrescription(Prescription prescription) throws Exception {
        Path storagePath = Path.of(pdfStoragePath, "prescriptions");
        Files.createDirectories(storagePath);

        String fileName = "prescription_" + prescription.getId() + "_" +
                System.currentTimeMillis() + ".pdf";
        String filePath = storagePath.resolve(fileName).toString();
        String relativePath = "prescriptions/" + fileName;

        try (FileOutputStream fos = new FileOutputStream(filePath);
             PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fos))) {

            Document document = new Document(pdfDoc);
            document.setMargins(30, 30, 30, 30);
            float pageWidth = pdfDoc.getDefaultPageSize().getWidth() - 60;

            // ===== EN-TÊTE =====
            Table header = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                    .useAllAvailableWidth();
            header.addCell(new Cell()
                    .add(new Paragraph("CLINIQUE MEDICARE")
                            .setBold().setFontSize(22).setFontColor(PRIMARY))
                    .setBorder(Border.NO_BORDER).setPaddingBottom(2));
            header.addCell(new Cell()
                    .add(new Paragraph("ORDONNANCE MÉDICALE")
                            .setBold().setFontSize(11).setFontColor(GRAY)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .setBorder(Border.NO_BORDER).setPaddingBottom(2));
            document.add(header);

            document.add(new LineSeparator(new SolidLine(2))
                    .setMarginBottom(10).setMarginTop(5));

            // ===== MÉDECIN =====
            Table doctorTable = new Table(UnitValue.createPercentArray(new float[]{100}))
                    .useAllAvailableWidth();
            doctorTable.addCell(createColoredBox(
                    "MÉDECIN PRESCRIPTEUR",
                    "Dr. " + prescription.getMedecin().getUser().getPrenom() + " " +
                            prescription.getMedecin().getUser().getNom(),
                    prescription.getMedecin().getSpecialite() != null ?
                            "Spécialité : " + prescription.getMedecin().getSpecialite() : null,
                    "N° Ordre : " + (prescription.getMedecin().getNumeroOrdre() != null ?
                            prescription.getMedecin().getNumeroOrdre() : "N/A"),
                    GREEN_LIGHT, GREEN, pageWidth
            ));
            document.add(doctorTable);
            document.add(new Paragraph(""));

            // ===== PATIENT =====
            var patient = prescription.getPatient();
            String sexeLabel = patient.getSexe() != null ?
                    (patient.getSexe().equals("M") ? "Masculin" : "Féminin") : null;

            Table patientTable = new Table(UnitValue.createPercentArray(new float[]{100}))
                    .useAllAvailableWidth();
            patientTable.addCell(createColoredBox(
                    "PATIENT",
                    patient.getPrenom() + " " + patient.getNom(),
                    "Date de naissance : " + patient.getDateNaissance().format(DATE_FMT) +
                            (sexeLabel != null ? "  •  Sexe : " + sexeLabel : ""),
                    buildPatientDetails(patient),
                    PRIMARY_LIGHT, PRIMARY, pageWidth
            ));
            document.add(patientTable);
            document.add(new Paragraph(""));

            // ===== DATES =====
            Table datesTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .useAllAvailableWidth();
            datesTable.addCell(createInfoCell("Date de prescription",
                    prescription.getDatePrescription().format(DATE_LONG_FMT), GRAY_LIGHT));
            if (prescription.getValideJusqua() != null) {
                datesTable.addCell(createInfoCell("Valide jusqu'au",
                        prescription.getValideJusqua().format(DATE_LONG_FMT), GRAY_LIGHT));
            } else {
                datesTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
            }
            document.add(datesTable);
            document.add(new Paragraph(""));

            // ===== ALLERGIES =====
            if (patient.getAllergies() != null && !patient.getAllergies().isEmpty()) {
                document.add(createSectionAlert("⚠️  ALLERGIES", patient.getAllergies(),
                        RED_LIGHT, RED));
                document.add(new Paragraph(""));
            }

            // ===== ANTÉCÉDENTS =====
            if (patient.getAntecedents() != null && !patient.getAntecedents().isEmpty()) {
                document.add(createSectionAlert("📋  ANTÉCÉDENTS", patient.getAntecedents(),
                        ORANGE_LIGHT, ORANGE));
                document.add(new Paragraph(""));
            }

            // ===== DIAGNOSTIC =====
            if (prescription.getDiagnostic() != null && !prescription.getDiagnostic().isEmpty()) {
                document.add(createSectionAlert("🎯  DIAGNOSTIC", prescription.getDiagnostic(),
                        AMBER_LIGHT, AMBER));
                document.add(new Paragraph(""));
            }

            // ===== MÉDICAMENTS =====
            document.add(new Paragraph("💊  MÉDICAMENTS").setBold().setFontSize(13)
                    .setFontColor(PRIMARY).setMarginBottom(8));

            if (prescription.getMedicamentsJson() != null) {
                try {
                    JsonNode meds = objectMapper.readTree(prescription.getMedicamentsJson());
                    if (meds.isArray()) {
                        int idx = 1;
                        for (JsonNode med : meds) {
                            String nom = med.has("nom") ? med.get("nom").asText() : "N/A";
                            String dosage = med.has("dosage") ? med.get("dosage").asText() : null;
                            String frequence = med.has("frequence") ? med.get("frequence").asText() : null;
                            String duree = med.has("duree") ? med.get("duree").asText() : null;

                            Table medTable = new Table(UnitValue.createPercentArray(new float[]{8, 92}))
                                    .useAllAvailableWidth().setMarginBottom(4);

                            medTable.addCell(new Cell()
                                    .add(new Paragraph(String.valueOf(idx))
                                            .setBold().setFontSize(11).setFontColor(WHITE)
                                            .setTextAlignment(TextAlignment.CENTER))
                                    .setBackgroundColor(PRIMARY)
                                    .setWidth(30).setHeight(22)
                                    .setBorder(Border.NO_BORDER)
                                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));

                            Cell detailCell = new Cell();
                            detailCell.add(new Paragraph(nom).setBold().setFontSize(10).setFontColor(DARK));

                            if (dosage != null || frequence != null || duree != null) {
                                StringBuilder tags = new StringBuilder();
                                if (dosage != null) tags.append("Dosage: ").append(dosage);
                                if (frequence != null) { if (tags.length() > 0) tags.append("   |   "); tags.append("Fréquence: ").append(frequence); }
                                if (duree != null) { if (tags.length() > 0) tags.append("   |   "); tags.append("Durée: ").append(duree); }
                                detailCell.add(new Paragraph(tags.toString()).setFontSize(9).setFontColor(GRAY).setMarginTop(2));
                            }

                            detailCell.setBorder(Border.NO_BORDER).setPaddingLeft(8);
                            medTable.addCell(detailCell);
                            document.add(medTable);
                            idx++;
                        }
                    }
                } catch (Exception e) {
                    document.add(new Paragraph(prescription.getMedicamentsJson()).setFontSize(10));
                }
            }
            document.add(new Paragraph(""));

            // ===== INSTRUCTIONS =====
            if (prescription.getInstructions() != null && !prescription.getInstructions().isEmpty()) {
                document.add(createSectionAlert("📝  INSTRUCTIONS PARTICULIÈRES",
                        prescription.getInstructions(), BLUE_LIGHT, BLUE));
                document.add(new Paragraph(""));
            }

            // ===== SIGNATURE =====
            document.add(new LineSeparator(new SolidLine(0.5f))
                    .setMarginTop(20).setMarginBottom(10));

            Table sigTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .useAllAvailableWidth();
            sigTable.addCell(new Cell()
                    .add(new Paragraph("").setHeight(50))
                    .setBorder(Border.NO_BORDER));
            sigTable.addCell(new Cell()
                    .add(new Paragraph("Fait à Dakar, le " + prescription.getDatePrescription().format(DATE_LONG_FMT))
                            .setFontSize(9).setFontColor(GRAY).setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph(""))
                    .add(new Paragraph("Signature du médecin:")
                            .setFontSize(9).setFontColor(GRAY).setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Dr. " + prescription.getMedecin().getUser().getPrenom() + " " +
                            prescription.getMedecin().getUser().getNom())
                            .setBold().setFontSize(10).setFontColor(DARK).setTextAlignment(TextAlignment.RIGHT))
                    .setBorder(Border.NO_BORDER));
            document.add(sigTable);

            // ===== PIED DE PAGE =====
            document.add(new LineSeparator(new SolidLine(1))
                    .setMarginTop(15).setMarginBottom(5));
            document.add(new Paragraph("CLINIQUE MEDICARE — Votre santé, notre priorité")
                    .setFontSize(7).setFontColor(GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();
        }

        return relativePath;
    }

    private String buildPatientDetails(com.medicare.entity.Patient patient) {
        StringBuilder sb = new StringBuilder();
        if (patient.getTelephone() != null) {
            sb.append("Tél : ").append(patient.getTelephone());
        }
        if (patient.getEmail() != null) {
            if (sb.length() > 0) sb.append("  •  ");
            sb.append("Email : ").append(patient.getEmail());
        }
        if (patient.getGroupeSanguin() != null) {
            if (sb.length() > 0) sb.append("  •  ");
            sb.append("Groupe sanguin : ").append(patient.getGroupeSanguin());
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private Cell createColoredBox(String title, String name, String subtitle, String details,
                                   DeviceRgb bgColor, DeviceRgb accentColor, float width) {
        Cell cell = new Cell()
                .setBackgroundColor(bgColor)
                .setBorder(new SolidBorder(accentColor, 1))
                .setPadding(12)
                .setWidth(width);

        cell.add(new Paragraph(title).setBold().setFontSize(8)
                .setFontColor(accentColor).setMarginBottom(3));
        cell.add(new Paragraph(name).setBold().setFontSize(13)
                .setFontColor(DARK).setMarginBottom(2));
        if (subtitle != null) {
            cell.add(new Paragraph(subtitle).setFontSize(9).setFontColor(GRAY));
        }
        if (details != null) {
            cell.add(new Paragraph(details).setFontSize(9).setFontColor(GRAY).setMarginTop(3));
        }
        return cell;
    }

    private Cell createInfoCell(String label, String value, DeviceRgb bgColor) {
        Cell cell = new Cell()
                .setBackgroundColor(bgColor)
                .setBorder(new SolidBorder(GRAY_LIGHT, 0.5f))
                .setPadding(8);
        cell.add(new Paragraph(label).setFontSize(8).setFontColor(GRAY)
                .setMarginBottom(2));
        cell.add(new Paragraph(value).setBold().setFontSize(10).setFontColor(DARK));
        return cell;
    }

    private Table createSectionAlert(String title, String content, DeviceRgb bgColor, DeviceRgb textColor) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{100}))
                .useAllAvailableWidth();
        table.addCell(new Cell()
                .add(new Paragraph(title).setBold().setFontSize(10).setFontColor(textColor).setMarginBottom(3))
                .add(new Paragraph(content).setFontSize(10).setFontColor(DARK))
                .setBackgroundColor(bgColor)
                .setBorder(new SolidBorder(textColor, 1))
                .setPadding(8));
        return table;
    }
}
