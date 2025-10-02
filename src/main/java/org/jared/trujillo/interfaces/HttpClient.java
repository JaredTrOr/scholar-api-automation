package org.jared.trujillo.interfaces;

import org.jared.trujillo.classes.HttpSimpleResponse;
import org.jared.trujillo.exceptions.HttpException;

import java.util.Map;

public interface HttpClient {

    HttpSimpleResponse get(String url, Map<String, String> headers) throws HttpException;
    HttpSimpleResponse get(String url) throws HttpException;

}
