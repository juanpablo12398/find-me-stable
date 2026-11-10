package edu.utn.proyecto.infrastructure.adapters.out.persistence.postgresql;

import edu.utn.proyecto.domain.model.concreta.Desaparecido;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.entities.DesaparecidoEntity;
import edu.utn.proyecto.infrastructure.adapters.out.persistence.mappers.DesaparecidoPersistenceMapper;
import edu.utn.proyecto.infrastructure.ports.out.jpa.DesaparecidoJpaRepository;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesaparecidoRepositoryTest {

    @Mock
    private DesaparecidoJpaRepository jpa;
    @Mock
    private DesaparecidoPersistenceMapper mapper;
    @Mock
    private BlindIndexer blindIndexer;

    @InjectMocks
    private DesaparecidoRepository repo;

    @Test
    void save_conDniDebeGenerarHmac() {
        Desaparecido domain = new Desaparecido();
        domain.setDni("12345678");

        DesaparecidoEntity entity = new DesaparecidoEntity();
        DesaparecidoEntity savedEntity = new DesaparecidoEntity();
        Desaparecido mapped = new Desaparecido();

        when(mapper.domainToEntity(domain)).thenReturn(entity);
        when(blindIndexer.index("12345678")).thenReturn("dni-hmac");
        when(jpa.save(entity)).thenReturn(savedEntity);
        when(mapper.entityToDomain(savedEntity)).thenReturn(mapped);

        Desaparecido result = repo.save(domain);

        assertThat(entity.getDniHmac()).isEqualTo("dni-hmac");
        assertThat(result).isSameAs(mapped);
    }

    @Test
    void save_sinDniNoDebeGenerarHmac() {
        Desaparecido domain = new Desaparecido();
        domain.setDni(null);
        DesaparecidoEntity entity = new DesaparecidoEntity();

        when(mapper.domainToEntity(domain)).thenReturn(entity);
        when(jpa.save(entity)).thenReturn(entity);
        when(mapper.entityToDomain(entity)).thenReturn(domain);

        repo.save(domain);

        verify(blindIndexer, never()).index(any());
        assertThat(entity.getDniHmac()).isNull();
    }

    @Test
    void contar_siJpaDevuelveNullDebeRetornarCero() {
        when(jpa.contar("abc", "BUSCANDO")).thenReturn(null);

        long total = repo.contar("abc", "BUSCANDO");

        assertThat(total).isZero();
    }

    @Test
    void buscarPorDniExacto_debeUsarBlindIndex() {
        DesaparecidoEntity entity = new DesaparecidoEntity();
        Desaparecido domain = new Desaparecido();

        when(blindIndexer.index("12345678")).thenReturn("dni-hmac");
        when(jpa.buscarPorDniExacto("dni-hmac", "BUSCANDO")).thenReturn(List.of(entity));
        when(mapper.entityToDomain(entity)).thenReturn(domain);

        List<Desaparecido> result = repo.buscarPorDniExacto("12345678", "BUSCANDO");

        assertThat(result).containsExactly(domain);
        verify(jpa).buscarPorDniExacto("dni-hmac", "BUSCANDO");
    }

    @Test
    void findByIds_vacioDebeRetornarListaVacia() {
        List<Desaparecido> result = repo.findByIds(List.of());

        assertThat(result).isEmpty();
        verifyNoInteractions(jpa, mapper);
    }

    @Test
    void findByIds_conDatosDebeMapearResultados() {
        UUID id = UUID.randomUUID();
        DesaparecidoEntity entity = new DesaparecidoEntity();
        Desaparecido domain = new Desaparecido();

        when(jpa.findAllById(List.of(id))).thenReturn(List.of(entity));
        when(mapper.entityToDomain(entity)).thenReturn(domain);

        List<Desaparecido> result = repo.findByIds(List.of(id));

        assertThat(result).containsExactly(domain);
    }

    @Test
    void findById_debeMapearOptional() {
        UUID id = UUID.randomUUID();
        DesaparecidoEntity entity = new DesaparecidoEntity();
        Desaparecido domain = new Desaparecido();

        when(jpa.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.entityToDomain(entity)).thenReturn(domain);

        Optional<Desaparecido> result = repo.findById(id);

        assertThat(result).contains(domain);
    }

    @Test
    void findByDni_debeUsarBlindIndexParaBuscarPorHmac() {
        DesaparecidoEntity entity = new DesaparecidoEntity();
        Desaparecido domain = new Desaparecido();
        domain.setDni("12345678");

        when(blindIndexer.index("12345678")).thenReturn("dni-hmac-value");
        when(jpa.findByDniHmac("dni-hmac-value")).thenReturn(Optional.of(entity));
        when(mapper.entityToDomain(entity)).thenReturn(domain);

        Optional<Desaparecido> result = repo.findByDni("12345678");

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
}



