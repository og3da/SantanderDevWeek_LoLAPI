package me.dio.sdw24.adapters.in.exception;

import me.dio.sdw24.domain.exceptions.ChampionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ChampionNotFoundException.class)
    public ResponseEntity<ApiError> handleDomainException(ChampionNotFoundException domainError) {
        String errorMessage = domainError.getMessage();
        logger.error(errorMessage);
        return ResponseEntity.unprocessableEntity().body(new ApiError(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleDomainException(Exception unexpectedError) {
        String errorMessage = "Erro inesperado";
        logger.error(errorMessage, unexpectedError);
        return ResponseEntity.internalServerError().body(new ApiError(errorMessage));
    }

    public record ApiError(String message) {
    }
}
