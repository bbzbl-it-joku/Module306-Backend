package ch.bbzbl_it.module_306_backend.service;

import ch.bbzbl_it.module_306_backend.dto.AttachmentDTO;
import ch.bbzbl_it.module_306_backend.entity.Attachment;
import ch.bbzbl_it.module_306_backend.repository.AttachmentRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.messages.DeleteObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AttachmentService {

    @Value("${s3.url}")
    private String url;

    @Value("${s3.username}")
    private String username;

    @Value("${s3.password}")
    private String password;

    @Autowired
    private AttachmentRepository attachmentRepository;

    public InputStream getAttachment(Attachment attachment) throws Exception {
        try (
                MinioClient minioClient = MinioClient.builder()
                        .endpoint("https://" + url)
                        .credentials(username, password)
                        .build()
        ) {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(attachment.getLinkType().toLowerCase())
                            .object(attachment.getLinkId() + "/" + attachment.getUrl())
                            .build());
        }
    }

    public List<Attachment> saveAllAttachments(List<AttachmentDTO> attachments, MultipartFile[] fileArray, Long attachableId, String attachableType) throws Exception {
        try (
                MinioClient minioClient = MinioClient.builder()
                        .endpoint("https://" + url)
                        .credentials(username, password)
                        .build()
        ) {
            if (attachments == null || fileArray == null || attachableId == null || attachableType == null) {
                return null;
            }

            List<MultipartFile> files = Arrays.stream(fileArray).toList();
            List<Attachment> compareAttachments = attachmentRepository.findAttachmentsByLinkTypeAndLinkId(attachableType.toUpperCase(), attachableId);

            compareAttachments.removeIf(
                    attachment -> attachments
                            .stream()
                            .anyMatch(attachmentDTO -> attachment.getId().equals(attachmentDTO.getId()))
            );

            if (!compareAttachments.isEmpty()) {
                var leftoverAttachment = attachmentRepository.findAllById(compareAttachments.stream().map(Attachment::getId).toList());
                minioClient.removeObjects(
                        RemoveObjectsArgs.builder()
                                .bucket(attachableType.toLowerCase())
                                .objects(leftoverAttachment.stream().map(attachment -> new DeleteObject(attachment.getLinkId() + "/" + attachment.getUrl())).toList())
                                .build()
                );
                attachmentRepository.deleteAll(leftoverAttachment);
            }

            Set<Map.Entry<AttachmentDTO, Optional<MultipartFile>>> mappedAttachments = attachments.stream().collect(Collectors.toMap(
                    dto -> dto,
                    attachmentDTO -> files.stream().filter(file -> file.getName().equals(attachmentDTO.getName())).findFirst()
            )).entrySet();

            for (var attachmentEntry : mappedAttachments) {
                if (attachmentEntry.getValue().isPresent()) {
                    var attachment = AttachmentDTO.toAttachment(attachmentEntry.getKey());
                    MultipartFile file = attachmentEntry.getValue().get();
                    var response = minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(attachableType.toLowerCase())
                                    .object(attachment.getLinkId() + "/" + attachment.getUrl())
                                    .stream(file.getInputStream(), file.getSize(), -1)
                                    .contentType(file.getContentType())
                                    .build()
                    );
                    attachment.setId(null);
                    attachment.setLinkType(response.bucket().toUpperCase());
                    attachment.setLinkId(attachableId);

                    attachmentRepository.save(attachment);
                }
            }

            return compareAttachments;
        }

    }
}
