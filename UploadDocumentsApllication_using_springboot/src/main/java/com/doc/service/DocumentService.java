package com.doc.service;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.doc.entity.Document;
import com.doc.repository.DocumentRepository;

@Service
public class DocumentService {
	@Autowired
	private DocumentRepository documentRepository;

	@Value("${upload.path}") 
	private String uploadDir;
	
	public DocumentService(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}
	public Document uploadDocument(MultipartFile file) throws IllegalStateException, IOException {
		System.out.println("service.....................");
		String fileName = file.getOriginalFilename();
		String filePath = uploadDir + "/" + fileName;

		Document document = new Document();
		document.setName(fileName);
		document.setType(file.getContentType());
		document.setPath(filePath);
		
		return documentRepository.save(document);
	}
	public Page<Document> getDocument(int page, int size) {
		return documentRepository.findAll(PageRequest.of(page, size));
	}
	public void deleteDocument(Long id) {
		documentRepository.deleteById(id);
	}
	public Document getDocumentById(Long id) {
	    return documentRepository.findById(id).orElse(null);
	}
}