package ch.bbzbl_it.module_306_backend.dto;

import ch.bbzbl_it.module_306_backend.entity.Attachment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentDTO {

    private Long id;
    private String linkType;
    private Long linkId;
    private String name;
    private String mimeType;

    public static AttachmentDTO toDTO(Attachment attachment) {
        if (attachment == null) {
            return null;
        }

        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(attachment.getId());
        dto.setLinkType(attachment.getLinkType());
        dto.setLinkId(attachment.getLinkId());
        dto.setName(attachment.getUrl());
        dto.setMimeType(attachment.getMimeType());

        return dto;
    }

    public static Attachment toAttachment(AttachmentDTO dto) {
        if (dto == null) {
            return null;
        }

        Attachment attachment = new Attachment();
        attachment.setId(dto.getId());
        attachment.setLinkType(dto.getLinkType());
        attachment.setLinkId(dto.getLinkId());
        attachment.setUrl(dto.getName());
        attachment.setMimeType(dto.getMimeType());

        return attachment;
    }

}
