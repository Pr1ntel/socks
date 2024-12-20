package org.example.socks.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.socks.model.Socks;
import org.example.socks.repository.SocksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Service;
import java.util.Comparator;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Builder
@AllArgsConstructor
public class SocksService {
    private final SocksRepository socksRepository;
    private final Logger logger = LoggerFactory.getLogger(SocksService.class);

    public List<Socks> getAll() {
        logger.info("Получение всех носков");
        return socksRepository.findAll().stream().map(
                socks -> Socks.builder()
                        .id(socks.getId())
                        .color(socks.getColor())
                        .percentageOfCottonContent(socks.getPercentageOfCottonContent())
                        .quantity(socks.getQuantity())
                        .build()).collect(Collectors.toList());
    }
    public static Comparator<Socks> getSortingFunction(String sortField) {
        switch (sortField) {
            case "color":
                return Comparator.comparing(Socks::getColor);
            case "percentageOfCottonContent":
                return Comparator.comparingDouble(Socks::getPercentageOfCottonContent);
            case "quantity":
                return Comparator.comparingInt(Socks::getQuantity);
        }
        return null;
    }
    public List<Socks> getAllSocks(int minCottonPercent, int maxCottonPercent, String sortField) {
        logger.info("Получение всех носков с хлопком от {} до {}", minCottonPercent, maxCottonPercent);
        List<Socks> socksList = socksRepository.findAll().stream().filter(s -> s.getPercentageOfCottonContent() >= minCottonPercent && s.getPercentageOfCottonContent() <= maxCottonPercent).sorted(getSortingFunction(sortField)).collect(Collectors.toList());
        return socksList;
    }
    @Transactional
    public Socks increaseQuantity(Long id, Integer quantity) {
        logger.info("Увеличиваем количество носков с ID {} на {}", id, quantity);
        Socks socks = socksRepository.findById(id);
        if (socks == null) {
            throw new RuntimeException("Носки с таким id не найдены.");
        }

        if (socks.getQuantity() + quantity < 0) {
            throw new RuntimeException("Количество носков недостаточно для выполнения операции.");
        }

        socks.setQuantity(socks.getQuantity() + quantity);
        socksRepository.saveAndFlush(socks);
        return socks;
    }

    @Transactional
    public Socks decreaseQuantity(Long id, Integer quantity) {
        logger.info("Уменьшаем количество носков с ID {} на {}", id, quantity);
        Socks socks = socksRepository.findById(id);
        if (socks == null) {
            throw new RuntimeException("Носки с таким id не найдены.");
        }

        if (socks.getQuantity() - quantity < 0) {
            throw new RuntimeException("Количество носков недостаточно для выполнения операции.");
        }

        socks.setQuantity(socks.getQuantity() - quantity);
        socksRepository.saveAndFlush(socks);
        return socks;
    }

    @Transactional
    public Socks updateSocks(Long id, Socks requestBody) {
        logger.info("Обновляются носки с ID {}", id);
        Socks socks = socksRepository.findById(id);
        if (socks == null) {
            throw new RuntimeException("Носки с таким id не найдены.");
        }

        socks.setColor(requestBody.getColor());
        socks.setPercentageOfCottonContent(requestBody.getPercentageOfCottonContent());
        socks.setQuantity(requestBody.getQuantity());

        socksRepository.saveAndFlush(socks);
        return socks;
    }

    @Transactional
    public List<Socks> loadSocksFromFile(InputStream fileStream) throws IOException {
        logger.info("Начинается загрузка носков из файла");
        List<Socks> socksList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream))) {
            String line;

            while ((line = reader.readLine()) != null) {
                Socks socks = parseSockData(line);

                if (socks != null) {
                    socksList.add(socks);
                    socksRepository.save(socks); // Сохраняем объект в БД
                }
            }
        } finally {
            if (fileStream != null) {
                fileStream.close();
            }
        }

        logger.info("Загрузчик носков завершил работу. Загружено {} носков.", socksList.size());
        return socksList;
    }

    private Socks parseSockData(String data) {
        String[] parts = data.split(",");

        if (parts.length == 3) {
            Socks socks = new Socks();
            socks.setColor(parts[0].trim());
            socks.setPercentageOfCottonContent(Integer.parseInt(parts[1].trim()));
            socks.setQuantity(Integer.parseInt(parts[2].trim()));
            return socks;
        } else {
            logger.error("Ошибка: неверная структура строки.");
            return null;
        }
    }
}