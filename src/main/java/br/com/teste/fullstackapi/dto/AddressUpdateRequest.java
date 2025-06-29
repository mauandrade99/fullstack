package br.com.teste.fullstackapi.dto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@SuppressWarnings("unused")
public class AddressUpdateRequest {
    @NotEmpty @Size(min = 8, max = 8)
    private String cep;
    @NotEmpty
    private String numero;
    private String complemento;
    private Integer frontendOrigin; 
    // Getters e Setters

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public Integer getFrontendOrigin() { return frontendOrigin; }
    public void setFrontendOrigin(Integer frontendOrigin) { this.frontendOrigin = frontendOrigin; } 
}