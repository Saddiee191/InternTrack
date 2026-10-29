package com.interntrack.resumes;

import com.interntrack.common.BadRequestException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfExtractorService {

    private static final Logger logger = LoggerFactory.getLogger(PdfExtractorService.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public String extractTextFromPdf(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File exceeds maximum allowed size of 5MB");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException("Only PDF files (.pdf) are supported");
        }

        if (contentType != null && !contentType.equalsIgnoreCase("application/pdf") && !contentType.equalsIgnoreCase("application/x-pdf")) {
            throw new BadRequestException("Invalid MIME type: File must be application/pdf");
        }

        try {
            byte[] bytes = file.getBytes();

            // Validate PDF Magic Bytes: %PDF- (0x25, 0x50, 0x44, 0x46, 0x2D)
            if (bytes.length < 5 || bytes[0] != 0x25 || bytes[1] != 0x50 || bytes[2] != 0x44 || bytes[3] != 0x46) {
                throw new BadRequestException("Invalid file format: File header does not match valid PDF specification");
            }

            try (PDDocument document = Loader.loadPDF(bytes)) {
                if (document.isEncrypted()) {
                    throw new BadRequestException("Password-protected or encrypted PDF files are not supported. Please upload an unlocked PDF.");
                }

                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                String extracted = stripper.getText(document);

                if (extracted == null || extracted.trim().isBlank()) {
                    throw new BadRequestException("Could not extract readable text from this PDF. Please ensure the resume is not a scanned image or empty document.");
                }

                return extracted.trim();
            }
        } catch (IOException e) {
            logger.error("Failed to parse PDF document: ", e);
            throw new BadRequestException("Failed to read PDF document: " + e.getMessage());
        }
    }
}
