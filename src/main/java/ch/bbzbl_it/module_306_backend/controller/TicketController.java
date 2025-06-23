package ch.bbzbl_it.module_306_backend.controller;

import ch.bbzbl_it.module_306_backend.dto.TicketDTO;
import ch.bbzbl_it.module_306_backend.dto.UserDTO;
import ch.bbzbl_it.module_306_backend.entity.Attachment;
import ch.bbzbl_it.module_306_backend.entity.Comment;
import ch.bbzbl_it.module_306_backend.entity.Ticket;
import ch.bbzbl_it.module_306_backend.entity.User;
import ch.bbzbl_it.module_306_backend.repository.AttachmentRepository;
import ch.bbzbl_it.module_306_backend.repository.CommentRepository;
import ch.bbzbl_it.module_306_backend.repository.TicketRepository;
import ch.bbzbl_it.module_306_backend.repository.UserRepository;
import ch.bbzbl_it.module_306_backend.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/ticket")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicket(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        var comments = commentRepository.findByTicket_Id(id);
        var attachments = attachmentRepository.findAllAttachmentsForTicket(id, (Long[]) comments.stream().map(Comment::getId).toArray());

        return optionalTicket
                .map(ticket -> new ResponseEntity<>(TicketDTO.toDTO(ticket, comments, attachments), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/overviewData")
    public ResponseEntity<List<TicketDTO>> getAllTicketOverviewData() {
        return new ResponseEntity<>(ticketRepository.findAll().stream().map(ticket -> TicketDTO.toDTO(ticket, null, null)).toList(), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<TicketDTO> createTicket(
            @RequestParam("files") MultipartFile[] attachments,
            @RequestBody TicketDTO ticketDTO
    ) {
        try {
            if (ticketDTO == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            var ticket = saveTicket(ticketDTO);
            List<Attachment> savedAttachments = attachmentService.saveAllAttachments(ticketDTO.getAttachments(), attachments, ticket.getId(), "TICKET");

            return new ResponseEntity<>(TicketDTO.toDTO(ticket, new ArrayList<>(), savedAttachments), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(
            @PathVariable("id") Long id,
            @RequestParam("files") MultipartFile[] attachments,
            @RequestBody TicketDTO ticketDTO
    ) {
        try {
            if (ticketDTO == null || ticketDTO.getModifiedBy() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Optional<Ticket> ticketOptional = ticketRepository.findById(id);
            Ticket result;
            if (ticketOptional.isPresent()) {
                var ticketToSave = ticketOptional.get();
                ticketToSave.setTitle(ticketDTO.getTitle());
                ticketToSave.setDescription(ticketDTO.getDescription());
                ticketToSave.setTags(ticketDTO.getTags());
                ticketToSave.setPriority(ticketDTO.getPriority());
                User modifier = userRepository.getUserByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName()
                );
                ticketToSave.setModifiedBy(modifier);
                ticketToSave.setModifiedAt(Instant.now());
                ticketToSave.setAssignedTo(UserDTO.toUser(ticketDTO.getAssignedTo()));
                ticketToSave.setStatus(ticketDTO.getStatus());
                ticketToSave.setDueDate(ticketDTO.getDueDate());
                result = ticketRepository.save(ticketToSave);
            } else {
                result = saveTicket(ticketDTO);
            }
            List<Attachment> savedAttachments = attachmentService.saveAllAttachments(ticketDTO.getAttachments(), attachments, result.getId(), "TICKET");

            return new ResponseEntity<>(TicketDTO.toDTO(result, null, savedAttachments), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Ticket saveTicket(TicketDTO ticketDTO) {
        ticketDTO.setId(null);
        ticketDTO.setCreatedAt(Instant.now());
        UserDTO creator = UserDTO.toDTO(
                userRepository.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
        );
        ticketDTO.setCreatedBy(creator);
        ticketDTO.setModifiedAt(null);
        ticketDTO.setModifiedBy(null);
        if (!ticketDTO.getAttachments().isEmpty()) {
            ticketDTO.getAttachments().forEach(attachment -> attachment.setId(null));
        }
        return ticketRepository.save(TicketDTO.toTicket(ticketDTO));
    }
}
