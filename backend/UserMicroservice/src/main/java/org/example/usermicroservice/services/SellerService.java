package org.example.usermicroservice.services;

import org.example.usermicroservice.dtos.DeleteImageDTO;
import org.example.usermicroservice.dtos.DeleteUserDTO;
import org.example.usermicroservice.dtos.SellerDTO;
import org.example.usermicroservice.dtos.mapper.SellerMapper;
import org.example.usermicroservice.entities.Seller;
import org.example.usermicroservice.messaging.producer.DeleteImageProducer;
import org.example.usermicroservice.messaging.producer.DeleteUserProducer;
import org.example.usermicroservice.repositories.SellerRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SellerService {
    private final SellerRepository sellerRepository;
    private final DeleteUserProducer deleteUserProducer;
    private final DeleteImageProducer deleteImageProducer;

    public SellerService(SellerRepository sellerRepository, DeleteUserProducer deleteUserProducer, DeleteImageProducer deleteImageProducer) {
        this.sellerRepository = sellerRepository;
        this.deleteUserProducer = deleteUserProducer;
        this.deleteImageProducer = deleteImageProducer;
    }

    public void saveSellerProfile(Seller seller) {
        sellerRepository.save(seller);
    }

    public SellerDTO getSellerProfile(UUID id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));
        return SellerMapper.toDTO(seller);
    }

    public SellerDTO updateSellerProfile(UUID id, SellerDTO dto) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));

        String oldImageName = seller.getImageName();
        String newImageName = dto.getImageName();
        
        SellerMapper.updateEntityFromDTO(dto, seller);
        Seller updatedSeller = sellerRepository.save(seller);
        
        if (oldImageName != null && !oldImageName.isEmpty() &&
            (newImageName == null || !newImageName.equals(oldImageName))) {
            DeleteImageDTO deleteImageMessage = new DeleteImageDTO(oldImageName);
            deleteImageProducer.sendDeleteImageMessage(deleteImageMessage);
        }
        
        return SellerMapper.toDTO(updatedSeller);
    }

    public void deleteSellerProfile(UUID id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));
        
        String imageName = seller.getImageName();
        
        sellerRepository.deleteById(id);
        
        DeleteUserDTO deleteMessage = new DeleteUserDTO(id);
        deleteUserProducer.sendDeleteUserMessage(deleteMessage);
        
        if (imageName != null && !imageName.isEmpty()) {
            DeleteImageDTO deleteImageMessage = new DeleteImageDTO(imageName);
            deleteImageProducer.sendDeleteImageMessage(deleteImageMessage);
        }
    }
}