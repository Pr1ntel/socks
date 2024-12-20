package org.example.socks.service_test;

import org.example.socks.model.Socks;
import org.example.socks.repository.SocksRepository;
import org.example.socks.service.SocksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @Mock
    private SocksRepository socksRepository;

    @InjectMocks
    private SocksService socksService;

    @BeforeEach
    void setup() {

    }

    @Test
    void testDecreaseQuantitySuccess() {
        Long id = 1L;
        Integer quantity = 5;
        Socks existingSock = Socks.builder().id(id).color("black").percentageOfCottonContent(70).quantity(10).build();
        given(socksRepository.findById(anyLong())).willReturn(existingSock);

        Socks updatedSock = socksService.decreaseQuantity(id, quantity);

        verify(socksRepository).saveAndFlush(updatedSock);
        assertThat(updatedSock.getQuantity()).isEqualTo(5);
    }

    @Test
    void testDecreaseQuantityNotFound() {
        Long id = 99L;
        Integer quantity = 5;
        given(socksRepository.findById(anyLong())).willReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> socksService.decreaseQuantity(id, quantity));
        assertEquals("Носки с таким id не найдены.", exception.getMessage());
    }

    @Test
    void testDecreaseQuantityInsufficientQuantity() {
        Long id = 1L;
        Integer quantity = 11;
        Socks existingSock = Socks.builder().id(id).color("black").percentageOfCottonContent(70).quantity(10).build();
        given(socksRepository.findById(anyLong())).willReturn(existingSock);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> socksService.decreaseQuantity(id, quantity));
        assertEquals("Количество носков недостаточно для выполнения операции.", exception.getMessage());
    }
    @Test
    void testGetAll() {
        List<Socks> expectedSocks = Arrays.asList(
                Socks.builder().id(1L).color("black").percentageOfCottonContent(70).quantity(10).build(),
                Socks.builder().id(2L).color("white").percentageOfCottonContent(90).quantity(20).build()
        );
        when(socksRepository.findAll()).thenReturn(expectedSocks);

        List<Socks> actualSocks = socksService.getAll();

        assertThat(actualSocks).isEqualTo(expectedSocks);
    }
}
