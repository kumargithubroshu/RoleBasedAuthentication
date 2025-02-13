package com.employee.onboarding.userAuthentication.service;

import java.io.IOException;
import java.util.List;
import com.employee.onboarding.userAuthentication.pojoRequest.DocumentRequest;
import com.employee.onboarding.userAuthentication.pojoResponse.DocumentResponse;

import jakarta.annotation.Resource;

public interface DocumentService {
    
   public void uploadDocument(DocumentRequest documentRequest) throws IOException;
    
   public List<DocumentResponse> getDocumentsByUserId(Long userId);
    
   public DocumentResponse getDocumentById(Long documentId);
   
   boolean deleteDocumentById(Long documentId) throws IOException;
   
   
}


