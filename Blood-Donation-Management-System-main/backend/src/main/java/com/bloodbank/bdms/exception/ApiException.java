package com.bloodbank.bdms.exception;

public class ApiException extends RuntimeException {
  public ApiException(String message) {
    super(message);
  }
}
