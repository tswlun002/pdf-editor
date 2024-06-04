package com.documentservice.exception;

import java.io.InvalidClassException;

public class InvalidUser extends InvalidClassException {
    public InvalidUser(String invalidUserWasGiven) {super(invalidUserWasGiven);}
}
