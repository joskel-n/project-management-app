package com.projectmanagement.service;

import com.projectmanagement.dto.ClientRequest;
import com.projectmanagement.model.Client;
import com.projectmanagement.repository.ClientRepository;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.BadRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;
    
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }
    
    @Transactional
    public Client createClient(ClientRequest clientRequest) {
        // Check if email is already in use
        if (clientRepository.existsByEmail(clientRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }
        
        Client client = new Client();
        mapClientRequestToClient(clientRequest, client);
        
        return clientRepository.save(client);
    }
    
    @Transactional
    public Client updateClient(Long id, ClientRequest clientRequest) {
        Client existingClient = getClientById(id);
        
        // Check if email is already in use by another client
        if (!existingClient.getEmail().equals(clientRequest.getEmail()) && 
            clientRepository.existsByEmailAndIdNot(clientRequest.getEmail(), id)) {
            throw new BadRequestException("Email is already in use by another client");
        }
        
        mapClientRequestToClient(clientRequest, existingClient);
        
        return clientRepository.save(existingClient);
    }
    
    @Transactional
    public void deleteClient(Long id) {
        Client client = getClientById(id);
        clientRepository.delete(client);
    }
    
    private void mapClientRequestToClient(ClientRequest request, Client client) {
        client.setName(request.getName());
        client.setContactPerson(request.getContactPerson());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());
        client.setDescription(request.getDescription());
    }
}