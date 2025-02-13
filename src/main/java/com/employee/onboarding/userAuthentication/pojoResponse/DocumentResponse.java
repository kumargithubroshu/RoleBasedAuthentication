package com.employee.onboarding.userAuthentication.pojoResponse;

import java.time.LocalDateTime;

import com.employee.onboarding.userAuthentication.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor


public class DocumentResponse {
    //private Long documentId;
    //private Long userId;
    private String documentName;
    //private String documentPath;
    //private LocalDateTime uploadedAt;

	public DocumentResponse(String documentName) {
		
		this.documentName = documentName;
	}
    
    
   
   
}

