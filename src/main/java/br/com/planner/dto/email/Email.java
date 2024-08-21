package br.com.planner.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Email {

    private String from;
    private List<String> to;
    private String subject;
    private String body;
    private LocalDateTime startsAt;

    // CONFIRMAR PARTICIPACAO NA VIAGEM
    // localhost:5173/tripId=849387547890358974&token=ejjbb8jkdsfjkesruieyh7i -> para pagina da trip -> modal para confirmar trip ->, informar o email,
    // o token e o email, descriptografo o token, pego o valor dentro dele e comparo com o email enviado, se estiver correto, confirmTrip
    // localhost:5173/create?tripId=89453287904554698473

    //envia email -> esse participante ja é um owner? sim -> envia direto para a tela de confirmar participacao na trip com credencial
    // envia email -> esse participante ja é um owner? nao -> para ele confirmar, ele deve ser um owner? se sim, criar conta na aplicacao e confirm

}
