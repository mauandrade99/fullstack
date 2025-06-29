package br.com.teste.fullstackapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.teste.fullstackapi.client.ViaCepClient;
import br.com.teste.fullstackapi.client.dto.ViaCepResponse;
import br.com.teste.fullstackapi.dto.AddressCreateRequest;
import br.com.teste.fullstackapi.model.Address;
import br.com.teste.fullstackapi.model.User;
import br.com.teste.fullstackapi.repository.AddressRepository;
import br.com.teste.fullstackapi.repository.UserRepository;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ViaCepClient viaCepClient;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository, ViaCepClient viaCepClient) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.viaCepClient = viaCepClient;
    }

    public Address createAddressForUser(Long userId, AddressCreateRequest request, Long performingUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!")); 


        ViaCepResponse viaCepResponse = viaCepClient.getEnderecoByCep(request.getCep());  
        

        if (viaCepResponse == null || viaCepResponse.isErro()) {
            throw new RuntimeException("CEP inválido ou não encontrado!");
        }
        
        Address newAddress = new Address();
        newAddress.setCep(viaCepResponse.getCep().replaceAll("-", "")); 
        newAddress.setLogradouro(viaCepResponse.getLogradouro());
        newAddress.setBairro(viaCepResponse.getBairro());
        newAddress.setCidade(viaCepResponse.getLocalidade());
        newAddress.setEstado(viaCepResponse.getUf());
        newAddress.setNumero(request.getNumero());
        newAddress.setComplemento(request.getComplemento()); 
        newAddress.setUsuario(user); 

        newAddress.setUserIdCreated(performingUserId);
        newAddress.setUserIdUpdated(performingUserId);

        newAddress.setFrontendOriginCreated(request.getFrontendOrigin());
        newAddress.setFrontendOriginUpdated(request.getFrontendOrigin());
       
        return addressRepository.save(newAddress);
    }

    public Address updateAddress(Long addressId, AddressCreateRequest request, Long performingUserId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado com o id: " + addressId));


        String newCep = request.getCep().replaceAll("-", "");
        if (!newCep.equals(address.getCep())) {

            ViaCepResponse viaCepResponse = viaCepClient.getEnderecoByCep(newCep);
            if (viaCepResponse == null || viaCepResponse.isErro()) {
                throw new RuntimeException("O novo CEP informado é inválido ou não foi encontrado!");
            }

            address.setCep(newCep);
            address.setLogradouro(viaCepResponse.getLogradouro());
            address.setBairro(viaCepResponse.getBairro());
            address.setCidade(viaCepResponse.getLocalidade());
            address.setEstado(viaCepResponse.getUf());
        }

        address.setNumero(request.getNumero());
        address.setComplemento(request.getComplemento());

        address.setUserIdUpdated(performingUserId);

        address.setFrontendOriginUpdated(request.getFrontendOrigin()); 

        return addressRepository.save(address);
    }

    public void deleteAddress(Long addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw new RuntimeException("Endereço não encontrado com o id: " + addressId);
        }
        addressRepository.deleteById(addressId);
    }

    public List<Address> findAddressesByUserId(Long userId) {
    if (!userRepository.existsById(userId)) {
        throw new RuntimeException("Usuário não encontrado!");
    }
    return addressRepository.findByUsuarioId(userId);
}
}