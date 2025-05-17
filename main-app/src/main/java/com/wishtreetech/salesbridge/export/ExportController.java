package com.wishtreetech.salesbridge.export;

import com.wishtreetech.commonutils.dto.export.ApiResponse;
import com.wishtreetech.commonutils.dto.export.ExportRequestDTO;
import com.wishtreetech.commonutils.service.export.ExportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);

    @Autowired
    private ExportService exportService;

    /**
     * Process company data export and send via email asynchronously
     *
     * @param request The export request containing company data and email
     * @return ResponseEntity with success/failure message
     */
    @PostMapping
    public ResponseEntity<ApiResponse> processExport(@Valid @RequestBody ExportRequestDTO request) {
        logger.info("Received export request for {} companies to email: {}",
                request.getCompanies().size(), request.getEmail());

        // Validate required fields
        if (request.getCompanies() == null || request.getCompanies().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, "No companies provided for export"));
        }

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, "Email address is required"));
        }

        // Process the export asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                exportService.processExportRequest(request);
            } catch (Exception e) {
                logger.error("Error processing export request", e);
            }
        });

        return ResponseEntity.ok(new ApiResponse(true,
                "Export request received. The file will be emailed to " + request.getEmail() + " when ready."));
    }
}