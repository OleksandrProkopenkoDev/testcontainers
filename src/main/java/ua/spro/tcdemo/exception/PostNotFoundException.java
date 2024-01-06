package ua.spro.tcdemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException{

  private static final String MESSAGE = "Post with id = %s not found";

  public PostNotFoundException(Integer id) {
    super(MESSAGE.formatted(id));
  }
}
