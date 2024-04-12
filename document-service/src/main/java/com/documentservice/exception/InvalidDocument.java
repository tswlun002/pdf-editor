package com.documentservice.exception;

import java.io.InvalidClassException;

public class InvalidDocument extends InvalidClassException {
    public InvalidDocument(String s) {
        super(s);
    }
}
