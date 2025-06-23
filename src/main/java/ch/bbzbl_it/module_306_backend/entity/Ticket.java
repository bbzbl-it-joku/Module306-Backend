package ch.bbzbl_it.module_306_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String tags;
    private Integer priority;
    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    private User createdBy;
    private Instant createdAt;
    @ManyToOne
    @JoinColumn(name = "modifiedBy", nullable = false)
    private User modifiedBy;
    private Instant modifiedAt;
    @ManyToOne
    @JoinColumn(name = "assignedTo", nullable = false)
    private User assignedTo;
    private Integer status;
    private Instant dueDate;
//    @OneToMany(mappedBy = "ticketId")
//    private List<Comment> comments;
}
