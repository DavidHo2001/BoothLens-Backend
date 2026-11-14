package com.boothlens.service;

import com.boothlens.dto.response.booth.BoothQuotationCalculation;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generateQuotationPdf(String referenceCode, Integer userId, BoothQuotationCalculation calculation) throws Exception {
        Context context = new Context();
        context.setVariable("referenceCode", referenceCode);
        context.setVariable("userId", userId);
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        context.setVariable("currentDate", today.format(formatter));
        context.setVariable("validUntil", today.plusDays(30).format(formatter));
        
        // Pass the quotation calculation into the HTML template.
        context.setVariable("calculation", calculation);

        String htmlContent = templateEngine.process("quotation", context);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, ""); 
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }
}
