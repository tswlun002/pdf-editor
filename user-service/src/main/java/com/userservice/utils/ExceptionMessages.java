package com.userservice.utils;
import java.util.function.BiFunction;

public  class ExceptionMessages {
   public static final  String INVALID_RESOURCE="%s is invalid or null";
    public static final String RESOURCE_NOT_FOUND="%s is not found with %s: %s.";
    public static final String RESOURCE_EXIST = "%s already exists with %s: %s.";
    public  static  final BiFunction<String,String[],String> FORMAT_EXCEPTION_MESSAGE= String::format;

}
