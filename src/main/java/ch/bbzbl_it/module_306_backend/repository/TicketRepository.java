package ch.bbzbl_it.module_306_backend.repository;

import ch.bbzbl_it.module_306_backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
