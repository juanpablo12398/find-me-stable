package edu.utn.proyecto.common.validation.concreta.avistamiento.rules;

import edu.utn.proyecto.common.exception.DomainException;
import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import edu.utn.proyecto.infrastructure.ports.out.IRepoDeDesaparecidos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesaparecidoExistsRuleTest {

    @Mock
    private IRepoDeDesaparecidos repo;

    @InjectMocks
    private DesaparecidoExistsRule rule;

    @Test
    void check_desaparecidoExists_ok() {
        UUID id = UUID.randomUUID();
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setDesaparecidoId(id.toString());

        when(repo.findById(id)).thenReturn(Optional.of(new Desaparecido()));

        assertThatCode(() -> rule.check(dto)).doesNotThrowAnyException();
    }

    @Test
    void check_desaparecidoNotFound_lanzaExcepcion() {
        UUID id = UUID.randomUUID();
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setDesaparecidoId(id.toString());

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistamiento.desaparecido.notfound");
    }

    @Test
    void check_desaparecidoIdNull_lanzaExcepcion() {
        AvistamientoRequestDTO dto = new AvistamientoRequestDTO();
        dto.setDesaparecidoId(null);

        assertThatThrownBy(() -> rule.check(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("avistamiento.desaparecido.notfound");
    }
}
