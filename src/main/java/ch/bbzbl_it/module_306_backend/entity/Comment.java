package ch.bbzbl_it.module_306_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    private User createdBy;
    private Instant createdAt;
    private Instant modifiedAt;
    @ManyToOne
    @JoinColumn(name = "ticketId", nullable = false)
    private Ticket ticket;

}
