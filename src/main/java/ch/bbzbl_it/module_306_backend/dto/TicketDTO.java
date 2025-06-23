package ch.bbzbl_it.module_306_backend.dto;

import ch.bbzbl_it.module_306_backend.entity.Attachment;
import ch.bbzbl_it.module_306_backend.entity.Comment;
import ch.bbzbl_it.module_306_backend.entity.Ticket;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TicketDTO {

    private Long id;
    private String title;
    private String description;
    private String tags;
    private Integer priority;
    private UserDTO createdBy;
    private Instant createdAt;
    private UserDTO modifiedBy;
    private Instant modifiedAt;
    private UserDTO assignedTo;
    private Integer status;
    private Instant dueDate;
    private List<CommentDTO> comments;
    private List<AttachmentDTO> attachments;

    public static TicketDTO toDTO(Ticket ticket, List<Comment> comments, List<Attachment> attachments) {
        if (ticket == null) {
            return null;
        }

        if (comments == null) {
            comments = new ArrayList<>();
        }

        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setTags(ticket.getTags());
        dto.setPriority(ticket.getPriority());
        dto.setCreatedBy(UserDTO.toDTO(ticket.getCreatedBy()));
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setModifiedBy(UserDTO.toDTO(ticket.getModifiedBy()));
        dto.setModifiedAt(ticket.getModifiedAt());
        dto.setAssignedTo(UserDTO.toDTO(ticket.getAssignedTo()));
        dto.setStatus(ticket.getStatus());
        dto.setDueDate(ticket.getDueDate());
        dto.setComments(comments.stream().map(comment -> CommentDTO.toDTO(comment, attachments)).toList());
        dto.setAttachments(
                attachments.stream()
                        .filter(attachment -> attachment.getLinkType().equalsIgnoreCase("TICKET") && attachment.getLinkId().equals(ticket.getId()))
                        .map(AttachmentDTO::toDTO)
                        .toList()
        );

        return dto;
    }

    public static Ticket toTicket(TicketDTO dto) {
        if (dto == null) {
            return null;
        }

        Ticket ticket = new Ticket();
        ticket.setId(dto.getId());
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setTags(dto.getTags());
        ticket.setPriority(dto.getPriority());
        ticket.setCreatedBy(UserDTO.toUser(dto.getCreatedBy()));
        ticket.setCreatedAt(dto.getCreatedAt());
        ticket.setModifiedBy(UserDTO.toUser(dto.getModifiedBy()));
        ticket.setModifiedAt(dto.getModifiedAt());
        ticket.setAssignedTo(UserDTO.toUser(dto.getAssignedTo()));
        ticket.setStatus(dto.getStatus());
        ticket.setDueDate(dto.getDueDate());

        return ticket;
    }

}
