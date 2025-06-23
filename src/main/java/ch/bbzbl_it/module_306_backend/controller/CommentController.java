package ch.bbzbl_it.module_306_backend.controller;

import ch.bbzbl_it.module_306_backend.dto.CommentDTO;
import ch.bbzbl_it.module_306_backend.dto.UserDTO;
import ch.bbzbl_it.module_306_backend.entity.Attachment;
import ch.bbzbl_it.module_306_backend.entity.Comment;
import ch.bbzbl_it.module_306_backend.repository.AttachmentRepository;
import ch.bbzbl_it.module_306_backend.repository.CommentRepository;
import ch.bbzbl_it.module_306_backend.repository.UserRepository;
import ch.bbzbl_it.module_306_backend.service.AttachmentService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/comment")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{ticketId}")
    public ResponseEntity<List<CommentDTO>> getAllComments(@PathVariable("ticketId") Long ticketId) {
        if (ticketId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        var comments = commentRepository.findByTicket_Id(ticketId);
        var attachments = attachmentRepository.findAttachmentsByLinkTypeAndLinkId("COMMENT", ticketId);

        return new ResponseEntity<>(comments.stream().map(comment -> CommentDTO.toDTO(comment, attachments)).toList(), HttpStatus.OK);
    }

    @PostMapping(path = "/{ticketId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentDTO> createComment(
            @PathParam("ticketId") Long ticketId,
            @RequestPart("files") MultipartFile[] attachments,
            @RequestPart("comment") CommentDTO commentDTO
    ) {
        try {
            if (commentDTO == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            var comment = saveComment(commentDTO, ticketId);
            List<Attachment> savedAttachments = attachmentService.saveAllAttachments(commentDTO.getAttachments(), attachments, comment.getId(), "COMMENT");

            return new ResponseEntity<>(CommentDTO.toDTO(comment, savedAttachments), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable("id") Long id,
            @RequestPart("files") MultipartFile[] attachments,
            @RequestPart("comment") CommentDTO commentDTO
    ) {
        try {
            if (commentDTO == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Optional<Comment> commentOptional = commentRepository.findById(commentDTO.getId());
            Comment result;
            if (commentOptional.isPresent()) {
                var commentToSave = commentOptional.get();
                commentToSave.setContent(commentDTO.getContent());
                commentToSave.setModifiedAt(Instant.now());

                result = commentRepository.save(commentToSave);
            } else {
                result = saveComment(commentDTO, id);
            }
            List<Attachment> savedAttachments = attachmentService.saveAllAttachments(commentDTO.getAttachments(), attachments, result.getId(), "COMMENT");

            return new ResponseEntity<>(CommentDTO.toDTO(result, savedAttachments), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Comment saveComment(CommentDTO commentDTO, Long ticketId) {
        commentDTO.setId(null);
        UserDTO creator = UserDTO.toDTO(userRepository.getUserByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName())
        );
        commentDTO.setCreatedBy(creator);
        commentDTO.setCreatedAt(Instant.now());
        commentDTO.setModifiedAt(null);
        commentDTO.setTicketId(ticketId);
        if (!commentDTO.getAttachments().isEmpty()) {
            commentDTO.getAttachments().forEach(attachment -> attachment.setId(null));
        }

        return commentRepository.save(CommentDTO.toComment(commentDTO));
    }

}
