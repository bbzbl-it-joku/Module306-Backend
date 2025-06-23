package ch.bbzbl_it.module_306_backend.repository;


import ch.bbzbl_it.module_306_backend.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findAttachmentsByLinkType(String linkType);

    List<Attachment> findAttachmentsByLinkTypeAndLinkId(String linkType, Long linkId);

    @Query("SELECT a FROM Attachment a WHERE (a.linkType like 'TICKET' AND a.linkId=:ticketId) OR (a.linkType like 'COMMENT' AND a.linkId in :attachmentIds)")
    List<Attachment> findAllAttachmentsForTicket(@Param("ticketId") Long ticketId, @Param("attachmentIds") Long[] attachmentIds);

}
