package ch.bbzbl_it.module_306_backend.controller;

import ch.bbzbl_it.module_306_backend.entity.Attachment;
import ch.bbzbl_it.module_306_backend.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @GetMapping("/{type}/{id}/{fileName}")
    public ResponseEntity<InputStreamResource> getAttachment(
            @PathVariable("type") String type,
            @PathVariable("id") Long id,
            @PathVariable("fileName") String fileName,
            @RequestParam("mimeType") String mimeType
    ) {
        try {
            var attachment = new Attachment();
            attachment.setLinkType(type);
            attachment.setLinkId(id);
            attachment.setUrl(fileName);
            attachment.setMimeType(mimeType);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(new InputStreamResource(attachmentService.getAttachment(attachment)));
        } catch (InvalidMediaTypeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
