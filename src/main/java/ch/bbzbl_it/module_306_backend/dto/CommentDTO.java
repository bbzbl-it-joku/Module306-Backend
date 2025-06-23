package ch.bbzbl_it.module_306_backend.dto;

import ch.bbzbl_it.module_306_backend.entity.Attachment;
import ch.bbzbl_it.module_306_backend.entity.Comment;
import ch.bbzbl_it.module_306_backend.entity.Ticket;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CommentDTO {

    private Long id;
    private String content;
    private UserDTO createdBy;
    private Instant createdAt;
    private Instant modifiedAt;
    private Long ticketId;
    private List<AttachmentDTO> attachments;

    public static CommentDTO toDTO(Comment comment, List<Attachment> attachments) {
        if (comment == null) {
            return null;
        }

        if (attachments == null) {
            attachments = new ArrayList<>();
        }

        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedBy(UserDTO.toDTO(comment.getCreatedBy()));
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setModifiedAt(comment.getModifiedAt());
        if (comment.getTicket() == null || comment.getTicket().getId() == null) {
            dto.setTicketId(null);
        }
        dto.setAttachments(
                attachments.stream()
                        .filter(attachment -> attachment.getLinkType().equalsIgnoreCase("COMMENT") && attachment.getLinkId().equals(comment.getId()))
                        .map(AttachmentDTO::toDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public static Comment toComment(CommentDTO commentDTO) {
        if (commentDTO == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setId(commentDTO.getId());
        comment.setContent(commentDTO.getContent());
        comment.setCreatedBy(UserDTO.toUser(commentDTO.getCreatedBy()));
        comment.setCreatedAt(commentDTO.getCreatedAt());
        comment.setModifiedAt(commentDTO.getModifiedAt());
        comment.setTicket(new Ticket());
        comment.getTicket().setId(commentDTO.getTicketId());

        return comment;
    }
}
