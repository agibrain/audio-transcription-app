package com.audio.transcribe.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * controller for audio transcription
 * 
 * @author subhrajeetghosh
 */

@RestController
@RequestMapping("/audio")
public class AudioTranscriptionController {
    private static final Logger logger = LoggerFactory.getLogger(AudioTranscriptionController.class);

    @org.springframework.beans.factory.annotation.Value("${whisper.service.url}")
    private String WHISPER_SERVICE_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Rest API to transcribe the audio file using whisper service
     * 
     * @param file {@link MultipartFile}
     * @return {@link ResponseEntity}
     */
    @PostMapping("/api/transcribe")
    public ResponseEntity<?> transcribe(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            logger.debug("Received file: {} with size: {} bytes",
                    file.getOriginalFilename(), file.getSize());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    WHISPER_SERVICE_URL,
                    requestEntity,
                    String.class);

            logger.debug("Received response from Whisper service: {}",
                    response.getStatusCode());

            return response;

        } catch (Exception e) {
            logger.error("Error processing audio file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing audio file: " + e.getMessage());
        }
    }
}
