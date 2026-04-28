package com.colibrihub.wordpress.controller;

import com.colibrihub.wordpress.dto.*;
import com.colibrihub.wordpress.entity.IntegrationLog;
import com.colibrihub.wordpress.enums.LogStatus;
import com.colibrihub.wordpress.repository.IntegrationLogRepository;
import com.colibrihub.wordpress.service.EspoCrmService;
import com.colibrihub.wordpress.service.WooCommerceService;
import com.colibrihub.wordpress.service.impl.ProductEventPublisherImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/integrations")
//@Tag(name = "Integraciones", description = "Endpoints que orquestan múltiples sistemas")
public class IntegrationController {
    private final WooCommerceService wooService;
    private final EspoCrmService crmService;
    private final IntegrationLogRepository logRepository; // ← Nuevo
    private final ProductEventPublisherImpl eventPublisher;

    public IntegrationController(WooCommerceService wooService,
                                 EspoCrmService crmService,
                                 IntegrationLogRepository logRepo, ProductEventPublisherImpl eventPublisher) { // ← Inyección
        this.wooService = wooService;
        this.crmService = crmService;
        this.logRepository = logRepo;
        this.eventPublisher = eventPublisher;
    }

    /*@Operation(summary = "Crear producto en WooCommerce y Lead en EspoCRM")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto y Lead creados"),
            @ApiResponse(responseCode = "207", description = "Producto creado, Lead falló"),
            @ApiResponse(responseCode = "422", description = "Validación falló"),
            @ApiResponse(responseCode = "502", description = "WooCommerce no disponible")
    })*/


    //Asincrono
    @PostMapping("/product-with-lead-asincrono")
    public ResponseEntity<ApiResponse<WooProductDto>> createProductWithLeadAsincrono(
            @Valid @RequestBody CreateProductWithLeadRequest request) {

        CreateProductRequest createProductRequest = new CreateProductRequest();
        createProductRequest.setDescription(request.getProductDescription());
        createProductRequest.setName(request.getProductName());
        createProductRequest.setPrice(request.getPrice());

        // 1. Crear producto en WooCommerce
        WooProductDto product = wooService.createProduct(createProductRequest);
        // 2. Publicar evento (NO esperar a crear el Lead)
        ProductCreatedEvent event = new ProductCreatedEvent(
                request.getProductName(),
                request.getPrice(),
                request.getProductDescription(),
                request.getContactEmail()
        );
        eventPublisher.publishProductCreated(event);
        // 3. Responder inmediatamente al cliente

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Producto creado, procesando lead en segundo plano",
                        product
                ));
    }

    @PostMapping("/product-with-lead")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createProductWithLead(
            @Valid @RequestBody CreateProductWithLeadRequest request) {
        Map<String, Object> result = new HashMap<>();
        // 1. Crear producto en WooCommerce
        try {

            CreateProductRequest createProductRequest = new CreateProductRequest();
            createProductRequest.setDescription(request.getProductDescription());
            createProductRequest.setName(request.getProductName());
            createProductRequest.setPrice(request.getPrice());

            WooProductDto product = wooService.createProduct(createProductRequest);
            result.put("product", product);

            // Log exitoso de WooCommerce
            saveLog("WooCommerce", "CREATE_PRODUCT", LogStatus.SUCCESS, null,
                    toJson(request));

        } catch (Exception e) {
            // Log fallido
            saveLog("WooCommerce", "CREATE_PRODUCT", LogStatus.FAILED,
                    e.getMessage(), toJson(request));
            // Si falla WooCommerce, no continuar
            throw new RuntimeException("Error al crear producto: " +
                    e.getMessage());
        }
        // 2. Crear Lead en EspoCRM
        try {
            String leadDesc = String.format("Interesado en producto: %s ($%.2f)",
                    request.getProductName(), request.getPrice());
            Map<String, Object> lead = crmService.createLead(
                    request.getContactFirstName(),
                    request.getContactLastName(),
                    request.getContactEmail(),
                    leadDesc
            );
            result.put("lead", lead);
            saveLog("EspoCRM", "CREATE_LEAD", LogStatus.SUCCESS, null,
                    toJson(request));
            result.put("status", "success");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Producto y Lead creados",
                            result));
        } catch (Exception e) {
            saveLog("EspoCRM", "CREATE_LEAD", LogStatus.FAILED, e.getMessage(),
                    toJson(request));
            // Producto creado pero Lead falló — 207 Multi-Status
            result.put("lead", null);
            result.put("status", "partial");
            result.put("leadError", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.MULTI_STATUS) // 207
                    .body(ApiResponse.success("Producto creado, Lead falló",
                            result));
        }
    }

    private void saveLog(String system, String op, LogStatus status,
                         String error, String data) {
        IntegrationLog log = new IntegrationLog();
        log.setTargetSystem(system);
        log.setOperation(op);
        log.setStatus(status);
        log.setErrorMessage(error);
        log.setRequestData(data);
        logRepository.save(log); // INSERT en MySQL
    }

    private String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    // En IntegrationController
    @Operation(summary = "Consultar logs de integración")
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<IntegrationLog>>> getLogs(
            @Parameter(description = "Filtrar por sistema")
            @RequestParam(required = false) String system,
            @Parameter(description = "Filtrar por estado")
            @RequestParam(required = false) LogStatus status) {
        List<IntegrationLog> logs;
        if (system != null && status != null) {
            logs = logRepository.findByTargetSystemAndStatus(system, status);
        } else if (system != null) {
            logs = logRepository.findByTargetSystem(system);
        } else if (status != null) {
            logs = logRepository.findByStatus(status);
        } else {
            logs = logRepository.findAll();
        }
        return ResponseEntity.ok(ApiResponse.success("Logs obtenidos", logs));
    }
}
