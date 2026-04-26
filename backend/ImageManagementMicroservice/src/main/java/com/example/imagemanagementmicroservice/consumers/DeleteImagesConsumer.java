package com.example.imagemanagementmicroservice.consumers;

import com.example.imagemanagementmicroservice.dto.ImageDeletionRequest;
import com.example.imagemanagementmicroservice.service.MinIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeleteImagesConsumer {
    private static final Logger log = LoggerFactory.getLogger(DeleteImagesConsumer.class);

    final private MinIOService minIOService;

    public DeleteImagesConsumer(MinIOService minIOService) {
        this.minIOService = minIOService;
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.deleteImages.topic}.${rabbitmq.sync.queue.unique.name}")
    public void handleImageDeletion(ImageDeletionRequest deletionRequest) {
        try {
            log.info("Received image deletion request with {} images",
                    deletionRequest.getImageNames().size()
            );
            if (deletionRequest.getImageNames() == null || deletionRequest.getImageNames().isEmpty()) {
                log.warn("Image deletion request contains no images");
                return;
            }

            // Delete each image from MinIO
            for (String imageName : deletionRequest.getImageNames()) {
                try {
                    minIOService.deleteImage(imageName);
                    log.debug("Successfully deleted image: {}", imageName);
                } catch (Exception e) {
                    log.error("Failed to delete image: {}", imageName, e);
                }
            }

            log.info("Completed image deletion request");
        } catch (Exception e) {
            log.error("Error processing image deletion request", e);
        }
    }
}
