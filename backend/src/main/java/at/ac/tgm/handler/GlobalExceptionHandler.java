package at.ac.tgm.handler;

import org.springframework.http.HttpHeaders;
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

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private HttpHeaders headers = new org.springframework.http.HttpHeaders();
    
    public GlobalExceptionHandler() {
        // Wäre sonst wegen @ResponseBody standardmäßig APPLICATION_JSON
        headers.setContentType(MediaType.TEXT_PLAIN);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handle(MethodArgumentNotValidException e) {
        List<String> list = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            list.add(error.getField() + " " + error.getDefaultMessage());
        }
        String body = String.join("\n", list);
        return new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Ungültige Anfrage, z.B. id != null bei POST bzw. id == null bei PUT.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handler(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handler(NoSuchElementException e) {
        String body = e.getMessage();
        return new ResponseEntity<>(body, headers, HttpStatus.NOT_FOUND);
    }
}

