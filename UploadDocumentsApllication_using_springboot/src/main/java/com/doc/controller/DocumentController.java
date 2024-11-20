package com.doc.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.doc.entity.Document;
import com.doc.service.DocumentService;


@Controller
@RequestMapping("/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;
    
    @Value("${upload.path}")
    private String uploadDir;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    @GetMapping()
    public String listDocuments(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Document> documentPage = documentService.getDocument(page, 5);       
        model.addAttribute("documents", documentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", documentPage.getTotalPages());
        
        return "index"; 
    }
    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file, Model model) {
        try {
            documentService.uploadDocument(file);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload file: " + e.getMessage());
        }
        return "redirect:/documents"; 
    }
    @GetMapping("/delete/{id}")
    public String deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id); 
        return "redirect:/documents"; 
    }   
    @GetMapping("/view/{id}")
    public ResponseEntity<String> viewDocument(@PathVariable Long id) throws IOException {
        Document document = documentService.getDocumentById(id);        
        if (document == null) {
            return new ResponseEntity<>("Document not found", HttpStatus.NOT_FOUND);
        }
        File file = new File(uploadDir + "/" + document.getName());
        String fileName = file.getName();
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        String filePath = file.getAbsolutePath();
        String responseMessage = "Filename: " + fileName + "\n" +
                                 "File Type: " + contentType + "\n" +
                                 "File Path: " + filePath;
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }   
}