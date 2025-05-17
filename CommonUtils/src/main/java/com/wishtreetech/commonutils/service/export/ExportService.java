package com.wishtreetech.commonutils.service.export;

import com.wishtreetech.commonutils.dto.export.ExportRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.export.temp-dir:./temp}")
    private String tempDir;

    /**
     * Process export request and send result via email
     *
     * @param request The export request containing companies and details
     * @throws Exception If any error occurs during processing
     */
    public void processExportRequest(ExportRequestDTO request) throws Exception {
        logger.info("Processing export request for {} companies", request.getCompanies().size());

        // Create temp directory if it doesn't exist
        Path tempDirPath = Paths.get(tempDir);
        if (!Files.exists(tempDirPath)) {
            Files.createDirectories(tempDirPath);
        }

        // Generate a unique filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String fileName = request.getCompanyType() + "-export-" + timestamp + ".csv";
        String filePath = tempDirPath.resolve(fileName).toString();

        // Generate the CSV file
        if ("detailed".equals(request.getExportType()) && request.getDetailsMap() != null
                && !request.getDetailsMap().isEmpty()) {
            generateDetailedCsv(request.getCompanies(), request.getDetailsMap(), filePath);
        } else {
            generateSimpleCsv(request.getCompanies(), filePath, request.getCompanyType());
        }

        // Send email with the CSV file attached
        sendExportEmail(request.getEmail(), filePath, fileName, request.getCompanyType());

        // Clean up - delete the temporary file
        try {
            Files.deleteIfExists(Paths.get(filePath));
            logger.info("Temporary export file deleted: {}", filePath);
        } catch (IOException e) {
            logger.warn("Failed to delete temporary export file: {}", filePath, e);
        }
    }

    /**
     * Generate a simple CSV with basic company information
     */
    private void generateSimpleCsv(List<Map<String, Object>> companies, String filePath, String companyType)
            throws IOException {
        // Define headers based on company type
        List<String> headers = new ArrayList<>(Arrays.asList(
                "Company Name", "Website", "Team Size", "Location"));

        if ("ycombinator".equalsIgnoreCase(companyType)) {
            headers.addAll(Arrays.asList("Industries", "YC Batch", "Region",
                    "One-liner Description", "Full Description"));
        } else if ("clutch".equalsIgnoreCase(companyType)) {
            headers.addAll(Arrays.asList("Rating", "Min Project Size", "Hourly Rate",
                    "Services Provided", "About", "Verified"));
        }

        // Create CSV printer
        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader(headers.toArray(new String[0])))) {

            // Write records
            for (Map<String, Object> company : companies) {
                List<Object> record = new ArrayList<>();

                // Basic company info
                record.add(getStringValue(company.get("name")));
                record.add(getStringValue(company.get("website")));
                record.add(getStringValue(company.get("teamSize")));
                record.add(getStringValue(company.get("location")));

                // Company-type specific fields
                if ("ycombinator".equalsIgnoreCase(companyType)) {
                    record.add(getStringValue(company.get("industries")));
                    record.add(getStringValue(company.get("batch")));
                    record.add(getStringValue(company.get("region")));
                    record.add(getStringValue(company.get("oneLiner")));
                    record.add(getStringValue(company.get("longDescription")));
                } else if ("clutch".equalsIgnoreCase(companyType)) {
                    record.add(getStringValue(company.get("rating")));
                    record.add(getStringValue(company.get("minProjectSize")));
                    record.add(getStringValue(company.get("hourlyRate")));
                    record.add(getStringValue(company.get("servicesProvided")));
                    record.add(getStringValue(company.get("about")));
                    record.add(getStringValue(company.get("verified")));
                }

                csvPrinter.printRecord(record);
            }

            csvPrinter.flush();
        }

        logger.info("Simple CSV export generated: {}", filePath);
    }

    /**
     * Generate a detailed CSV with company, founder, and job information
     */
    @SuppressWarnings("unchecked")
    private void generateDetailedCsv(List<Map<String, Object>> companies, Map<String, Object> detailsMap,
                                     String filePath) throws IOException {
        // Define all columns for the CSV
        String[] headers = new String[] {
                "Company Name", "Website", "Team Size", "Location", "Industries", "YC Batch",
                "Region", "One-liner Description", "Full Description", "Founded Year",
                "LinkedIn", "Twitter", "Facebook", "Crunchbase",
                "Founder Name", "Founder Role", "Founder LinkedIn", "Founder Twitter",
                "Job Title", "Job Location", "Apply Link"
        };

        // Create CSV printer
        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {

            // Write records for each company
            for (Map<String, Object> company : companies) {
                String companyId = getStringValue(company.get("id"));
                Map<String, Object> details = null;

                // Get company details if available
                if (detailsMap.containsKey(companyId)) {
                    details = (Map<String, Object>) detailsMap.get(companyId);
                }

                // Get founders and jobs if available
                List<Map<String, Object>> founders = details != null && details.containsKey("founder")
                        ? (List<Map<String, Object>>) details.get("founder")
                        : Collections.emptyList();

                List<Map<String, Object>> jobs = details != null && details.containsKey("job")
                        ? (List<Map<String, Object>>) details.get("job")
                        : Collections.emptyList();

                // Calculate how many rows we need
                int numFounders = founders.size();
                int numJobs = jobs.size();
                int numRows = Math.max(1, Math.max(numFounders, numJobs));

                // Create rows for this company
                for (int i = 0; i < numRows; i++) {
                    List<Object> record = new ArrayList<>();

                    // Company data only in first row
                    if (i == 0) {
                        // Basic company info
                        record.add(getStringValue(company.get("name")));
                        record.add(getStringValue(company.get("website")));
                        record.add(getStringValue(company.get("teamSize")));
                        record.add(getStringValue(company.get("location")));
                        record.add(getStringValue(company.get("industries")));
                        record.add(getStringValue(company.get("batch")));
                        record.add(getStringValue(company.get("region")));
                        record.add(getStringValue(company.get("oneLiner")));
                        record.add(getStringValue(company.get("longDescription")));

                        // Founded year
                        record.add(details != null ? getStringValue(details.get("foundedYear")) : "");

                        // Social media links
                        if (details != null && details.containsKey("socialMediaLinks")) {
                            Map<String, Object> socialLinks = (Map<String, Object>) details.get("socialMediaLinks");
                            record.add(socialLinks.containsKey("LinkedIn") ? getStringValue(socialLinks.get("LinkedIn")) : "");
                            record.add(socialLinks.containsKey("Twitter") ? getStringValue(socialLinks.get("Twitter")) : "");
                            record.add(socialLinks.containsKey("Facebook") ? getStringValue(socialLinks.get("Facebook")) : "");
                            record.add(socialLinks.containsKey("Crunchbase") ? getStringValue(socialLinks.get("Crunchbase")) : "");
                        } else {
                            record.add(""); // LinkedIn
                            record.add(""); // Twitter
                            record.add(""); // Facebook
                            record.add(""); // Crunchbase
                        }
                    } else {
                        // Empty cells for company data in subsequent rows
                        for (int j = 0; j < 14; j++) {
                            record.add("");
                        }
                    }

                    // Founder information
                    if (i < numFounders) {
                        Map<String, Object> founder = founders.get(i);
                        record.add(getStringValue(founder.get("founderName")));
                        record.add(getStringValue(founder.get("founderRole")));
                        record.add(getStringValue(founder.get("linkedInProfile")));
                        record.add(getStringValue(founder.get("twitterProfile")));
                    } else {
                        record.add(""); // Founder Name
                        record.add(""); // Founder Role
                        record.add(""); // Founder LinkedIn
                        record.add(""); // Founder Twitter
                    }

                    // Job information
                    if (i < numJobs) {
                        Map<String, Object> job = jobs.get(i);
                        record.add(getStringValue(job.get("jobTitle")));
                        record.add(getStringValue(job.get("location")));
                        record.add(getStringValue(job.get("applyLink")));
                    } else {
                        record.add(""); // Job Title
                        record.add(""); // Job Location
                        record.add(""); // Apply Link
                    }

                    csvPrinter.printRecord(record);
                }
            }

            csvPrinter.flush();
        }

        logger.info("Detailed CSV export generated: {}", filePath);
    }

    /**
     * Send an email with the export file attachment
     */
    private void sendExportEmail(String email, String filePath, String fileName, String companyType)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(email);

        // Format company type for display
        String formattedType = "ycombinator".equalsIgnoreCase(companyType)
                ? "YCombinator"
                : companyType.substring(0, 1).toUpperCase() + companyType.substring(1);

        helper.setSubject("Your " + formattedType + " Companies Export");

        // Create HTML email content using Thymeleaf
        Context context = new Context();
        context.setVariable("companyType", formattedType);
        String htmlContent = templateEngine.process("email/export-template", context);

        // Set plain text as fallback
        String plainText = "Hello,\n\n" +
                "Your requested export of " + formattedType + " companies is attached to this email.\n\n" +
                "Thank you for using SalesBridge!\n\n" +
                "This is an automated message, please do not reply.";

        helper.setText(plainText, htmlContent);

        // Add the CSV file as an attachment
        helper.addAttachment(fileName, new File(filePath));

        // Send the email
        mailSender.send(message);
        logger.info("Export email sent to: {}", email);
    }

    /**
     * Helper method to convert value to string safely
     */
    private String getStringValue(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof List) {
            return String.join("; ", ((List<?>) value).stream()
                    .map(Object::toString)
                    .toArray(String[]::new));
        }

        return value.toString();
    }
}