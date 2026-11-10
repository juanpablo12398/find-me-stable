package edu.utn.proyecto.infrastructure.adapters.out.persistence.postgresql;

import edu.utn.proyecto.domain.model.concreta.Avistador;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.AvistadorEntity;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.mappers.AvistadorPersistenceMapper;
import edu.utn.proyecto.infrastructure.ports.out.jpa.AvistadorJpaRepository;
import edu.utn.proyecto.security.encrypt.BlindIndexer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvistadorRepositoryTest {

    @Mock
    private AvistadorJpaRepository jpa;
    @Mock
    private AvistadorPersistenceMapper mapper;
    @Mock
    private BlindIndexer blindIndexer;

    @InjectMocks
    private AvistadorRepository repo;

    @Test
    void save_conEmailDebeGenerarDosIndices() {
        Avistador domain = new Avistador();
        domain.setDni("12345678");
        domain.setEmail("mail@test.com");
        AvistadorEntity entity = new AvistadorEntity();

        when(mapper.domainToEntity(domain)).thenReturn(entity);
        when(blindIndexer.index("12345678")).thenReturn("dni-hmac");
        when(blindIndexer.index("mail@test.com")).thenReturn("mail-hmac");
        when(jpa.save(entity)).thenReturn(entity);
        when(mapper.entityToDomain(entity)).thenReturn(domain);

        repo.save(domain);

        assertThat(entity.getDniHmac()).isEqualTo("dni-hmac");
        assertThat(entity.getEmailHmac()).isEqualTo("mail-hmac");
    }

    @Test
    void save_sinEmailNoDebeGenerarIndiceDeEmail() {
        Avistador domain = new Avistador();
        domain.setDni("12345678");
        domain.setEmail(null);
        AvistadorEntity entity = new AvistadorEntity();

        when(mapper.domainToEntity(domain)).thenReturn(entity);
        when(blindIndexer.index("12345678")).thenReturn("dni-hmac");
        when(jpa.save(entity)).thenReturn(entity);
        when(mapper.entityToDomain(entity)).thenReturn(domain);

        repo.save(domain);

        verify(blindIndexer, never()).index((String) null);
        assertThat(entity.getDniHmac()).isEqualTo("dni-hmac");
        assertThat(entity.getEmailHmac()).isNull();
    }

    @Test
    void findByIds_vacioDebeRetornarListaVacia() {
        List<Avistador> result = repo.findByIds(List.of());

        assertThat(result).isEmpty();
        verifyNoInteractions(jpa, mapper);
    }

    @Test
    void findByIds_conDatosDebeMapear() {
        UUID id = UUID.randomUUID();
        AvistadorEntity entity = new AvistadorEntity();
        Avistador domain = new Avistador();

        when(jpa.findAllById(List.of(id))).thenReturn(List.of(entity));
        when(mapper.entityToDomain(entity)).thenReturn(domain);

        List<Avistador> result = repo.findByIds(List.of(id));

        assertThat(result).containsExactly(domain);
    }

    @Test
    void findById_debeMapearOptional() {
        UUID id = UUID.randomUUID();
        AvistadorEntity entity = new AvistadorEntity();
        Avistador domain = new Avistador();

        when(jpa.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.entityToDomain(entity)).thenReturn(domain);

        Optional<Avistador> result = repo.findById(id);

        assertThat(result).contains(domain);
    }

    @Test
    void findByDni_debeUsarBlindIndexParaBuscarPorHmac() {
        AvistadorEntity entity = new AvistadorEntity();
        Avistador domain = new Avistador();
        domain.setDni("12345678");

        when(blindIndexer.index("12345678")).thenReturn("dni-hmac-value");
        when(jpa.findByDniHmac("dni-hmac-value")).thenReturn(Optional.of(entity));
        when(mapper.entityToDomain(entity)).thenReturn(domain);

        Optional<Avistador> result = repo.findByDni("12345678");

        assertThat(result).isPresent();
        assertThat(result.get().getDni()).isEqualTo("12345678");
    }

    @Test
    void existsByDni_debeUsarBlindIndexParaVerificarExistencia() {
        when(blindIndexer.index("12345678")).thenReturn("dni-hmac-value");
        when(jpa.existsByDniHmac("dni-hmac-value")).thenReturn(true);

        boolean exists = repo.existsByDni("12345678");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByDni_retornaFalseSiNoExiste() {
        when(blindIndexer.index("99999999")).thenReturn("otro-hmac-value");
        when(jpa.existsByDniHmac("otro-hmac-value")).thenReturn(false);

        boolean exists = repo.existsByDni("99999999");

        assertThat(exists).isFalse();
    }
}



