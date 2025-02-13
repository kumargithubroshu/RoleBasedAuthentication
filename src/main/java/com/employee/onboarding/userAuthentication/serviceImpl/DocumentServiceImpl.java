package com.employee.onboarding.userAuthentication.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
//import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.employee.onboarding.userAuthentication.entity.Document;
import com.employee.onboarding.userAuthentication.entity.User;
import com.employee.onboarding.userAuthentication.pojoRequest.DocumentRequest;
import com.employee.onboarding.userAuthentication.pojoResponse.DocumentResponse;
//import com.employee.onboarding.userAuthentication.pojoResponse.DocumentResponse;
import com.employee.onboarding.userAuthentication.repository.DocumentRepo;
import com.employee.onboarding.userAuthentication.repository.UserRepo;
import com.employee.onboarding.userAuthentication.service.DocumentService;

import jakarta.annotation.Resource;

@Service
public class DocumentServiceImpl implements DocumentService{

	 private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);
	 private static final String UPLOAD_DIR = "E:/uploads/"; 
	    
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Override
	public void uploadDocument(DocumentRequest documentRequest) throws IOException  {
		MultipartFile file = documentRequest.getFile();
        validateFile(file);

        // Check if user exists
        User user = userRepo.findById(documentRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + documentRequest.getUserId()));

        // Save file to the filesystem
        String fileName = file.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;
        saveFileToFileSystem(file, filePath);

        // Save document metadata to the database
        Document document = Document.builder()
                .documentName(fileName)
                .documentPath(filePath)
                .uploadedAt(LocalDateTime.now())
                .user(user)
                .build();

        documentRepo.save(document);
        log.info("Document metadata saved in the database: {}", document.getDocumentName());
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty. Please upload a valid document.");
        }

        if (!isValidFileType(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only PDF, DOCX, and PNG are allowed.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {  // 5MB limit
            throw new IllegalArgumentException("File size exceeds the maximum limit of 5MB.");
        }
    }

    private boolean isValidFileType(String fileType) {
        return fileType.equals("application/pdf") ||
               fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
               fileType.equals("image/png");
    }

//    private String generateUniqueFileName(String originalFilename) {
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        return UUID.randomUUID().toString() + extension;
//    }

    private void saveFileToFileSystem(MultipartFile file, String filePath) throws IOException {
        File dest = new File(filePath);
        dest.getParentFile().mkdirs();  // Create directories if they don't exist
        file.transferTo(dest);
    }


	
	@Override
	public List<DocumentResponse> getDocumentsByUserId(Long userId) {
		User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Document> documents = documentRepo.findByUser(user);
        return documents.stream()
                .map(document -> new DocumentResponse(document.getDocumentName()))
                .collect(Collectors.toList());
	
	}

	@Override
	public DocumentResponse getDocumentById(Long documentId) {
		Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return new DocumentResponse(document.getDocumentName());
	}
	
	
	@Override
    public boolean deleteDocumentById(Long documentId) throws IOException {
        Document document = documentRepo.findById(documentId)
                .orElse(null);

        if (document == null) {
            log.warn("Document not found with ID: {}", documentId);
            return false;
        }

        // Delete the file from the filesystem
        File file = new File(document.getDocumentPath());
        if (file.exists() && file.delete()) {
            documentRepo.delete(document);  // Delete from the database
            log.info("Document deleted successfully: {}", document.getDocumentName());
            return true;
        } else {
            log.error("Failed to delete file from filesystem for document: {}", document.getDocumentName());
            throw new IOException("Failed to delete the file from the filesystem.");
        }
    }
	
	
    }



	


