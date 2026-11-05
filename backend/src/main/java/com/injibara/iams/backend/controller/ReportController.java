package com.injibara.iams.backend.controller;

import com.injibara.iams.backend.dto.ItemResponse;
import com.injibara.iams.backend.service.DashboardService;
import com.injibara.iams.backend.service.ItemService;
import com.injibara.iams.backend.service.StockService;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ItemService itemService;
    private final StockService stockService;
    private final DashboardService dashboardService;

    public ReportController(ItemService itemService, StockService stockService, DashboardService dashboardService) {
        this.itemService = itemService;
        this.stockService = stockService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/inventory")
    public ResponseEntity<byte[]> inventoryReport() {
        List<ItemResponse> items = itemService.search(null, null, null);
        StringBuilder sb = new StringBuilder();
        sb.append("Inventory Report\n");
        sb.append("Generated: ").append(java.time.LocalDateTime.now()).append("\n\n");
        sb.append("id,itemCode,itemName,category,location,unit,quantity,minimumStock,status\n");
        for (ItemResponse i : items) {
            sb.append(i.getId()).append(",")
              .append(csv(i.getItemCode())).append(",")
              .append(csv(i.getItemName())).append(",")
              .append(csv(i.getCategoryName())).append(",")
              .append(csv(i.getLocationName())).append(",")
              .append(csv(i.getUnit())).append(",")
              .append(i.getQuantity()).append(",")
              .append(i.getMinimumStock()).append(",")
              .append(i.getStatus()).append("\n");
        }
        return csvResponse("inventory_report.csv", sb.toString());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<byte[]> lowStockReport() {
        List<ItemResponse> items = itemService.lowStock();
        StringBuilder sb = new StringBuilder();
        sb.append("Low Stock Report\n");
        sb.append("Generated: ").append(java.time.LocalDateTime.now()).append("\n\n");
        sb.append("itemCode,itemName,quantity,minimumStock,status\n");
        for (ItemResponse i : items) {
            sb.append(csv(i.getItemCode())).append(",")
              .append(csv(i.getItemName())).append(",")
              .append(i.getQuantity()).append(",")
              .append(i.getMinimumStock()).append(",")
              .append(i.getStatus()).append("\n");
        }
        return csvResponse("low_stock_report.csv", sb.toString());
    }

    @GetMapping("/transactions")
    public ResponseEntity<byte[]> transactionReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction History Report\n");
        sb.append("Generated: ").append(java.time.LocalDateTime.now()).append("\n\n");
        sb.append("id,type,item,quantity,date,performedBy\n");
        for (var tx : stockService.history()) {
            sb.append(tx.getId()).append(",")
              .append(tx.getType()).append(",")
              .append(csv(tx.getItem() == null ? "" : tx.getItem().getItemName())).append(",")
              .append(tx.getQuantity()).append(",")
              .append(tx.getTransactionDate()).append(",")
              .append(csv(tx.getPerformedBy() == null ? "" : tx.getPerformedBy().getUsername())).append("\n");
        }
        return csvResponse("transaction_report.csv", sb.toString());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary() {
        return ResponseEntity.ok(dashboardService.summary());
    }

    @GetMapping("/inventory/pdf")
    public ResponseEntity<byte[]> inventoryPdf() {
        List<ItemResponse> items = itemService.search(null, null, null);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document();
            PdfWriter.getInstance(doc, baos);
            doc.open();
            doc.add(new Paragraph("Inventory Report"));
            doc.add(new Paragraph("Generated: " + java.time.LocalDateTime.now()));
            doc.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(5);
            table.addCell("Code");
            table.addCell("Name");
            table.addCell("Category");
            table.addCell("Qty");
            table.addCell("Min");
            for (ItemResponse i : items) {
                table.addCell(i.getItemCode() == null ? "" : i.getItemCode());
                table.addCell(i.getItemName() == null ? "" : i.getItemName());
                table.addCell(i.getCategoryName() == null ? "" : i.getCategoryName());
                table.addCell(String.valueOf(i.getQuantity()));
                table.addCell(String.valueOf(i.getMinimumStock()));
            }
            doc.add(table);
            doc.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "inventory_report.pdf");
            return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/low-stock/pdf")
    public ResponseEntity<byte[]> lowStockPdf() {
        List<ItemResponse> items = itemService.lowStock();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document();
            PdfWriter.getInstance(doc, baos);
            doc.open();
            doc.add(new Paragraph("Low Stock Report"));
            doc.add(new Paragraph("Generated: " + java.time.LocalDateTime.now()));
            doc.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(4);
            table.addCell("Code");
            table.addCell("Name");
            table.addCell("Qty");
            table.addCell("Min");
            for (ItemResponse i : items) {
                table.addCell(i.getItemCode() == null ? "" : i.getItemCode());
                table.addCell(i.getItemName() == null ? "" : i.getItemName());
                table.addCell(String.valueOf(i.getQuantity()));
                table.addCell(String.valueOf(i.getMinimumStock()));
            }
            doc.add(table);
            doc.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "low_stock_report.pdf");
            return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    private ResponseEntity<byte[]> csvResponse(String filename, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(content.getBytes(StandardCharsets.UTF_8));
    }

    private String csv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
