package com.employee.onboarding.userAuthentication.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.employee.onboarding.userAuthentication.pojoRequest.DocumentRequest;
import com.employee.onboarding.userAuthentication.pojoResponse.DocumentResponse;
import com.employee.onboarding.userAuthentication.service.DocumentService;
import com.employee.onboarding.userAuthentication.pojoResponse.Message;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/documents")



public class DocumentController {

    @Autowired
    private DocumentService documentService;
    
    private Logger log = LoggerFactory.getLogger(getClass());
    
    private static final String UPLOAD_DOCUMENT = "/upload";
    private static final String GET_DOCUMENT_BY_USER_ID = "/user/{userId}";
    private static final String GET_DOCUMENT_BY_ID = "/{documentId}";

    @Operation(summary = "upload documents")
    @PostMapping(value = UPLOAD_DOCUMENT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> uploadDocument( @RequestParam("file") MultipartFile file, @RequestParam Long userId) {

        log.info("Received request to upload document: {}");

        try {
            DocumentRequest documentRequest = new DocumentRequest(userId, file);
            documentService.uploadDocument(documentRequest);
            log.info("Document uploaded successfully: {}");
            return ResponseEntity.status(HttpStatus.CREATED).body(new Message("Document uploaded successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Validation error during document upload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during document upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message("Document upload failed. Please try again later."));
        }
    }


    @Operation(summary = "Fetch document by userId")
    @GetMapping(value = GET_DOCUMENT_BY_USER_ID )
    public ResponseEntity<List<DocumentResponse>> getDocumentsByUserId(@PathVariable Long userId) {
    	log.info("Received request to fetch documents for userId: {}", userId);
    	try {
    		List<DocumentResponse> responses = documentService.getDocumentsByUserId(userId);
    		log.info("Documents fetched successfully for userId: {}", userId);
            return ResponseEntity.ok(responses);
    	}catch (Exception e) {
            log.error("Unexpected error while fetching documents for userId {}: ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @Operation(summary = "Fetch document by documentId")
    @GetMapping(value = GET_DOCUMENT_BY_ID )
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long documentId) {
    	log.info("Received request to fetch documents for documentId: {}", documentId);
    	try {
    		DocumentResponse document = documentService.getDocumentById(documentId);
    		if(document!=null) {
    			return ResponseEntity.status(HttpStatus.OK).body(document);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} catch (Exception e) {
			log.error("Error occurred while fetching document with ID: {}", documentId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
    		}
    
    
    @Operation(summary = "Delete document by documentId")
    @DeleteMapping(value = "/delete/{documentId}")
    public ResponseEntity<Message> deleteDocumentById(@PathVariable Long documentId) {
        log.info("Received request to delete document with ID: {}", documentId);
        try {
            boolean isDeleted = documentService.deleteDocumentById(documentId);
            if (isDeleted) {
                return ResponseEntity.status(HttpStatus.OK).body(new Message("Document deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Document not found with ID: " + documentId));
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting document with ID: {}", documentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message("Failed to delete document. Please try again later."));

        }	
  }
    
     }



