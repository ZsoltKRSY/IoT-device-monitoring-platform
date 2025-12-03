package sd.users.handlers.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Date;

@Getter
@Setter
public class ExceptionHandlerResponseDTO {
    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String resource;
    private Collection<?> details;

    public ExceptionHandlerResponseDTO(String resource, String error, int status, String message, Collection<?> details, String path) {
        this.timestamp = new Date();
        this.resource = resource;
        this.error = error;
        this.status = status;
        this.message = message;
        this.details = details;
        this.path = path;
    }
}
