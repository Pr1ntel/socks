package org.example.socks.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.socks.model.Socks;
import org.example.socks.service.SocksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Tag(name="Controller-command", description = "HELLO WORLD")
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class SocksController {
    private final Logger logger = LoggerFactory.getLogger(SocksController.class);
    private final SocksService socksService;

    @Operation(method = "Get", summary = "Get All")
    @GetMapping(value = "/socks")
    public List<Socks> getAllSocks() {
        return socksService.getAll();
    }
    @GetMapping("/socks/cottonFilter")
    public List<Socks> getFilteredSocks(@RequestParam("minCottonPercent") int minCottonPercent, @RequestParam("maxCottonPercent") int maxCottonPercent, @RequestParam("sortField") String sortField) {
        return socksService.getAllSocks(minCottonPercent, maxCottonPercent, sortField);
    }
    @PostMapping("/income")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Socks> increaseQuantity(@RequestBody Socks requestBody) {
        logger.info("Увеличение количества носков с ID {} на {}", requestBody.getId(), requestBody.getQuantity());
        Socks socks = socksService.increaseQuantity(requestBody.getId(), requestBody.getQuantity());
        return new ResponseEntity<>(socks, HttpStatus.OK);
    }

    @PostMapping("/outcome")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Socks> decreaseQuantity(@RequestBody Socks requestBody) {
        logger.info("Уменьшение количества носков с ID {} на {}", requestBody.getId(), requestBody.getQuantity());
        Socks socks = socksService.decreaseQuantity(requestBody.getId(), requestBody.getQuantity());
        return new ResponseEntity<>(socks, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Socks> updateSocks(@PathVariable("id") Long id, @RequestBody Socks requestBody) {
        logger.info("Обновление носков с ID {}", id);
        Socks socks = socksService.updateSocks(id, requestBody);
        return new ResponseEntity<>(socks, HttpStatus.OK);
    }


    @PostMapping(value = "/socks/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<Socks>> loadSocksFromFile(InputStream fileStream) throws IOException {
        logger.info("Начало загрузки носков из файла");
        List<Socks> socksList = socksService.loadSocksFromFile(fileStream);
        logger.info("Загружено {} носков", socksList.size());
        return new ResponseEntity<>(socksList, HttpStatus.CREATED);
    }
}
