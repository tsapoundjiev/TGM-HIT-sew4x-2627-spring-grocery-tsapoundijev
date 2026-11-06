package at.ac.tgm.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Zentrale Fehlerbehandlung: wandelt Exceptions aus Controller und Service in HTTP-Antworten um.
 * Die Fehlermeldung wird als {@code text/plain} im Body geliefert (keine Stacktraces).
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * Validierungsfehler im Request Body (z.B. name leer) -> 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handle(MethodArgumentNotValidException e) {
        List<String> list = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            list.add(error.getField() + " " + error.getDefaultMessage());
        }
        String body = String.join("\n", list);
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(body);
    }

    /**
     * Ungültige Anfrage, z.B. id != null bei POST bzw. id == null bei PUT -> 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handler(IllegalArgumentException e) {
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
    }

    /**
     * Artikel nicht gefunden -> 404 Not Found.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handler(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
    }
}
